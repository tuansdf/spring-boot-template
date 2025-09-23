package com.example.sbt.common.util;

import com.example.sbt.common.constant.FileType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.springframework.http.ContentDisposition;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private static final Pattern UNSAFE_CHARS = Pattern.compile("[^A-Za-z0-9._-]");
    private static final Pattern MARK_CHARS = Pattern.compile("\\p{M}");

    public static byte[] toBytes(String filePath) {
        try {
            return Files.readAllBytes(Path.of(filePath));
        } catch (Exception e) {
            return null;
        }
    }

    public static void writeFile(byte[] bytes, String outputPath) {
        if (bytes == null) return;
        try (FileOutputStream outputStream = new FileOutputStream(outputPath);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            bufferedOutputStream.write(bytes);
        } catch (Exception e) {
            log.error("writeFile {}", e.toString());
        }
    }

    public static String getFileExtension(String filename) {
        if (StringUtils.isBlank(filename)) return "";
        int dotIndex = filename.lastIndexOf(EXTENSION_SEPARATOR);
        if (dotIndex < 0 || dotIndex == filename.length() - 1) return "";
        return filename.substring(dotIndex + 1);
    }

    public static String toFilename(String name, FileType fileType) {
        if (StringUtils.isBlank(name) || fileType == null) return "";
        return name + EXTENSION_SEPARATOR + fileType.getExtension();
    }

    public static String toFilePath(String... paths) {
        if (ArrayUtils.isEmpty(paths)) return "";
        return String.join(PATH_SEPARATOR, paths);
    }

    public static FileType validateFileType(byte[] file, List<FileType> fileTypes) {
        if (file == null || file.length == 0) {
            return null;
        }
        if (CollectionUtils.isEmpty(fileTypes)) {
            fileTypes = SUPPORTED_FILE_TYPES;
        }
        try {
            FileType detectedFileType = FileType.fromMimeType(tika.detect(file));
            if (detectedFileType == null) {
                return null;
            }

            for (FileType fileType : fileTypes) {
                if (detectedFileType.equals(fileType)) {
                    return fileType;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static FileType validateFileType(byte[] file) {
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

    public static String truncateFilename(String filename, Integer maxLength) {
        if (StringUtils.isBlank(filename)) return "";
        maxLength = CommonUtils.defaultWhenNotPositive(maxLength, MAX_FILE_NAME_LENGTH);
        if (filename.length() <= maxLength) return filename;
        String extension = getFileExtension(filename);
        if (StringUtils.isEmpty(extension)) {
            return filename.substring(0, maxLength);
        }
        return filename.substring(0, maxLength - extension.length()) + extension;
    }

    public static String truncateFilename(String filename) {
        return truncateFilename(filename, null);
    }

    public static String cleanFilePath(String filePath) {
        if (StringUtils.isBlank(filePath)) return "";
        filePath = FilenameUtils.normalize(filePath, true);
        filePath = filePath.replaceAll("\\s*/\\s*", PATH_SEPARATOR);
        filePath = filePath.replaceAll("/+", PATH_SEPARATOR);
        filePath = StringUtils.strip(filePath, PATH_SEPARATOR);
        return filePath.trim();
    }

    public static String cleanFilename(String filename) {
        if (StringUtils.isBlank(filename)) return "";
        filename = FilenameUtils.normalize(filename);
        filename = Normalizer.normalize(filename, Normalizer.Form.NFKD);
        filename = filename.replace("đ", "d").replace("Đ", "D");
        filename = MARK_CHARS.matcher(filename).replaceAll("");
        filename = UNSAFE_CHARS.matcher(filename).replaceAll("_");
        return filename.trim();
    }

    public static String buildContentDisposition(String filename) {
        filename = truncateFilename(cleanFilename(filename));
        if (StringUtils.isBlank(filename)) {
            return "";
        }
        ContentDisposition.Builder builder = ContentDisposition.attachment();
        if (StringUtils.isNotBlank(filename)) {
            builder.filename(filename);
        }
        return builder.build().toString();
    }
}
