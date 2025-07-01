package com.example.sbt.shared.util;

import com.example.sbt.shared.constant.FileType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class FileUtils {

    private static final Tika tika = new Tika();
    private static final List<FileType> SUPPORTED_FILE_TYPES = Arrays.asList(FileType.values());
    private static final int MAX_FILE_NAME_LENGTH = 255;
    private static final String PATH_SEPARATOR = "/";
    private static final String EXTENSION_SEPARATOR = ".";
    private static final Pattern UNSAFE_CHARS = Pattern.compile("[^a-zA-Z0-9\\-._ ]");
    private static final Pattern MARK_CHARS = Pattern.compile("\\p{M}");
    private static final Pattern LEADING_TRAILING_CHARS = Pattern.compile("^[. ]+|[. ]+$");

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

    public static boolean validateFileType(byte[] file, List<FileType> fileTypes) {
        if (file == null || file.length == 0) return false;
        if (CollectionUtils.isEmpty(fileTypes)) {
            fileTypes = SUPPORTED_FILE_TYPES;
        }
        try {
            FileType detectedFileType = FileType.fromMimeType(tika.detect(file));
            if (detectedFileType == null) return false;

            for (FileType fileType : fileTypes) {
                if (detectedFileType.equals(fileType)) return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static boolean validateFileType(byte[] file) {
        return validateFileType(file, null);
    }

    public static boolean validateFileType(MultipartFile file, List<FileType> fileTypes) {
        if (file == null || file.isEmpty()) return false;
        if (CollectionUtils.isEmpty(fileTypes)) {
            fileTypes = SUPPORTED_FILE_TYPES;
        }
        try {
            FileType fileTypeByMimeType = FileType.fromMimeType(file.getContentType());
            if (fileTypeByMimeType == null) return false;

            FileType fileTypeByExtension = FileType.fromExtension(getFileExtension(file.getOriginalFilename()));
            if (!fileTypeByMimeType.equals(fileTypeByExtension)) return false;

            FileType detectedFileType = null;
            try (InputStream inputStream = file.getInputStream()) {
                detectedFileType = FileType.fromMimeType(tika.detect(inputStream));
            }
            if (detectedFileType == null || !detectedFileType.equals(fileTypeByMimeType)) return false;

            for (FileType fileType : fileTypes) {
                if (detectedFileType.equals(fileType)) return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static boolean validateFileType(MultipartFile file) {
        return validateFileType(file, null);
    }

    public static String truncateFileName(String fileName, Integer maxLength) {
        if (StringUtils.isBlank(fileName)) return "";
        if (maxLength == null || maxLength <= 0) {
            maxLength = MAX_FILE_NAME_LENGTH;
        }
        if (fileName.length() <= maxLength) return fileName;
        String extension = getFileExtension(fileName);
        if (StringUtils.isEmpty(extension)) {
            return fileName.substring(0, maxLength);
        }
        return fileName.substring(0, maxLength - extension.length()).concat(extension);
    }

    public static String truncateFileName(String fileName) {
        return truncateFileName(fileName, null);
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
            fileName = fileName.substring(0, fileName.length() - extension.length()).concat(extension.trim().toLowerCase());
        }
        return fileName.trim();
    }

}
