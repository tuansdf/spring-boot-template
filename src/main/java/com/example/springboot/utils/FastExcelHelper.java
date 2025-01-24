package com.example.springboot.utils;

import com.example.springboot.exception.CustomException;
import com.example.springboot.modules.report.ExportTemplate;
import com.example.springboot.modules.report.ImportTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Slf4j
public class FastExcelHelper {

    private static final String DEFAULT_SHEET = "Sheet1";
    private static final int DEFAULT_HEADER_ROW = 0;
    private static final int DEFAULT_BODY_ROW = 1;
    private static final String DEFAULT_APPLICATION_NAME = "Application";
    private static final String DEFAULT_APPLICATION_VERSION = "1.0";

    private static void setCellValue(Worksheet worksheet, int row, int col, Object value) {
        if (worksheet == null || value == null || row < 0 || col < 0) return;
        switch (value) {
            case String v -> worksheet.value(row, col, v);
            case Number v -> worksheet.value(row, col, v);
            case Boolean v -> worksheet.value(row, col, v);
            default -> worksheet.value(row, col, value.toString());
        }
    }

    private static <T> void setRowCellValue(Worksheet worksheet, int row, List<T> objects) {
        for (int i = 0; i < objects.size(); i++) {
            setCellValue(worksheet, row, i, objects.get(i));
        }
    }

    private static List<Object> getRowCellValues(Row row) {
        List<Object> rowData = new ArrayList<>();
        for (Cell cell : row) {
            rowData.add(cell.getValue());
        }
        return rowData;
    }

    public static class Export {
        public static <T> void processTemplate(ExportTemplate<T> template, OutputStream outputStream) {
            if (template == null || outputStream == null || CollectionUtils.isEmpty(template.getHeader()) ||
                    CollectionUtils.isEmpty(template.getBody()) || template.getRowDataExtractor() == null)
                return;

            try (Workbook workbook = new Workbook(outputStream, DEFAULT_APPLICATION_NAME, DEFAULT_APPLICATION_VERSION)) {
                Worksheet worksheet = workbook.newWorksheet(DEFAULT_SHEET);

                setRowCellValue(worksheet, DEFAULT_HEADER_ROW, template.getHeader());

                var body = template.getBody();
                var rowDataExtractor = template.getRowDataExtractor();
                for (int i = 0; i < body.size(); i++) {
                    T item = body.get(i);
                    setRowCellValue(worksheet, DEFAULT_BODY_ROW + i, rowDataExtractor.apply(item));
                }
            } catch (Exception e) {
                log.error("processTemplate ", e);
            }
        }

        public static <T> void processTemplateWriteFile(ExportTemplate<T> template, String outputPath) {
            try {
                FileUtils.writeFile(processTemplateToBytes(template), outputPath);
            } catch (Exception e) {
                log.error("processTemplateWriteFile", e);
            }
        }

        public static <T> byte[] processTemplateToBytes(ExportTemplate<T> template) {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                processTemplate(template, outputStream);
                return outputStream.toByteArray();
            } catch (Exception e) {
                log.error("processTemplateToBytes", e);
                return new byte[0];
            }
        }
    }

    public static class Import {
        public static <T> List<T> processTemplate(ImportTemplate<T> template, InputStream inputStream) {
            List<T> result = new ArrayList<>();

            if (template == null || CollectionUtils.isEmpty(template.getHeader()))
                return result;

            var header = template.getHeader();
            var rowExtractor = template.getRowExtractor();

            try (ReadableWorkbook workbook = new ReadableWorkbook(inputStream)) {
                Sheet sheet = workbook.getFirstSheet();
                AtomicInteger rowIdx = new AtomicInteger(0);
                try (Stream<Row> rows = sheet.openStream()) {
                    rows.forEach(row -> {
                        if (rowIdx.get() == 0) {
                            if (row.getCellCount() != header.size()) {
                                throw new CustomException("Invalid template");
                            }
                            for (int colIdx = 0; colIdx < row.getCellCount(); colIdx++) {
                                if (!header.get(colIdx).equals(row.getCell(colIdx).getValue())) {
                                    throw new CustomException("Invalid template");
                                }
                            }
                        } else {
                            result.add(rowExtractor.apply(getRowCellValues(row)));
                        }

                        rowIdx.incrementAndGet();
                    });
                }
            } catch (Exception e) {
                log.error("processTemplate ", e);
            }

            return result;
        }

        public static <T> List<T> processTemplate(ImportTemplate<T> template, String filePath) {
            try (FileInputStream inputStream = new FileInputStream(Paths.get(filePath).toFile())) {
                return processTemplate(template, inputStream);
            } catch (Exception e) {
                log.error("processTemplate ", e);
                return new ArrayList<>();
            }
        }

        public static <T> List<T> processTemplate(ImportTemplate<T> template, byte[] bytes) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
                return processTemplate(template, inputStream);
            } catch (Exception e) {
                log.error("processTemplate ", e);
                return new ArrayList<>();
            }
        }

        public static <T> List<T> processTemplate(ImportTemplate<T> template, MultipartFile file) {
            try {
                return processTemplate(template, file.getInputStream());
            } catch (Exception e) {
                log.error("processTemplate ", e);
                return new ArrayList<>();
            }
        }
    }

}
