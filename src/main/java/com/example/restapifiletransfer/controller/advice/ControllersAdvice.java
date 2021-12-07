package com.example.restapifiletransfer.controller.advice;

import com.example.restapifiletransfer.exception.CompressionException;
import com.example.restapifiletransfer.exception.DecompressionException;
import com.example.restapifiletransfer.exception.S3OperationException;
import com.example.restapifiletransfer.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestControllerAdvice
@ResponseBody
public class ControllersAdvice {

    private static final Logger log = LoggerFactory.getLogger(ControllersAdvice.class);
    private static final String UTC = "UTC";

    @ExceptionHandler(CompressionException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleCompressionException(Exception e, HttpServletRequest request) {
        log.error(e.getMessage(), e.getCause());

        return new ErrorMessage(e, request);
    }

    @ExceptionHandler(DecompressionException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleDecompressionException(Exception e, HttpServletRequest request) {
        log.error(e.getMessage(), e.getCause());

        return new ErrorMessage(e, request);
    }

    @ExceptionHandler(S3OperationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleS3OperationException(Exception e, HttpServletRequest request) {
        log.error(e.getMessage(), e.getCause());

        return new ErrorMessage(e, request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorMessage handleUnauthorizedException(Exception e, HttpServletRequest request) {
        log.error(e.getMessage(), e.getCause());

        return new ErrorMessage(e, request);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleUnknownException(Exception e, HttpServletRequest request) {
        log.error(e.getMessage(), e.getCause());

        return new ErrorMessage("Unexpected error occurred", request);
    }


    public static class ErrorMessage {

        private String message;
        private String path;
        private String httpMethod;
        private ZonedDateTime when;

        public ErrorMessage(Exception e, HttpServletRequest request) {
            this(e.getMessage(), request);
        }

        public ErrorMessage(String messsage, HttpServletRequest request) {
            this.message = messsage;
            this.path = request.getRequestURI();
            this.httpMethod = request.getMethod();
            this.when = ZonedDateTime.now(ZoneId.of(UTC));
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getHttpMethod() {
            return httpMethod;
        }

        public void setHttpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
        }

        public ZonedDateTime getWhen() {
            return when;
        }

        public void setWhen(ZonedDateTime when) {
            this.when = when;
        }
    }

}
