package com.example.restapifiletransfer.exception;

public class UploadException extends StandardException {

    public UploadException(String msg) {
        super(msg);
    }

    public UploadException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
