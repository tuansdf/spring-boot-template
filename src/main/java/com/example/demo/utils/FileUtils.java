package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

@Slf4j
public class FileUtils {

    public static void writeFile(byte[] bytes, String outputPath) {
        try {
            if (bytes == null) return;
            try (FileOutputStream outputStream = new FileOutputStream(outputPath);
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
                bufferedOutputStream.write(bytes);
            }
        } catch (Exception e) {
            log.error("writeFile", e);
        }
    }

}
