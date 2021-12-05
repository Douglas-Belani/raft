package com.example.restapifiletransfer.util;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil {

    private static final String USERNAME_CLAIM = "username";

    private RequestUtil(){

    }

    public static String extractFilePath(String webContext, HttpServletRequest request) {
        String filePath = request.getRequestURI();
        filePath = filePath.replace(webContext, "");
        return filePath.endsWith("/") ? filePath : filePath + "/";
    }

    public static String extractFilePathWithoutTrailingSlash(String webContext, HttpServletRequest request) {
        String filePath = extractFilePath(webContext, request);
        return filePath.substring(0, filePath.length() -1);
    }

    public static String extractUsernameFromAccessToken(JwtAuthenticationToken token) {
        return (String) token.getTokenAttributes().get(USERNAME_CLAIM);
    }

}
