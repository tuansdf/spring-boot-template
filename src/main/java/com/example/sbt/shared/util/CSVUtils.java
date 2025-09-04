package com.example.sbt.shared.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class CSVUtils {
    public static <T> List<T> toData(Reader input, Function<String[], T> rowProcessor) {
        if (input == null) return null;
        try (CSVReader reader = new CSVReader(input)) {
            List<T> result = new ArrayList<>();
            boolean isHeader = true;
            for (String[] row : reader) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                result.add(rowProcessor.apply(row));
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> List<T> toData(String filePath, Function<String[], T> rowProcessor) {
        try (Reader reader = Files.newBufferedReader(Paths.get(filePath))) {
            return toData(reader, rowProcessor);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> List<T> toData(MultipartFile file, Function<String[], T> rowProcessor) {
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            return toData(reader, rowProcessor);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> List<T> toData(byte[] file, Function<String[], T> rowProcessor) {
        try (Reader reader = new InputStreamReader(new ByteArrayInputStream(file), StandardCharsets.UTF_8)) {
            return toData(reader, rowProcessor);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> void writeData(Writer input, String[] header, List<T> data, Function<T, String[]> rowProcessor) {
        if (input == null) return;
        try (input; CSVWriter writer = new CSVWriter(input)) {
            if (ArrayUtils.isNotEmpty(header)) {
                writer.writeNext(header);
            }
            if (CollectionUtils.isNotEmpty(data) && rowProcessor != null) {
                for (T item : data) {
                    writer.writeNext(rowProcessor.apply(item));
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static <T> byte[] writeDataToBytes(String[] header, List<T> data, Function<T, String[]> rowProcessor) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             Writer writer = new OutputStreamWriter(outputStream)) {
            writeData(writer, header, data, rowProcessor);
            return outputStream.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> void writeDataToFile(String filePath, String[] header, List<T> data, Function<T, String[]> rowProcessor) {
        try (Writer writer = new FileWriter(filePath)) {
            writeData(writer, header, data, rowProcessor);
        } catch (Exception ignored) {
        }
    }
}