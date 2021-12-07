package com.example.restapifiletransfer.controller.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.MimeTypeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FailedAuthenticationAdvice implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(FailedAuthenticationAdvice.class);

    private final ObjectMapper objectMapper;

    public FailedAuthenticationAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        log.error(e.getMessage(), e.getCause());

        ControllersAdvice.ErrorMessage errorMessage = new ControllersAdvice.ErrorMessage(e, request);
        String jsonErrorMessage = objectMapper.writeValueAsString(errorMessage);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MimeTypeUtils.APPLICATION_JSON.toString());
        response.getOutputStream().print(jsonErrorMessage);
    }
}
