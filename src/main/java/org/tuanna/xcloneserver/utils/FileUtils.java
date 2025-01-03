package org.tuanna.xcloneserver.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;

@Slf4j
public class FileUtils {

    public static void writeFile(byte[] bytes, String outputPath) {
        try {
            if (bytes == null) return;
            try (FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                outputStream.write(bytes);
            }
        } catch (Exception e) {
            log.error("writefile", e);
        }
    }

}
