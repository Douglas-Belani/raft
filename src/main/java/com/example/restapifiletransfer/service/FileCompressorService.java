package com.example.restapifiletransfer.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileCompressorService {

    public byte[] compress(MultipartFile uploadedFile);

    public byte[] decompress(byte[] compressedFile);

}
