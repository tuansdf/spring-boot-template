package com.example.sbt.common.util.io;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;

@Slf4j
public class ImageUtils {

    public static void compressImage(BufferedImage input, OutputStream output, Options options) throws IOException {
        var builder = Thumbnails.of(input);
        if (options.getWidth() != null && input.getWidth() > options.getWidth()) {
            builder = builder.width(options.getWidth());
        } else {
            builder = builder.width(input.getWidth());
        }
        if (options.getHeight() != null && input.getHeight() > options.getHeight()) {
            builder = builder.height(options.getHeight());
        } else {
            builder = builder.height(input.getHeight());
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
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(Paths.get(inputPath).toFile());
            compressImage(image, outputStream, options);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("compressImageToBytes ", e);
            return null;
        }
    }

    public static byte[] compressImageToBytes(byte[] bytes, Options options) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            compressImage(image, outputStream, options);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("compressImageToBytes ", e);
            return null;
        }
    }

    public static void compressImageWriteFile(String inputPath, String outputPath, Options options) {
        try (FileOutputStream outputStream = new FileOutputStream(outputPath);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            BufferedImage image = ImageIO.read(Paths.get(inputPath).toFile());
            compressImage(image, bufferedOutputStream, options);
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
