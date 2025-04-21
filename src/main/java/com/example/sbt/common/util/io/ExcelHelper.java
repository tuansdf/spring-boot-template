package com.example.sbt.common.util.io;

import com.example.sbt.common.exception.InvalidImportTemplateException;
import com.example.sbt.common.util.CommonUtils;
import com.example.sbt.common.util.ConversionUtils;
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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExcelHelper {

    private static final String DEFAULT_SHEET = "Sheet1";
    private static final int DEFAULT_HEADER_ROW = 0;
    private static final int DEFAULT_BODY_ROW = 1;

    public static void writeFile(Workbook workbook, String outputPath) {
        try {
            if (workbook == null || StringUtils.isBlank(outputPath)) return;
            try (workbook; FileOutputStream outputStream = new FileOutputStream(outputPath);
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
                workbook.write(bufferedOutputStream);
            }
        } catch (Exception e) {
            log.error("writeFile", e);
        }
    }

    public static byte[] toBytes(Workbook workbook) {
        try {
            if (workbook == null) return new byte[0];
            try (workbook; ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            log.error("toBytes", e);
            return new byte[0];
        }
    }

    private static Sheet getSheet(Workbook workbook) {
        Sheet result = workbook.getSheet(DEFAULT_SHEET);
        if (result == null) {
            result = workbook.createSheet(DEFAULT_SHEET);
        }
        return result;
    }

    private static Row getRow(Sheet sheet, int i) {
        Row result = sheet.getRow(i);
        if (result == null) {
            result = sheet.createRow(i);
        }
        return result;
    }

    private static Cell getCell(Row row, int i) {
        Cell result = row.getCell(i);
        if (result == null) {
            result = row.createCell(i);
        }
        return result;
    }

    private static Object getCellValue(Cell cell) {
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

    private static Object getCellValue(Row row, int col) {
        try {
            if (row == null) return null;
            return getCellValue(row.getCell(col));
        } catch (Exception e) {
            return null;
        }
    }

    private static List<Object> getRowCellValues(Row row) {
        List<Object> rowData = new ArrayList<>();
        for (Cell cell : row) {
            rowData.add(getCellValue(cell));
        }
        return rowData;
    }

    private static void setCellValue(Cell cell, Object value) {
        if (cell == null || value == null) return;
        switch (value) {
            case String v -> cell.setCellValue(v);
            case Double v -> cell.setCellValue(v);
            case Integer v -> cell.setCellValue(v);
            case Long v -> cell.setCellValue(v);
            case Number v -> {
                Double vDouble = ConversionUtils.toDouble(v);
                if (vDouble != null) cell.setCellValue(vDouble);
            }
            case Boolean v -> cell.setCellValue(v);
            default -> cell.setCellValue(value.toString());
        }
    }

    private static void setRowCellValue(Row row, List<Object> objects) {
        if (CollectionUtils.isEmpty(objects)) return;
        for (int i = 0; i < objects.size(); i++) {
            setCellValue(getCell(row, i), objects.get(i));
        }
    }

    private static <T> void setRowCellValue(Row row, T[] objects) {
        if (ArrayUtils.isEmpty(objects)) return;
        for (int i = 0; i < objects.length; i++) {
            setCellValue(getCell(row, i), ConversionUtils.safeToString(objects[i]));
        }
    }

    private static void setCellValue(Sheet sheet, int row, int col, Object value) {
        setCellValue(getCell(getRow(sheet, row), col), value);
    }

    public static class Export {
        public static <T> void processTemplate(ExportTemplate<T> template, Workbook workbook) {
            try {
                Sheet sheet = getSheet(workbook);
                if (template.getHeader() != null) {
                    setRowCellValue(getRow(sheet, sheet.getLastRowNum() + 1), template.getHeader());
                }

                var body = template.getBody();
                var rowDataExtractor = template.getRowExtractor();
                int fromRow = sheet.getLastRowNum() + 1;
                int i = 0;
                for (T item : body) {
                    int rowNum = fromRow + i;
                    Row row = getRow(sheet, rowNum);
                    setRowCellValue(row, rowDataExtractor.apply(item, rowNum));
                    i++;
                }
            } catch (Exception e) {
                log.error("processTemplate ", e);
            }
        }

        public static <T> Workbook processTemplate(ExportTemplate<T> template) {
            try {
                Workbook workbook = new SXSSFWorkbook();
                processTemplate(template, workbook);
                return workbook;
            } catch (Exception e) {
                log.error("processTemplate ", e);
                return null;
            }
        }

        public static <T> void processTemplateWriteFile(ExportTemplate<T> template, String outputPath) {
            try {
                writeFile(processTemplate(template), outputPath);
            } catch (Exception e) {
                log.error("processTemplateWriteFile", e);
            }
        }

        public static <T> byte[] processTemplateToBytes(ExportTemplate<T> exportTemplate) {
            try {
                return toBytes(processTemplate(exportTemplate));
            } catch (Exception e) {
                log.error("processTemplateToBytes", e);
                return new byte[0];
            }
        }
    }

    public static class Import {
        public static final int BUFFER_SIZE = 4096;
        public static final int ROW_CACHE_SIZE = 10000;

        public static <T> void processTemplate(ImportTemplate<T> template, Workbook workbook) {
            try {
                var header = template.getHeader();
                var rowPreProcessor = template.getRowPreProcessor();
                var rowProcessor = template.getRowProcessor();
                var rowSize = header.size();

                Sheet sheet = workbook.getSheetAt(0);

                int rowIdx = 0;
                for (Row row : sheet) {
                    if (rowIdx == 0) {
                        if (row.getLastCellNum() != rowSize) {
                            throw new InvalidImportTemplateException();
                        }
                        for (int colIdx = 0; colIdx < rowSize; colIdx++) {
                            if (!header.get(colIdx).equals(getCellValue(row, colIdx))) {
                                throw new InvalidImportTemplateException();
                            }
                        }
                    } else {
                        var item = rowPreProcessor.apply(CommonUtils.rightPad(getRowCellValues(row), rowSize));
                        if (rowProcessor != null) {
                            rowProcessor.accept(item);
                        }
                    }

                    rowIdx++;
                }
            } catch (Exception e) {
                log.error("processTemplate ", e);
            }
        }

        public static <T> void processTemplate(ImportTemplate<T> template, String filePath) {
            try (FileInputStream inputStream = new FileInputStream(Paths.get(filePath).toFile());
                 Workbook workbook = StreamingReader.builder().rowCacheSize(ROW_CACHE_SIZE).open(inputStream)) {
                processTemplate(template, workbook);
            } catch (Exception e) {
                log.error("processTemplate ", e);
            }
        }

        public static <T> void processTemplate(ImportTemplate<T> template, byte[] bytes) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                 Workbook workbook = StreamingReader.builder().rowCacheSize(ROW_CACHE_SIZE).bufferSize(BUFFER_SIZE).open(inputStream)) {
                processTemplate(template, workbook);
            } catch (Exception e) {
                log.error("processTemplate ", e);
            }
        }

        public static <T> void processTemplate(ImportTemplate<T> template, MultipartFile file) {
            try (Workbook workbook = StreamingReader.builder().rowCacheSize(ROW_CACHE_SIZE).bufferSize(BUFFER_SIZE).open(file.getInputStream())) {
                processTemplate(template, workbook);
            } catch (Exception e) {
                log.error("processTemplate ", e);
            }
        }
    }

}