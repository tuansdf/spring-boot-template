package com.example.sbt.common.util.io;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Paths;

@Slf4j
public class ImageUtils {

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

    public static byte[] compressImageToBytes(String inputPath, Options options) {
        try (FileInputStream inputStream = new FileInputStream(Paths.get(inputPath).toFile());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            compressImage(inputStream, bufferedOutputStream, options);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("compressImageToBytes ", e);
            return null;
        }
    }

    public static void compressImageWriteFile(String inputPath, String outputPath, Options options) {
        try (FileInputStream inputStream = new FileInputStream(Paths.get(inputPath).toFile());
             FileOutputStream outputStream = new FileOutputStream(outputPath);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            compressImage(inputStream, bufferedOutputStream, options);
        } catch (Exception e) {
            log.error("compressImageWriteFile ", e);
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
