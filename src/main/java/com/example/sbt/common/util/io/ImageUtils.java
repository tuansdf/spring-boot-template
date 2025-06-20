package com.example.sbt.common.util.io;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class ImageUtils {

    public static void compressImage(InputStream input, OutputStream output, Options options) throws IOException {
        if (input == null || output == null) return;
        BufferedImage image = ImageIO.read(input);
        var builder = Thumbnails.of(image);
        if (options.getWidth() != null && image.getWidth() > options.getWidth()) {
            builder = builder.width(options.getWidth());
        } else {
            builder = builder.width(image.getWidth());
        }
        if (options.getHeight() != null && image.getHeight() > options.getHeight()) {
            builder = builder.height(options.getHeight());
        } else {
            builder = builder.height(image.getHeight());
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

    public static byte[] compressImageToBytes(MultipartFile file, Options options) {
        if (file == null) return null;
        try (InputStream inputStream = file.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            compressImage(inputStream, outputStream, options);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("compressImageToBytes ", e);
            return null;
        }
    }

    public static byte[] compressImageToBytes(byte[] bytes, Options options) {
        if (bytes == null || bytes.length == 0) return null;
        try (InputStream inputStream = new ByteArrayInputStream(bytes);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            compressImage(inputStream, outputStream, options);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("compressImageToBytes ", e);
            return null;
        }
    }

    public static byte[] compressImageToBytes(String filePath, Options options) {
        if (StringUtils.isBlank(filePath)) return null;
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            compressImage(inputStream, outputStream, options);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("compressImageToBytes ", e);
            return null;
        }
    }

    public static void compressImageWriteFile(String filePath, String outputPath, Options options) {
        if (StringUtils.isBlank(filePath) || StringUtils.isBlank(outputPath)) return;
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath));
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
