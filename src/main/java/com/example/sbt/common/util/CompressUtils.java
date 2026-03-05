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
    private CompressUtils() {
    }

    public static class Gzip {
        public static final GzipParameters GZIP_FAST = new GzipParameters();

        static {
            GZIP_FAST.setCompressionLevel(Deflater.BEST_SPEED);
        }

        private Gzip() {
        }

        public static byte[] compress(byte[] bytes) throws IOException {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 OutputStream gos = new GzipCompressorOutputStream(baos, GZIP_FAST)) {
                gos.write(bytes);
                gos.flush();
                gos.close();
                return baos.toByteArray();
            }
        }

        public static byte[] decompress(byte[] bytes) throws IOException {
            try (GzipCompressorInputStream gis = new GzipCompressorInputStream(new ByteArrayInputStream(bytes))) {
                return gis.readAllBytes();
            }
        }
    }
}
