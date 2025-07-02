package com.example.sbt.shared.util;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

@Slf4j
public class ImageUtils {

    public static void compressImage(InputStream input, OutputStream output, Options options) throws IOException {
        if (input == null || output == null) return;
        ImageWriter writer = null;
        ImageOutputStream ios = null;
        try {
            BufferedImage originalImage = ImageIO.read(input);
            if (originalImage == null) {
                throw new IOException("Could not decode image from input stream");
            }

            BufferedImage imageToWrite = originalImage;

            if (options.getWidth() != null && options.getHeight() != null) {
                Image scaled = originalImage.getScaledInstance(
                        options.getWidth(),
                        options.getHeight(),
                        Image.SCALE_SMOOTH
                );
                BufferedImage resized = new BufferedImage(
                        options.getWidth(),
                        options.getHeight(),
                        originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType()
                );
                Graphics2D g2d = resized.createGraphics();
                g2d.drawImage(scaled, 0, 0, null);
                g2d.dispose();
                imageToWrite = resized;
            }

            // Determine format
            String format = options.getFormat() != null ? options.getFormat() : "jpg";

            // Get writer
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(format);
            if (!writers.hasNext()) {
                throw new IllegalArgumentException("No ImageWriter found for format: " + format);
            }
            writer = writers.next();

            ios = ImageIO.createImageOutputStream(output);
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();

            // Set compression quality for JPEG
            if (param.canWriteCompressed() && options.getQuality() != null) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(options.getQuality());
            }

            writer.write(null, new IIOImage(imageToWrite, null, null), param);
        } finally {
            try {
                if (writer != null) {
                    writer.dispose();
                }
                if (ios != null) {
                    ios.flush();
                }
            } catch (Exception ignored) {
            }
        }
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
