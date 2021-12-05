package com.example.restapifiletransfer.service;

import com.example.restapifiletransfer.model.File;
import com.example.restapifiletransfer.model.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.Charset;
import java.util.List;

public interface S3Service {

    public abstract File downloadFileFromS3(String filePath, String email);

    public abstract void uploadFileToS3(MultipartFile uploadedFile, String filePath,String email, Charset charset);

    public abstract void createFolderInS3(String folder, String email);

    public abstract void deleteFileInS3(String folder, String email);

    public List<FileMetadata> listObjectsInS3UserFolder(String filePath, String email);

}
