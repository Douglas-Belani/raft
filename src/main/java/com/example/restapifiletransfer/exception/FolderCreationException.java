package com.example.restapifiletransfer.exception;

public class FolderCreationException extends StandardException {

    public FolderCreationException(String msg) {
        super(msg);
    }

    public FolderCreationException(String msg, Throwable e) {
        super(msg, e);
    }

}
