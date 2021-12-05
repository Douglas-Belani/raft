package com.example.restapifiletransfer.service.impl;

import com.example.restapifiletransfer.exception.*;
import com.example.restapifiletransfer.model.File;
import com.example.restapifiletransfer.model.FileMetadata;
import com.example.restapifiletransfer.model.HomeFolder;
import com.example.restapifiletransfer.service.FileCompressorService;
import com.example.restapifiletransfer.service.HomeFolderService;
import com.example.restapifiletransfer.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class S3ServiceImpl implements S3Service {

    private static final Logger log = LoggerFactory.getLogger(S3ServiceImpl.class);
    private static final String OWNER = "owner";
    private static final String CHARSET = "charset";
    private static final String MIME_TYPE = "mime-type";
    private static final String ORIGINAL_SIZE = "original-size";

    @Value("${rest.api.file.transfer.aws.s3.bucket}")
    private String bucketName;

    private final S3Client s3Client;
    private final HomeFolderService homeFolderService;
    private final FileCompressorService fileCompressorService;

    public S3ServiceImpl(S3Client s3Client,
                         HomeFolderService homeFolderService,
                         FileCompressorService fileCompressorService) {
        this.s3Client = s3Client;
        this.homeFolderService = homeFolderService;
        this.fileCompressorService = fileCompressorService;
    }

    @Override
    public File downloadFileFromS3(String filePath, String username) {
        HomeFolder homeFolder = homeFolderService.findHomeFolder(username);

        String objectKey = homeFolder.getFolder() + filePath;
        log.info("Object key {}", objectKey);
        GetObjectRequest getObjectRequest = GetObjectRequest
                .builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(getObjectRequest);
        GetObjectResponse getObjectResponse = responseInputStream.response();
        Map<String, String> metadata = getObjectResponse.metadata();

        String owner = metadata.get(OWNER);

        if (!owner.equals(homeFolder.getUsername())) {
            throw new UnauthorizedException(String.format("User %s downloaded file %s, which belongs to %s",
                    username, filePath, owner));
        }

        File file = new File();
        file.setMimeType(metadata.get(MIME_TYPE));
        file.setCharset(metadata.get(CHARSET));

        try {
            byte[] decompressedContent = fileCompressorService.decompress(responseInputStream.readAllBytes());
            file.setContent(decompressedContent);
            responseInputStream.close();

            return file;

        } catch (IOException e) {
            throw new DownloadException(
                    String.format("Error downloading file %s requested by user %s", filePath, username), e);
        }
    }

    @Override
    public void uploadFileToS3(MultipartFile uploadedFile, String filePath, String username, Charset charset) {
        HomeFolder homeFolder = homeFolderService.findHomeFolder(username);

        Map<String, String> metadata = new HashMap<>();
        metadata.put(OWNER, homeFolder.getUsername());
        metadata.put(CHARSET, charset.name());
        metadata.put(MIME_TYPE, uploadedFile.getContentType());
        metadata.put(ORIGINAL_SIZE, String.valueOf(uploadedFile.getSize()));

        String objectKey = homeFolder.getFolder() + filePath + uploadedFile.getOriginalFilename();
        log.info("Uploading file {} to S3", objectKey);

        PutObjectRequest putObjectRequest = PutObjectRequest
                .builder()
                .bucket(bucketName)
                .key(objectKey)
                .metadata(metadata)
                .build();

        byte[] compressedContent = fileCompressorService.compress(uploadedFile);

        try {
            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(compressedContent));
            SdkHttpResponse sdkHttpResponse = putObjectResponse.sdkHttpResponse();

            if (!sdkHttpResponse.isSuccessful()) {
                throw new UploadException(String.format("Error uploading file %s to S3. Http Response Code %s",
                        uploadedFile.getOriginalFilename(), putObjectResponse.sdkHttpResponse().statusCode()));
            }

        } catch (AwsServiceException | SdkClientException e) {
            throw new UploadException("Error uploading file " + uploadedFile.getOriginalFilename() + " to S3", e);
        }
    }

    public void createFolderInS3(String folder, String username) {
        HomeFolder homeFolder = homeFolderService.findHomeFolder(username);

        String objectKey = homeFolder.getFolder() + folder;
        log.info("Uploading folder {} to S3", objectKey);

        PutObjectRequest putObjectRequest = PutObjectRequest
                .builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        try {
            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, RequestBody.empty());
            SdkHttpResponse sdkHttpResponse = putObjectResponse.sdkHttpResponse();

            if (!sdkHttpResponse.isSuccessful()) {
                throw new FolderCreationException(String.format("Error creating folder %s in S3. Http Response Code %s",
                        folder, sdkHttpResponse.statusCode()));
            }
        } catch (AwsServiceException | SdkClientException e) {
            throw new FolderCreationException(String.format("Error creating folder %s in S3", folder), e);
        }
    }

    @Override
    public List<FileMetadata> listObjectsInS3UserFolder(String filePath, String username) {
        HomeFolder homeFolder = homeFolderService.findHomeFolder(username);

        String objectKey = homeFolder.getFolder() + filePath;

        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request
                .builder()
                .bucket(bucketName)
                .delimiter("/")
                .prefix(objectKey)
                .build();

        try {
            ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);

            List<FileMetadata> fileMetadataList = new ArrayList<>();

            if (listObjectsV2Response.hasContents()) {
                listObjectsV2Response
                        .contents()
                        .forEach(s3Object -> {
                            FileMetadata fileMetadata = new FileMetadata();
                            fileMetadata.setFilename(s3Object.key().replace(objectKey, ""));
                            fileMetadata.setFolder(false);
                            fileMetadata.setSize(s3Object.size());
                            fileMetadata.setLastModified(s3Object.lastModified());

                            fileMetadataList.add(fileMetadata);
                        });
            }

            if (listObjectsV2Response.hasCommonPrefixes()) {
                listObjectsV2Response
                        .commonPrefixes()
                        .forEach(commonPrefix -> {
                            FileMetadata fileMetadata = new FileMetadata();
                            fileMetadata.setFilename(commonPrefix.prefix().replace(objectKey, ""));
                            fileMetadata.setFolder(true);
                            fileMetadata.setSize(0L);
                            fileMetadata.setLastModified(null);

                            fileMetadataList.add(fileMetadata);
                        });
            }

            return fileMetadataList;

        } catch (AwsServiceException | SdkClientException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void deleteFileInS3(String filePath, String username) {
        HomeFolder homeFolder = homeFolderService.findHomeFolder(username);

        String objectKey = homeFolder.getFolder() + filePath;
        log.info("Object key {}", objectKey);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest
                .builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        DeleteObjectResponse response = s3Client.deleteObject(deleteObjectRequest);

        if (!response.sdkHttpResponse().isSuccessful()) {
            throw new FileDeletionException(String.format("Error deleting file %s requested by user %s", filePath, username));
        }
    }
}
