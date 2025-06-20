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
    private static final String PATH_SEPARATOR = "/";
    private static final String EXTENSION_SEPARATOR = ".";
    private static final Pattern UNSAFE_CHARS = Pattern.compile("[^a-zA-Z0-9\\-._ ]");
    private static final Pattern MARK_CHARS = Pattern.compile("\\p{M}");
    private static final Pattern LEADING_TRAILING_CHARS = Pattern.compile("^[._ ]+|[._ ]+$");

    public static void writeFile(byte[] bytes, String outputPath) {
        if (bytes == null) return;
        try (FileOutputStream outputStream = new FileOutputStream(outputPath);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            bufferedOutputStream.write(bytes);
        } catch (Exception e) {
            log.error("writeFile ", e);
        }
    }

    public static String getFileExtension(String fileName) {
        if (StringUtils.isBlank(fileName)) return "";
        int dotIndex = fileName.lastIndexOf(EXTENSION_SEPARATOR);
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) return "";
        return fileName.substring(dotIndex + 1);
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
            String extension = getFileExtension(file.getOriginalFilename());
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
        if (fileName.length() <= maxLength) return fileName;
        String extension = getFileExtension(fileName);
        if (StringUtils.isBlank(extension)) {
            return fileName.substring(0, maxLength);
        }
        return fileName.substring(0, maxLength - extension.length()).concat(extension.trim());
    }

    public static String cleanFilePath(String filePath) {
        return ConversionUtils.safeTrim(StringUtils.strip(filePath
                .replaceAll("\\s*/\\s*", PATH_SEPARATOR)
                .replaceAll("/+", PATH_SEPARATOR), PATH_SEPARATOR));
    }

    public static String cleanFileName(String fileName) {
        if (StringUtils.isBlank(fileName)) return "";
        fileName = FilenameUtils.normalize(fileName);
        fileName = Normalizer.normalize(fileName, Normalizer.Form.NFKD);
        fileName = fileName.replace("đ", "d");
        fileName = fileName.replace("Đ", "D");
        fileName = MARK_CHARS.matcher(fileName).replaceAll("");
        fileName = UNSAFE_CHARS.matcher(fileName).replaceAll("_");
        fileName = LEADING_TRAILING_CHARS.matcher(fileName).replaceAll("");
        String extension = getFileExtension(fileName);
        if (StringUtils.isNotBlank(extension)) {
            fileName = fileName.substring(0, fileName.length() - extension.length()).concat(extension.trim());
        }
        return fileName.trim();
    }

}
