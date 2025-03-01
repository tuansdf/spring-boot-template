package com.example.demo.utils;

import lombok.Builder;
import lombok.Data;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Paths;

public class ImageHelper {

    public static void compressImage(InputStream input, OutputStream output, Options options) throws IOException {
        var builder = Thumbnails.of(input);
        if (options.getWidth() != null) {
            builder = builder.width(options.getWidth());
        }
        if (options.getHeight() != null) {
            builder = builder.height(options.getHeight());
        }
        if (options.getQuality() != null) {
            builder = builder.outputQuality(options.getQuality());
        }
        if (StringUtils.isNotBlank(options.getFormat())) {
            builder = builder.outputFormat(options.getFormat());
        } else {
            builder = builder.useOriginalFormat();
        }
        builder.toOutputStream(output);
    }

    public static byte[] compressImageToBytes(InputStream input, Options options) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            compressImage(input, bufferedOutputStream, options);
            return outputStream.toByteArray();
        }
    }

    public static void compressImageWriteFile(String inputPath, String outputPath, Options options) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(Paths.get(inputPath).toFile());
             FileOutputStream outputStream = new FileOutputStream(outputPath);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            compressImage(inputStream, bufferedOutputStream, options);
        }
    }

    @Data
    @Builder
    public static class Options {
        private Integer width;
        private Integer height;
        private Float quality;
        private String format;
    }

}
