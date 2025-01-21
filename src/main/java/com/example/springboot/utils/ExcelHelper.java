package com.example.springboot.utils;

import com.example.springboot.modules.report.ExportTemplate;
import com.example.springboot.modules.report.ImportTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Paths;
import java.sql.Time;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class ExcelHelper {

    private static final String DEFAULT_SHEET = "Sheet1";
    private static final int DEFAULT_HEADER_ROW = 0;
    private static final int DEFAULT_BODY_ROW = 1;

    public static void writeFile(Workbook workbook, String outputPath) {
        try {
            if (workbook == null || StringUtils.isEmpty(outputPath)) return;
            try (FileOutputStream outputStream = new FileOutputStream(outputPath);
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
                workbook.write(bufferedOutputStream);
            }
        } catch (Exception e) {
            log.error("writeFile", e);
        } finally {
            try {
                if (workbook != null) workbook.close();
            } catch (Exception ignored) {
            }
        }
    }

    public static byte[] toBytes(Workbook workbook) {
        try {
            if (workbook == null) return new byte[0];
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            log.error("toBytes", e);
            return new byte[0];
        } finally {
            try {
                if (workbook != null) workbook.close();
            } catch (Exception ignored) {
            }
        }
    }

    private static List<Object> getRowCellValues(Row row) {
        List<Object> rowData = new ArrayList<>();
        for (Cell cell : row) {
            CellType cellType = cell.getCellType();
            switch (cellType) {
                case BOOLEAN -> rowData.add(cell.getBooleanCellValue());
                case NUMERIC -> {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        rowData.add(cell.getLocalDateTimeCellValue());
                    } else {
                        rowData.add(cell.getNumericCellValue());
                    }
                }
                case STRING -> rowData.add(cell.getStringCellValue());
            }
        }
        return rowData;
    }

    private static <T> void setRowCellValue(Row row, List<T> objects) {
        for (int i = 0; i < objects.size(); i++) {
            setCellValue(getCell(row, i), objects.get(i));
        }
    }

    private static <T> void setRowCellValue(Row row, List<T> objects, List<CellStyle> styles) {
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i) == null) continue;
            Cell cell = getCell(row, i);
            setCellValue(cell, objects.get(i));
            if (styles == null || styles.get(i) == null) continue;
            cell.setCellStyle(styles.get(i));
        }
    }

    private static <T> void setRowCellValue(Row row, T[] objects) {
        for (int i = 0; i < objects.length; i++) {
            setCellValue(getCell(row, i), ConversionUtils.toString(objects[i]));
        }
    }

    private static void setCellValue(Cell cell, Object value) {
        if (cell == null) return;
        switch (value) {
            case null -> cell.setCellValue("");
            case String v -> cell.setCellValue(v);
            case Double v -> cell.setCellValue(v);
            case Integer v -> cell.setCellValue(v);
            case Long v -> cell.setCellValue(v);
            case Boolean v -> cell.setCellValue(v);
            case Time v -> cell.setCellValue(v);
            case Date v -> cell.setCellValue(v);
            case LocalTime v -> cell.setCellValue(v.toString());
            case LocalDate v -> cell.setCellValue(v);
            case LocalDateTime v -> cell.setCellValue(v);
            case ZonedDateTime v -> cell.setCellValue(v.toLocalDateTime());
            case OffsetDateTime v -> cell.setCellValue(v.toLocalDateTime());
            default -> cell.setCellValue(value.toString());
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

    public static class Export {
        public static <T> void processTemplate(ExportTemplate<T> template, Workbook workbook) {
            try {
                if (template == null || workbook == null || CollectionUtils.isEmpty(template.getHeader()) ||
                        CollectionUtils.isEmpty(template.getBody()) || template.getRowDataExtractor(false) == null)
                    return;

                Sheet sheet = getSheet(workbook);
                if (sheet.getLastRowNum() < 0) {
                    setRowCellValue(getRow(sheet, DEFAULT_HEADER_ROW), template.getHeader());
                }

                List<CellStyle> styles = template.getColStyles(workbook);

                var body = template.getBody();
                var rowDataExtractor = template.getRowDataExtractor(false);
                int fromRow = sheet.getLastRowNum() + 1;
                for (int i = 0; i < body.size(); i++) {
                    T item = body.get(i);
                    Row row = getRow(sheet, fromRow + i);
                    setRowCellValue(row, rowDataExtractor.apply(item), styles);
                }
            } catch (Exception e) {
                log.error("processTemplate", e);
            }
        }

        public static <T> Workbook processTemplate(ExportTemplate<T> template) {
            try {
                Workbook workbook = new SXSSFWorkbook();
                processTemplate(template, workbook);
                return workbook;
            } catch (Exception e) {
                log.error("processTemplate", e);
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
        public static <T> List<T> processTemplate(ImportTemplate<T> template, Workbook workbook) {
            List<T> result = new ArrayList<>();

            if (template == null || CollectionUtils.isEmpty(template.getHeader()))
                return result;

            var header = template.getHeader();
            var rowExtractor = template.getRowExtractor();

            try {
                Sheet sheet = workbook.getSheetAt(0);

                if (sheet.getLastRowNum() == 0) return result;

                Row headerRow = sheet.getRow(0);
                if (headerRow.getLastCellNum() != header.size()) return result;
                for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                    if (!header.get(i).equals(headerRow.getCell(i).getStringCellValue())) return result;
                }

                for (int i = 1; i < sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    result.add(rowExtractor.apply(getRowCellValues(row)));
                }
            } catch (Exception e) {
                log.error("processTemplate", e);
            }

            return result;
        }

        public static <T> List<T> processTemplate(ImportTemplate<T> template, String filePath) {
            try (FileInputStream inputStream = new FileInputStream(Paths.get(filePath).toFile());
                 Workbook workbook = new XSSFWorkbook(inputStream)) {
                return processTemplate(template, workbook);
            } catch (Exception e) {
                log.error("processTemplate", e);
                return new ArrayList<>();
            }
        }

        public static <T> List<T> processTemplate(ImportTemplate<T> template, byte[] bytes) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                 Workbook workbook = new XSSFWorkbook(inputStream)) {
                return processTemplate(template, workbook);
            } catch (Exception e) {
                log.error("processTemplate", e);
                return new ArrayList<>();
            }
        }

        public static <T> List<T> processTemplate(ImportTemplate<T> template, MultipartFile file) {
            try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
                return processTemplate(template, workbook);
            } catch (Exception e) {
                log.error("processTemplate", e);
                return new ArrayList<>();
            }
        }
    }

}