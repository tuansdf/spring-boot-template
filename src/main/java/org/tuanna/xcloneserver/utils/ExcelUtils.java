package org.tuanna.xcloneserver.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.tuanna.xcloneserver.modules.excel.ReportTemplate;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExcelUtils {

    private static final String DEFAULT_SHEET = "Sheet1";
    private static final int DEFAULT_HEADER_START_AT_ROW = 0;
    private static final int DEFAULT_BODY_START_AT_ROW = 1;

    public static void processTemplate(Workbook workbook, ReportTemplate reportTemplate) {
        try {
            if (CollectionUtils.isEmpty(reportTemplate.getHeader()) || CollectionUtils.isEmpty(reportTemplate.getBody()) || CollectionUtils.isEmpty(reportTemplate.getMapper()))
                return;

            Sheet sheet = getSheet(workbook);

            for (int i = 0; i < reportTemplate.getHeader().size(); i++) {
                getCell(getRow(sheet, DEFAULT_HEADER_START_AT_ROW), i).setCellValue(reportTemplate.getHeader().get(i));
            }

            Class<?> dataClass = reportTemplate.getBody().getFirst().getClass();
            Map<String, Method> methods = new HashMap<>();
            for (String key : new ArrayList<>(reportTemplate.getMapper().keySet())) {
                try {
                    methods.put(key, dataClass.getMethod("get" + StringUtils.capitalize(key)));
                } catch (Exception ignored) {
                }
            }
            List<String> mapperKeys = new ArrayList<>(methods.keySet());
            if (CollectionUtils.isEmpty(mapperKeys)) return;

            for (int i = 0; i < reportTemplate.getBody().size(); i++) {
                Object data = reportTemplate.getBody().get(i);
                Row row = getRow(sheet, DEFAULT_BODY_START_AT_ROW + i);
                for (String key : mapperKeys) {
                    int col = reportTemplate.getMapper().get(key);
                    String value = CommonUtils.safeToString(methods.get(key).invoke(data));
                    getCell(row, col).setCellValue(value);
                }
            }
        } catch (Exception e) {
            log.error("processtemplate", e);
        }
    }

    public static byte[] processTemplate(ReportTemplate reportTemplate) {
        try {
            if (CollectionUtils.isEmpty(reportTemplate.getHeader()) || CollectionUtils.isEmpty(reportTemplate.getBody()) || CollectionUtils.isEmpty(reportTemplate.getMapper()))
                return new byte[]{};

            Workbook workbook = new XSSFWorkbook();
            processTemplate(workbook, reportTemplate);
            return toBytes(workbook);
        } catch (Exception e) {
            log.error("processtemplate", e);
            return new byte[]{};
        }
    }

    public static void processTemplate(ReportTemplate reportTemplate, String outputPath) {
        try {
            byte[] bytes = processTemplate(reportTemplate);
            writeFile(bytes, outputPath);
        } catch (Exception e) {
            log.error("processtemplate", e);
        }
    }

    public static void writeFile(Workbook workbook, String outputPath) {
        try {
            if (workbook == null) return;
            try (FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                workbook.write(outputStream);
                workbook.close();
            }
        } catch (Exception e) {
            log.error("writefile", e);
        }
    }

    public static void writeFile(byte[] bytes, String outputPath) {
        try {
            if (bytes == null) return;
            try (FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                outputStream.write(bytes);
            }
        } catch (Exception e) {
            log.error("writefile", e);
        }
    }

    public static byte[] toBytes(Workbook workbook) throws IOException {
        try {
            if (workbook == null) return new byte[]{};
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                workbook.close();
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            return new byte[]{};
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