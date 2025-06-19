package com.example.sbt.common.util.io;

import com.example.sbt.common.constant.FileType;
import com.example.sbt.common.util.ConversionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.text.Normalizer;
import java.util.regex.Pattern;

@Slf4j
public class FileUtils {

    private static final int MAX_FILE_NAME_LENGTH = 255;
    private static final Pattern FORBIDDEN_CHARS = Pattern.compile("[^a-zA-Z0-9\\-._ ]");

    public static void writeFile(byte[] bytes, String outputPath) {
        if (bytes == null) return;
        try (FileOutputStream outputStream = new FileOutputStream(outputPath);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            bufferedOutputStream.write(bytes);
        } catch (Exception e) {
            log.error("writeFile ", e);
        }
    }

    public static boolean validateFileType(MultipartFile file, FileType... fileTypes) {
        if (ArrayUtils.isEmpty(fileTypes)) {
            return false;
        }
        if (StringUtils.isBlank(file.getOriginalFilename())) {
            return false;
        }
        if (StringUtils.isBlank(file.getContentType())) {
            return false;
        }
        for (FileType fileType : fileTypes) {
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            if (fileType.getExtension().equals(extension) && fileType.getMimeType().equals(file.getContentType())) {
                return true;
            }
        }
        return false;
    }

    public static String truncateFileName(String fileName, Integer maxLength) {
        if (StringUtils.isBlank(fileName)) return "";
        if (maxLength == null || maxLength <= 0) {
            maxLength = MAX_FILE_NAME_LENGTH;
        }
        fileName = fileName.trim();
        if (fileName.length() <= MAX_FILE_NAME_LENGTH) return fileName;
        String extension = FilenameUtils.getExtension(fileName);
        if (StringUtils.isBlank(extension)) {
            return fileName.substring(0, MAX_FILE_NAME_LENGTH);
        }
        extension = extension.trim();
        return fileName.substring(0, MAX_FILE_NAME_LENGTH - extension.length()).concat(".").concat(extension);
    }

    public static String cleanFilePath(String filePath) {
        return ConversionUtils.safeTrim(StringUtils.strip(filePath
                .replaceAll("\\s*/\\s*", "/")
                .replaceAll("/+", "/"), "/"));
    }

    public static String cleanFileName(String fileName) {
        if (StringUtils.isBlank(fileName)) return "";
        String result = FilenameUtils.normalize(fileName);
        result = Normalizer.normalize(result, Normalizer.Form.NFKD).replaceAll("\\p{M}", "");
        result = FORBIDDEN_CHARS.matcher(result).replaceAll("_");
        result = result.replaceAll("^[._ ]+|[._ ]+$", "");
        return result.trim();
    }

}
