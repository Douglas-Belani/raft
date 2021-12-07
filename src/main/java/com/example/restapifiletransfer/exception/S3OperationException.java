package com.example.restapifiletransfer.exception;

public class S3OperationException extends RuntimeException {

    public S3OperationException(String msg) {
        super(msg);
    }

    public S3OperationException(String msg, Throwable e) {
        super(msg, e);
    }

}
