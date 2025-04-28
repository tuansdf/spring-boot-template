package com.example.sbt.common.util.io;

import com.example.sbt.common.constant.FileType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

@Slf4j
public class FileUtils {

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

}
