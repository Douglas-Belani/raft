package com.example.restapifiletransfer.controller;

import com.example.restapifiletransfer.model.File;
import com.example.restapifiletransfer.service.S3Service;
import com.example.restapifiletransfer.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    private static final String DOWNLOAD_CONTEXT_PATH = "/api/v1/files/download";
    private static final String UPLOAD_CONTEXT_PATH = "/api/v1/files/upload";

    private final S3Service s3Service;

    public FileController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping(value = "/download/**")
    public ResponseEntity<byte[]> handleFileDownload(JwtAuthenticationToken token,
                                                     HttpServletRequest request) {

        String filePath = RequestUtil.extractFilePathWithoutTrailingSlash(DOWNLOAD_CONTEXT_PATH, request); // removes trailing slash
        String username = RequestUtil.extractUsernameFromAccessToken(token);

        log.info("User {} sent download request for file {}", username, filePath);

        File file = s3Service.downloadFileFromS3(filePath, username);
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, file.getMimeType());
        headers.add(HttpHeaders.CONTENT_ENCODING, file.getCharset());

        return new ResponseEntity<>(file.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping(value = "/upload/**", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void handleFileUpload(@RequestParam("file") MultipartFile file,
                            JwtAuthenticationToken token,
                            HttpServletRequest request) {

        String filePath = RequestUtil.extractFilePath(UPLOAD_CONTEXT_PATH, request);
        String username = RequestUtil.extractUsernameFromAccessToken(token);
        log.info("User {} uploaded file {} to {}", username, file.getOriginalFilename(), filePath + file.getOriginalFilename());

        Charset charset = Charset.forName(request.getCharacterEncoding());
        s3Service.uploadFileToS3(file, filePath, username, charset);

        log.info("Successfully stored user {} file {}", username, file.getOriginalFilename());
    }

    @DeleteMapping(value = "/delete/**")
    public void handleFileDeletion(JwtAuthenticationToken token,
                                   HttpServletRequest request) {

        String filePath = RequestUtil.extractFilePathWithoutTrailingSlash(UPLOAD_CONTEXT_PATH, request);
        String username = RequestUtil.extractUsernameFromAccessToken(token);

        log.info("User {} sent deletion request for file {}", username, filePath);
        s3Service.deleteFileInS3(filePath, username);

        log.info("Successfully deleted file {} for user {}", filePath, username);
    }

}
