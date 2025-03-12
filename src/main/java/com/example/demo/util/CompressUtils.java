package com.example.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;

@Slf4j
public class CompressUtils {

    public static byte[] toGzip(byte[] inputBytes) {
        try {
            GzipParameters parameters = new GzipParameters();
            parameters.setCompressionLevel(Deflater.BEST_SPEED);
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 OutputStream outputStream = new GzipCompressorOutputStream(byteArrayOutputStream, parameters)) {
                outputStream.write(inputBytes);
                return byteArrayOutputStream.toByteArray();
            }
        } catch (Exception e) {
            log.error("compressGzip");
            return new byte[0];
        }
    }

}
