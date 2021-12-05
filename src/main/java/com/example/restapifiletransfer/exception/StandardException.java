package com.example.restapifiletransfer.exception;

public class StandardException extends RuntimeException {

    private String msg;
    private Throwable e;

    public StandardException(String msg) {
        super(msg);
    }

    public StandardException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.e = e;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Throwable getE() {
        return e;
    }

    public void setE(Throwable e) {
        this.e = e;
    }
}
