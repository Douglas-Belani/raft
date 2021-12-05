package com.example.restapifiletransfer.controller;

import com.example.restapifiletransfer.model.FileMetadata;
import com.example.restapifiletransfer.service.S3Service;
import com.example.restapifiletransfer.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1/metadata")
public class MetadataController {

    private static final Logger log = LoggerFactory.getLogger(MetadataController.class);
    private static final String METADATA_WEB_CONTEXT = "/api/v1/metadata";

    private final S3Service s3Service;

    public MetadataController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping("/**")
    public ResponseEntity<List<FileMetadata>> handleMetadataRequest(JwtAuthenticationToken token,
                                                                    HttpServletRequest request) {

        String path = RequestUtil.extractFilePath(METADATA_WEB_CONTEXT, request);
        String username = RequestUtil.extractUsernameFromAccessToken(token);
        log.info("Handling metadata request for user {} with path {}", username, path);

        List<FileMetadata> fileMetadataList = s3Service.listObjectsInS3UserFolder(path, username);
        return new ResponseEntity<>(fileMetadataList, HttpStatus.OK);
    }

}
