package com.example.sbt.shared.util;

import com.github.pjfanning.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExcelUtils {

    public static final int BUFFER_SIZE = 4096;
    public static final int ROW_CACHE_SIZE = 100;
    private static final String DEFAULT_SHEET = "Sheet1";

    public static void writeFile(Workbook workbook, String outputPath) {
        if (workbook == null || StringUtils.isBlank(outputPath)) return;
        try (workbook; FileOutputStream outputStream = new FileOutputStream(outputPath);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            workbook.write(bufferedOutputStream);
        } catch (Exception e) {
            log.error("writeFile ", e);
        }
    }

    public static byte[] toBytes(Workbook workbook) {
        if (workbook == null) return null;
        try (workbook; ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("toBytes ", e);
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
                case BOOLEAN -> cell.getBooleanCellValue();
                case NUMERIC -> cell.getNumericCellValue();
                case STRING -> cell.getStringCellValue();
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static Object getCellValue(Row row, Integer colIdx) {
        if (row == null || colIdx == null) return null;
        return getCellValue(getCell(row, colIdx));
    }

    public static Object getCellValue(Sheet sheet, Integer rowIdx, Integer colIdx) {
        if (sheet == null || rowIdx == null || colIdx == null) return null;
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
            case Boolean v -> cell.setCellValue(v);
            case String v -> cell.setCellValue(v);
            case Double v -> cell.setCellValue(v);
            case Integer v -> cell.setCellValue(v);
            case Long v -> cell.setCellValue(v);
            case Number v -> {
                Double vDouble = ConversionUtils.toDouble(v);
                if (vDouble != null) cell.setCellValue(vDouble);
            }
            default -> {
            }
        }
    }

    public static void setCellValue(Sheet sheet, Integer rowIdx, Integer colIdx, Object value) {
        if (sheet == null || rowIdx == null || colIdx == null || value == null) return;
        setCellValue(getCell(getRow(sheet, rowIdx), colIdx), value);
    }

    public static void setCellValue(Row row, Integer colIdx, Object value) {
        if (row == null || colIdx == null || value == null) return;
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
        if (sheet == null || rowIdx == null || CollectionUtils.isEmpty(objects)) return;
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
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            return StreamingReader.builder().rowCacheSize(ROW_CACHE_SIZE).bufferSize(BUFFER_SIZE).open(inputStream);
        } catch (Exception e) {
            log.error("toWorkbook ", e);
            return null;
        }
    }

    public static Workbook toWorkbook(byte[] bytes) {
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            return StreamingReader.builder().rowCacheSize(ROW_CACHE_SIZE).bufferSize(BUFFER_SIZE).open(inputStream);
        } catch (Exception e) {
            log.error("toWorkbook ", e);
            return null;
        }
    }

    public static Workbook toWorkbook(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return StreamingReader.builder().rowCacheSize(ROW_CACHE_SIZE).bufferSize(BUFFER_SIZE).open(inputStream);
        } catch (Exception e) {
            log.error("toWorkbook ", e);
            return null;
        }
    }

}