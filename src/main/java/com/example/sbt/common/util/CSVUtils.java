package com.example.sbt.common.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.Deflater;

@Slf4j
public class CSVUtils {
    public static void readData(Reader reader, Consumer<String[]> rowProcessor) {
        try (CSVReader csvReader = new CSVReader(reader)) {
            if (csvReader.readNext() == null) return;
            for (String[] row : csvReader) {
                rowProcessor.accept(row);
            }
        } catch (Exception e) {
            log.error("readData", e);
        }
    }

    public static <T> List<T> readData(Reader reader, Function<String[], T> rowProcessor) {
        try {
            List<T> result = new ArrayList<>();
            readData(reader, (row) -> {
                result.add(rowProcessor.apply(row));
            });
            return result;
        } catch (Exception e) {
            log.error("readData", e);
            return null;
        }
    }

    public static <T> List<T> readData(String filePath, Function<String[], T> rowProcessor) {
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath), StandardCharsets.UTF_8)) {
            return readData(reader, rowProcessor);
        } catch (Exception e) {
            log.error("readData", e);
            return null;
        }
    }

    public static <T> List<T> readData(byte[] file, Function<String[], T> rowProcessor) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(file);
             Reader reader = new InputStreamReader(bais, StandardCharsets.UTF_8)) {
            return readData(reader, rowProcessor);
        } catch (Exception e) {
            log.error("readData", e);
            return null;
        }
    }

    public static <T> List<T> readData(MultipartFile file, Function<String[], T> rowProcessor) {
        try (InputStream is = file.getInputStream();
             InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return readData(reader, rowProcessor);
        } catch (Exception e) {
            log.error("readData", e);
            return null;
        }
    }

    public static <T> List<T> readDataFromGzip(String filePath, Function<String[], T> rowProcessor) {
        try (InputStream is = Files.newInputStream(Path.of(filePath));
             GzipCompressorInputStream gis = new GzipCompressorInputStream(is);
             InputStreamReader reader = new InputStreamReader(gis, StandardCharsets.UTF_8)) {
            return readData(reader, rowProcessor);
        } catch (Exception e) {
            log.error("readDataFromGzipBytes", e);
            return null;
        }
    }

    public static <T> List<T> readDataFromGzip(byte[] file, Function<String[], T> rowProcessor) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(file);
             GzipCompressorInputStream gis = new GzipCompressorInputStream(bais);
             InputStreamReader reader = new InputStreamReader(gis, StandardCharsets.UTF_8)) {
            return readData(reader, rowProcessor);
        } catch (Exception e) {
            log.error("readDataFromGzipBytes", e);
            return null;
        }
    }

    public static <T> List<T> readDataFromGzip(MultipartFile file, Function<String[], T> rowProcessor) {
        try (InputStream is = file.getInputStream();
             GzipCompressorInputStream gis = new GzipCompressorInputStream(is);
             InputStreamReader reader = new InputStreamReader(gis, StandardCharsets.UTF_8)) {
            return readData(reader, rowProcessor);
        } catch (Exception e) {
            log.error("readDataFromGzipBytes", e);
            return null;
        }
    }

    public static <T> void writeData(Writer writer, String[] header, List<T> data, Function<T, String[]> rowProcessor) {
        try (CSVWriter csvWriter = new CSVWriter(writer)) {
            if (ArrayUtils.isNotEmpty(header)) {
                csvWriter.writeNext(header);
            }
            if (CollectionUtils.isNotEmpty(data) && rowProcessor != null) {
                for (T item : data) {
                    csvWriter.writeNext(rowProcessor.apply(item));
                }
            }
        } catch (Exception e) {
            log.error("writeData", e);
        }
    }

    public static <T> byte[] writeDataToBytes(String[] header, List<T> data, Function<T, String[]> rowProcessor) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {
            writeData(writer, header, data, rowProcessor);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("writeDataToBytes", e);
            return null;
        }
    }

    public static <T> void writeDataToFile(String filePath, String[] header, List<T> data, Function<T, String[]> rowProcessor) {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(filePath), StandardCharsets.UTF_8)) {
            writeData(writer, header, data, rowProcessor);
        } catch (Exception e) {
            log.error("writeDataToFile", e);
        }
    }

    public static <T> byte[] writeDataToGzipBytes(String[] header, List<T> data, Function<T, String[]> rowProcessor) {
        GzipParameters parameters = new GzipParameters();
        parameters.setCompressionLevel(Deflater.BEST_SPEED);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GzipCompressorOutputStream gos = new GzipCompressorOutputStream(baos, parameters);
             OutputStreamWriter writer = new OutputStreamWriter(gos, StandardCharsets.UTF_8)) {
            writeData(writer, header, data, rowProcessor);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("writeDataToGzipBytes", e);
            return null;
        }
    }

    public static <T> void writeDataToGzipFile(String filePath, String[] header, List<T> data, Function<T, String[]> rowProcessor) {
        GzipParameters parameters = new GzipParameters();
        parameters.setCompressionLevel(Deflater.BEST_SPEED);
        try (OutputStream fos = Files.newOutputStream(Path.of(filePath));
             GzipCompressorOutputStream gos = new GzipCompressorOutputStream(fos, parameters);
             OutputStreamWriter writer = new OutputStreamWriter(gos, StandardCharsets.UTF_8)) {
            writeData(writer, header, data, rowProcessor);
        } catch (Exception e) {
            log.error("writeDataToGzipFile", e);
        }
    }
}