package org.tuanna.xcloneserver.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.tuanna.xcloneserver.modules.excel.ReportTemplate;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class ExcelUtils {

    private static final String DEFAULT_SHEET = "Sheet1";
    private static final int DEFAULT_HEADER_START_AT_ROW = 0;
    private static final int DEFAULT_BODY_START_AT_ROW = 1;

    public static <T> void processTemplate(Workbook workbook, ReportTemplate<T> reportTemplate) {
        try {
            String[] header = reportTemplate.getHeader();
            List<T> body = reportTemplate.getBody();
            if (workbook == null || CollectionUtils.isEmpty(body)) return;

            Sheet sheet = getSheet(workbook);

            setRowCellValue(getRow(sheet, DEFAULT_HEADER_START_AT_ROW), header);

            Function<T, Object[]> rowExtractor = reportTemplate.getRowExtractor();
            for (int i = 0; i < body.size(); i++) {
                T data = body.get(i);
                Row row = getRow(sheet, DEFAULT_BODY_START_AT_ROW + i);
                setRowCellValue(row, rowExtractor.apply(data));
            }
        } catch (Exception e) {
            log.error("processTemplate", e);
        }
    }

    public static <T> Workbook processTemplate(ReportTemplate<T> reportTemplate) {
        try {
            if (CollectionUtils.isEmpty(reportTemplate.getBody())) return null;

            Workbook workbook = new XSSFWorkbook();
            processTemplate(workbook, reportTemplate);
            return workbook;
        } catch (Exception e) {
            log.error("processTemplate", e);
            return null;
        }
    }

    public static <T> void processTemplateToFile(ReportTemplate<T> reportTemplate, String outputPath) {
        try {
            if (CollectionUtils.isEmpty(reportTemplate.getBody())) return;

            try (Workbook workbook = processTemplate(reportTemplate)) {
                writeFile(workbook, outputPath);
            }
        } catch (Exception e) {
            log.error("processTemplateToFile", e);
        }
    }

    public static <T> byte[] processTemplateToBytes(ReportTemplate<T> reportTemplate) {
        try {
            if (CollectionUtils.isEmpty(reportTemplate.getBody())) return new byte[]{};

            return toBytes(processTemplate(reportTemplate));
        } catch (Exception e) {
            log.error("processTemplateToBytes", e);
            return new byte[]{};
        }
    }

    public static void writeFile(Workbook workbook, String outputPath) {
        try {
            if (workbook == null) return;
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
            if (workbook == null) return new byte[]{};
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
                workbook.write(bufferedOutputStream);
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            return new byte[]{};
        } finally {
            try {
                if (workbook != null) workbook.close();
            } catch (Exception ignored) {
            }
        }
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