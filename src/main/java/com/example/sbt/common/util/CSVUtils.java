package com.example.sbt.common.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
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

@Slf4j
public class CSVUtils {
    private static final int BUFFER_SIZE = 1024 * 64;

    public static void read(Reader reader, Consumer<String[]> rowProcessor) {
        try (CSVReader csvReader = new CSVReader(reader)) {
            if (csvReader.readNext() == null) return;
            for (String[] row : csvReader) {
                rowProcessor.accept(row);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public static <T> List<T> read(Reader reader, Function<String[], T> rowProcessor) {
        try {
            List<T> result = new ArrayList<>();
            read(reader, (row) -> {
                result.add(rowProcessor.apply(row));
            });
            return result;
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public static <T> List<T> read(InputStream is, Function<String[], T> rowProcessor) {
        try (is; InputStream bis = new BufferedInputStream(is, BUFFER_SIZE);
             Reader reader = new InputStreamReader(bis, StandardCharsets.UTF_8)) {
            return read(reader, rowProcessor);
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public static <T> List<T> read(String filePath, Function<String[], T> rowProcessor) {
        try (Reader reader = Files.newBufferedReader(Path.of(filePath), StandardCharsets.UTF_8)) {
            return read(reader, rowProcessor);
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public static <T> List<T> read(byte[] file, Function<String[], T> rowProcessor) {
        try (InputStream is = new ByteArrayInputStream(file)) {
            return read(is, rowProcessor);
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public static <T> List<T> read(MultipartFile file, Function<String[], T> rowProcessor) {
        try (InputStream is = file.getInputStream()) {
            return read(is, rowProcessor);
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public static <T> List<T> readGzip(InputStream is, Function<String[], T> rowProcessor) {
        try (is; InputStream bis = new BufferedInputStream(is, BUFFER_SIZE);
             InputStream gis = new GzipCompressorInputStream(bis)) {
            return read(gis, rowProcessor);
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public static <T> List<T> readGzip(String filePath, Function<String[], T> rowProcessor) {
        try (InputStream is = Files.newInputStream(Path.of(filePath))) {
            return read(is, rowProcessor);
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public static <T> List<T> readGzip(byte[] file, Function<String[], T> rowProcessor) {
        try (InputStream is = new ByteArrayInputStream(file)) {
            return read(is, rowProcessor);
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public static <T> List<T> readGzip(MultipartFile file, Function<String[], T> rowProcessor) {
        try (InputStream is = file.getInputStream()) {
            return read(is, rowProcessor);
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public static <T> void write(Writer writer, String[] header, List<T> data, Function<T, String[]> rowProcessor) {
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
            log.error("", e);
        }
    }

    public static <T> void write(OutputStream os, String[] header, List<T> data, Function<T, String[]> rowProcessor) {
        try (Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
            write(writer, header, data, rowProcessor);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public static <T> void write(String filePath, String[] header, List<T> data, Function<T, String[]> rowProcessor) {
        try (Writer writer = Files.newBufferedWriter(Path.of(filePath), StandardCharsets.UTF_8)) {
            write(writer, header, data, rowProcessor);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public static <T> byte[] write(String[] header, List<T> data, Function<T, String[]> rowProcessor) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            write(baos, header, data, rowProcessor);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public static <T> void writeGzip(OutputStream os, String[] header, List<T> data, Function<T, String[]> rowProcessor) {
        try (OutputStream bos = new BufferedOutputStream(os, BUFFER_SIZE);
             OutputStream gos = new GzipCompressorOutputStream(bos, CompressUtils.Gzip.GZIP_FAST)) {
            write(gos, header, data, rowProcessor);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public static <T> void writeGzip(String filePath, String[] header, List<T> data, Function<T, String[]> rowProcessor) {
        try (OutputStream fos = Files.newOutputStream(Path.of(filePath))) {
            write(fos, header, data, rowProcessor);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public static <T> byte[] writeGzip(String[] header, List<T> data, Function<T, String[]> rowProcessor) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            write(baos, header, data, rowProcessor);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }
}