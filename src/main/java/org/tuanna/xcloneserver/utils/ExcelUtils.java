package org.tuanna.xcloneserver.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.tuanna.xcloneserver.modules.excel.ExportTemplate;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class ExcelUtils {

    private static final String DEFAULT_SHEET = "Sheet1";
    private static final int DEFAULT_HEADER_ROW = 0;
    private static final int DEFAULT_BODY_ROW = 1;

    public static <T> void processTemplate(Workbook workbook, ExportTemplate<T> template) {
        try {
            if (template == null || workbook == null || ArrayUtils.isEmpty(template.getHeader()) || CollectionUtils.isEmpty(template.getBody()))
                return;

            String[] header = template.getHeader();
            List<T> body = template.getBody();
            Function<T, Object[]> rowExtractor = template.getRowExtractor();

            Sheet sheet = getSheet(workbook);
            setRowCellValue(getRow(sheet, DEFAULT_HEADER_ROW), header);

            for (int i = 0; i < body.size(); i++) {
                T data = body.get(i);
                Row row = getRow(sheet, DEFAULT_BODY_ROW + i);
                setRowCellValue(row, rowExtractor.apply(data));
            }
        } catch (Exception e) {
            log.error("processTemplate", e);
        }
    }

    public static <T> Workbook processTemplate(ExportTemplate<T> exportTemplate) {
        try {
            Workbook workbook = new SXSSFWorkbook();
            processTemplate(workbook, exportTemplate);
            return workbook;
        } catch (Exception e) {
            log.error("processTemplate", e);
            return null;
        }
    }

    public static <T> void processTemplateToFile(ExportTemplate<T> exportTemplate, String outputPath) {
        try {
            writeFile(processTemplate(exportTemplate), outputPath);
        } catch (Exception e) {
            log.error("processTemplateToFile", e);
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