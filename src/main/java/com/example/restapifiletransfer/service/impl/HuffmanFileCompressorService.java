package com.example.restapifiletransfer.service.impl;

import com.example.restapifiletransfer.exception.CompressionException;
import com.example.restapifiletransfer.exception.DecompressionException;
import com.example.restapifiletransfer.service.FileCompressorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

@Service
public class HuffmanFileCompressorService implements FileCompressorService {

    private static final Logger log = LoggerFactory.getLogger(HuffmanFileCompressorService.class);

    @Override
    public byte[] compress(MultipartFile uploadedFile) {
        Deflater deflater = new Deflater(Deflater.HUFFMAN_ONLY);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(baos, deflater);
        try {
            log.info("Original file size {}", uploadedFile.getSize());
            deflaterOutputStream.write(uploadedFile.getBytes());
            deflaterOutputStream.close();

            byte[] compressedContent = baos.toByteArray();
            log.info("Compressed file size {}", compressedContent.length);
            return compressedContent;

        } catch (IOException e) {
            throw new CompressionException("Error compressing file " + uploadedFile.getOriginalFilename(), e);
        }
    }

    @Override
    public byte[] decompress(byte[] compressedFile) {
        try {
            log.info("Decompressing file");

            Inflater inflater = new Inflater();
            ByteArrayInputStream bais = new ByteArrayInputStream(compressedFile);
            InflaterInputStream inflaterInputStream = new InflaterInputStream(bais, inflater);

            byte[] byteContent = inflaterInputStream.readAllBytes();
            inflaterInputStream.close();

            return byteContent;

        } catch (IOException e) {
            throw new DecompressionException("Error decompressing file", e);
        }
    }
}
