package com.example.restapifiletransfer.controller;

import com.example.restapifiletransfer.service.S3Service;
import com.example.restapifiletransfer.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/folders")
public class FolderController {

    private static final Logger log = LoggerFactory.getLogger(FolderController.class);
    private static final String FOLDER_CREATION_CONTEXT_PATH = "/api/v1/folders/creation";
    private static final String FOLDER_DELETION_CONTEXT_PATH = "/api/v1/folders/delete";

    private final S3Service s3Service;

    public FolderController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/creation/**")
    public void handleFolderCreation(JwtAuthenticationToken token,
                                     HttpServletRequest request) {

        String folderPath = RequestUtil.extractFilePath(FOLDER_CREATION_CONTEXT_PATH, request);
        String username = RequestUtil.extractUsernameFromAccessToken(token);

        log.info("Creating folder {} for user {}", folderPath, username);

        s3Service.createFolderInS3(folderPath, username);
        log.info("User {} folder {} created successfully", username, folderPath);
    }

    @DeleteMapping("/delete/**")
    public void handleFolderDeletion(JwtAuthenticationToken token,
                                     HttpServletRequest request) {

        String folderPath = RequestUtil.extractFilePath(FOLDER_DELETION_CONTEXT_PATH, request);
        String username = RequestUtil.extractUsernameFromAccessToken(token);

        log.info("Deleting folder {} for user {}", folderPath, username);

        s3Service.deleteFileInS3(folderPath, username);
        log.info("User {} folder {} deleted successfully", username, folderPath);
    }

}
