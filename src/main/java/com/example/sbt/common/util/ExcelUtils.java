package com.example.sbt.common.util;

import com.github.pjfanning.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class ExcelUtils {
    private static final String DEFAULT_SHEET = "Sheet1";

    public static void writeFile(Workbook workbook, Path path) {
        try (OutputStream fos = Files.newOutputStream(path)) {
            workbook.write(fos);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public static byte[] toBytes(Workbook workbook) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public static Sheet getSheet(Workbook workbook) {
        if (workbook == null) return null;
        Sheet result = workbook.getSheet(DEFAULT_SHEET);
        if (result == null) {
            result = workbook.createSheet(DEFAULT_SHEET);
        }
        return result;
    }

    public static Row getRow(Sheet sheet, int rowIdx) {
        if (sheet == null || rowIdx < 0) return null;
        Row result = sheet.getRow(rowIdx);
        if (result == null) {
            result = sheet.createRow(rowIdx);
        }
        return result;
    }

    public static Cell getCell(Row row, int colIdx) {
        if (row == null || colIdx < 0) return null;
        Cell result = row.getCell(colIdx);
        if (result == null) {
            result = row.createCell(colIdx);
        }
        return result;
    }

    public static Object getCellValue(Cell cell) {
        if (cell == null) return null;
        try {
            return switch (cell.getCellType()) {
                case NUMERIC -> {
                    double value = cell.getNumericCellValue();
                    if (value % 1 == 0) {
                        yield (long) value;
                    }
                    yield value;
                }
                case STRING -> cell.getStringCellValue();
                default -> null;
            };
        } catch (Exception e) {
            log.debug("getCellValue", e);
            return null;
        }
    }

    public static Object getCellValue(Row row, int colIdx) {
        return getCellValue(getCell(row, colIdx));
    }

    public static Object getCellValue(Sheet sheet, int rowIdx, int colIdx) {
        return getCellValue(getCell(getRow(sheet, rowIdx), colIdx));
    }

    public static List<Object> getRowCellValues(Row row) {
        if (row == null) return null;
        List<Object> rowData = new ArrayList<>();
        for (int i = 0; i < row.getLastCellNum(); i++) {
            rowData.add(getCellValue(row.getCell(i)));
        }
        return rowData;
    }

    public static void setCellValue(Cell cell, Object value) {
        if (cell == null || value == null) return;
        switch (value) {
            case String v -> cell.setCellValue(v);
            case Number v -> cell.setCellValue(v.doubleValue());
            default -> cell.setCellValue(value.toString());
        }
    }

    public static void setCellValue(Sheet sheet, int rowIdx, int colIdx, Object value) {
        setCellValue(getCell(getRow(sheet, rowIdx), colIdx), value);
    }

    public static void setCellValue(Row row, int colIdx, Object value) {
        setCellValue(getCell(row, colIdx), value);
    }

    public static void setCellValues(Row row, List<Object> objects) {
        if (row == null || CollectionUtils.isEmpty(objects)) return;
        int i = 0;
        for (Object object : objects) {
            setCellValue(row, i, object);
            i++;
        }
    }

    public static void setCellValues(Sheet sheet, int rowIdx, List<Object> objects) {
        if (CollectionUtils.isEmpty(objects)) return;
        Row row = getRow(sheet, rowIdx);
        if (row == null) return;
        int i = 0;
        for (Object object : objects) {
            setCellValue(row, i, object);
            i++;
        }
    }

    public static <T> void setCellValues(Row row, T[] objects) {
        if (row == null || ArrayUtils.isEmpty(objects)) return;
        for (int i = 0; i < objects.length; i++) {
            setCellValue(row, i, ConversionUtils.safeToString(objects[i]));
        }
    }

    public static Workbook toStreamingWorkbook(Path path) {
        try (InputStream fis = Files.newInputStream(path)) {
            return StreamingReader.builder().open(fis);
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public static Workbook toStreamingWorkbook(byte[] file) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(file)) {
            return StreamingReader.builder().open(bais);
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public static Workbook toStreamingWorkbook(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return StreamingReader.builder().open(inputStream);
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public static void readData(Workbook workbook, Consumer<List<Object>> rowProcessor) {
        try (workbook) {
            if (workbook == null) return;
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) return;
            boolean isHeader = true;
            for (Row row : sheet) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                rowProcessor.accept(getRowCellValues(row));
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public static <T> List<T> readData(Workbook workbook, Function<List<Object>, T> rowProcessor) {
        try (workbook) {
            List<T> result = new ArrayList<>();
            readData(workbook, (row) -> {
                result.add(rowProcessor.apply(row));
            });
            return result;
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public static <T> List<T> readData(Path path, Function<List<Object>, T> rowProcessor) {
        try (Workbook workbook = toStreamingWorkbook(path)) {
            return readData(workbook, rowProcessor);
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public static <T> List<T> readData(byte[] file, Function<List<Object>, T> rowProcessor) {
        try (Workbook workbook = toStreamingWorkbook(file)) {
            return readData(workbook, rowProcessor);
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public static <T> List<T> readData(MultipartFile file, Function<List<Object>, T> rowProcessor) {
        try (Workbook workbook = toStreamingWorkbook(file)) {
            return readData(workbook, rowProcessor);
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public static <T> void writeData(Workbook workbook, List<Object> header, List<T> data, Function<T, List<Object>> rowProcessor) {
        try {
            Sheet sheet = getSheet(workbook);
            if (CollectionUtils.isNotEmpty(header)) {
                setCellValues(sheet, 0, header);
            }
            if (CollectionUtils.isNotEmpty(data) && rowProcessor != null) {
                int idx = CollectionUtils.isNotEmpty(header) ? 1 : 0;
                for (T item : data) {
                    setCellValues(sheet, idx, rowProcessor.apply(item));
                    idx++;
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public static <T> byte[] writeDataToBytes(List<Object> header, List<T> data, Function<T, List<Object>> rowProcessor) {
        try (Workbook workbook = new SXSSFWorkbook()) {
            writeData(workbook, header, data, rowProcessor);
            return toBytes(workbook);
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public static <T> void writeDataToFile(Path path, List<Object> header, List<T> data, Function<T, List<Object>> rowProcessor) {
        try (Workbook workbook = new SXSSFWorkbook()) {
            writeData(workbook, header, data, rowProcessor);
            writeFile(workbook, path);
        } catch (Exception e) {
            log.error("", e);
        }
    }
}