package com.example.sbt.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;

@Slf4j
public class CompressUtils {
    public static byte[] compressToGzip(byte[] bytes) throws IOException {
        GzipParameters parameters = new GzipParameters();
        parameters.setCompressionLevel(Deflater.BEST_SPEED);
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             OutputStream outputStream = new GzipCompressorOutputStream(byteArrayOutputStream, parameters)) {
            outputStream.write(bytes);
            return byteArrayOutputStream.toByteArray();
        }
    }

    public static byte[] decompressFromGzip(byte[] bytes) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             GzipCompressorInputStream gis = new GzipCompressorInputStream(bais);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int n;
            while ((n = gis.read(buffer)) != -1) {
                baos.write(buffer, 0, n);
            }
            return baos.toByteArray();
        }
    }
}
