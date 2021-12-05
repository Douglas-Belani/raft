package com.example.restapifiletransfer.model;

import java.util.Arrays;
import java.util.Objects;

public class File {

    private byte[] content;
    private String mimeType;
    private String charset;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return Arrays.equals(content, file.content) && Objects.equals(mimeType, file.mimeType) && Objects.equals(charset, file.charset);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(mimeType, charset);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }
}
