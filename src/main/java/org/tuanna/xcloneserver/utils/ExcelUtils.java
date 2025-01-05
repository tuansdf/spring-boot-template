package org.tuanna.xcloneserver.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.tuanna.xcloneserver.modules.report.ExportTemplate;
import org.tuanna.xcloneserver.modules.report.ImportTemplate;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExcelUtils {

    private static final String DEFAULT_SHEET = "Sheet1";
    private static final int DEFAULT_HEADER_ROW = 0;
    private static final int DEFAULT_BODY_ROW = 1;

    public static <T> void exportTemplate(Workbook workbook, ExportTemplate<T> template) {
        try {
            if (template == null || workbook == null || CollectionUtils.isEmpty(template.getHeader()) || CollectionUtils.isEmpty(template.getBody()))
                return;

            var header = template.getHeader();
            var body = template.getBody();
            var rowExtractor = template.getRowExtractor();

            Sheet sheet = getSheet(workbook);
            setRowCellValue(getRow(sheet, DEFAULT_HEADER_ROW), header);

            for (int i = 0; i < body.size(); i++) {
                T data = body.get(i);
                Row row = getRow(sheet, DEFAULT_BODY_ROW + i);
                setRowCellValue(row, rowExtractor.apply(data));
            }
        } catch (Exception e) {
            log.error("exportTemplate", e);
        }
    }

    public static <T> Workbook exportTemplate(ExportTemplate<T> template) {
        try {
            Workbook workbook = new SXSSFWorkbook();
            exportTemplate(workbook, template);
            return workbook;
        } catch (Exception e) {
            log.error("exportTemplate", e);
            return null;
        }
    }

    public static <T> void exportTemplateToFile(ExportTemplate<T> template, String outputPath) {
        try {
            writeFile(exportTemplate(template), outputPath);
        } catch (Exception e) {
            log.error("exportTemplateToFile", e);
        }
    }

    public static <T> List<T> importTemplate(ImportTemplate<T> template, String inputPath) {
        List<T> result = new ArrayList<>();

        if (template == null || CollectionUtils.isEmpty(template.getHeader()) || StringUtils.isEmpty(inputPath))
            return result;

        var header = template.getHeader();
        var rowExtractor = template.getRowExtractor();

        try {
            try (FileInputStream inputStream = new FileInputStream(Paths.get(inputPath).toFile());
                 Workbook workbook = new XSSFWorkbook(inputStream)) {

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
            }
        } catch (Exception e) {
            log.error("importTemplate", e);
        }

        return result;
    }

    public static <T> byte[] exportTemplateToBytes(ExportTemplate<T> exportTemplate) {
        try {
            return toBytes(exportTemplate(exportTemplate));
        } catch (Exception e) {
            log.error("exportTemplateToBytes", e);
            return new byte[0];
        }
    }

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

    private static List<String> getRowCellValues(Row row) {
        List<String> rowData = new ArrayList<>();
        for (Cell cell : row) {
            rowData.add(cell.getStringCellValue());
        }
        return rowData;
    }

    private static <T> void setRowCellValue(Row row, List<T> objects) {
        for (int i = 0; i < objects.size(); i++) {
            getCell(row, i).setCellValue(CommonUtils.safeToString(objects.get(i)));
        }
    }

    private static <T> void setRowCellValue(Row row, T[] objects) {
        for (int i = 0; i < objects.length; i++) {
            getCell(row, i).setCellValue(CommonUtils.safeToString(objects[i]));
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

}