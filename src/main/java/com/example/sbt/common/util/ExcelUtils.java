package com.example.sbt.common.util;

import com.github.pjfanning.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class ExcelUtils {
    private static final String DEFAULT_SHEET = "Sheet1";

    public static void writeFile(Workbook workbook, String outputPath) {
        if (workbook == null || StringUtils.isBlank(outputPath)) return;
        try (workbook; FileOutputStream outputStream = new FileOutputStream(outputPath);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            workbook.write(bufferedOutputStream);
        } catch (Exception e) {
            log.error("writeFile {}", e.toString());
        }
    }

    public static byte[] toBytes(Workbook workbook) {
        if (workbook == null) return null;
        try (workbook; ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("toBytes {}", e.toString());
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

    public static Row getRow(Sheet sheet, Integer rowIdx) {
        if (sheet == null || rowIdx == null) return null;
        Row result = sheet.getRow(rowIdx);
        if (result == null) {
            result = sheet.createRow(rowIdx);
        }
        return result;
    }

    public static Cell getCell(Row row, Integer colIdx) {
        if (row == null || colIdx == null) return null;
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
                case NUMERIC -> cell.getNumericCellValue();
                case STRING -> cell.getStringCellValue();
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static Object getCellValue(Row row, Integer colIdx) {
        return getCellValue(getCell(row, colIdx));
    }

    public static Object getCellValue(Sheet sheet, Integer rowIdx, Integer colIdx) {
        return getCellValue(getCell(getRow(sheet, rowIdx), colIdx));
    }

    public static List<Object> getRowCellValues(Row row) {
        if (row == null) return null;
        List<Object> rowData = new ArrayList<>();
        for (Cell cell : row) {
            rowData.add(getCellValue(cell));
        }
        return rowData;
    }

    public static void setCellValue(Cell cell, Object value) {
        if (cell == null || value == null) return;
        switch (value) {
            case String v -> cell.setCellValue(v);
            case Number v -> cell.setCellValue(v.doubleValue());
            default -> cell.setCellValue(ConversionUtils.toString(value));
        }
    }

    public static void setCellValue(Sheet sheet, Integer rowIdx, Integer colIdx, Object value) {
        setCellValue(getCell(getRow(sheet, rowIdx), colIdx), value);
    }

    public static void setCellValue(Row row, Integer colIdx, Object value) {
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

    public static void setCellValues(Sheet sheet, Integer rowIdx, List<Object> objects) {
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

    public static Workbook toWorkbook(String filePath) {
        if (StringUtils.isBlank(filePath)) return null;
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            return StreamingReader.builder().open(inputStream);
        } catch (Exception e) {
            return null;
        }
    }

    public static Workbook toWorkbook(byte[] file) {
        if (file == null) return null;
        try (InputStream inputStream = new ByteArrayInputStream(file)) {
            return StreamingReader.builder().open(inputStream);
        } catch (Exception e) {
            return null;
        }
    }

    public static Workbook toWorkbook(MultipartFile file) {
        if (file == null) return null;
        try (InputStream inputStream = file.getInputStream()) {
            return StreamingReader.builder().open(inputStream);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> List<T> toData(Workbook workbook, Function<List<Object>, T> rowProcessor) {
        try (workbook) {
            if (workbook == null) return null;
            List<T> result = new ArrayList<>();
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) return null;
            boolean isHeader = true;
            for (Row row : sheet) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                result.add(rowProcessor.apply(ExcelUtils.getRowCellValues(row)));
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> List<T> toData(MultipartFile file, Function<List<Object>, T> rowProcessor) {
        if (file == null) return null;
        try (Workbook workbook = toWorkbook(file)) {
            return toData(workbook, rowProcessor);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> List<T> toData(String filePath, Function<List<Object>, T> rowProcessor) {
        if (StringUtils.isBlank(filePath)) return null;
        try (Workbook workbook = toWorkbook(filePath)) {
            return toData(workbook, rowProcessor);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> List<T> toData(byte[] file, Function<List<Object>, T> rowProcessor) {
        if (file == null) return null;
        try (Workbook workbook = toWorkbook(file)) {
            return toData(workbook, rowProcessor);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> void writeData(Workbook workbook, List<Object> header, List<T> data, Function<T, List<Object>> rowProcessor) {
        if (workbook == null) return;
        Sheet sheet = getSheet(workbook);
        if (CollectionUtils.isNotEmpty(header)) {
            ExcelUtils.setCellValues(sheet, 0, header);
        }
        if (CollectionUtils.isNotEmpty(data) && rowProcessor != null) {
            int idx = 1;
            for (T item : data) {
                ExcelUtils.setCellValues(sheet, idx, rowProcessor.apply(item));
                idx++;
            }
        }
    }

    public static <T> byte[] writeDataToBytes(List<Object> header, List<T> data, Function<T, List<Object>> rowProcessor) {
        try (Workbook workbook = new SXSSFWorkbook()) {
            writeData(workbook, header, data, rowProcessor);
            return toBytes(workbook);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> void writeDataToFile(String filePath, List<Object> header, List<T> data, Function<T, List<Object>> rowProcessor) {
        try (Workbook workbook = new SXSSFWorkbook()) {
            writeData(workbook, header, data, rowProcessor);
            writeFile(workbook, filePath);
        } catch (Exception ignored) {
        }
    }
}