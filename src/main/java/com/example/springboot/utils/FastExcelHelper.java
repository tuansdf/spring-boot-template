package com.example.springboot.utils;

import com.example.springboot.exception.CustomException;
import com.example.springboot.modules.report.ImportTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Slf4j
public class FastExcelHelper {

    private static List<Object> getRowCellValues(Row row) {
        List<Object> rowData = new ArrayList<>();
        for (Cell cell : row) {
            rowData.add(cell.getValue());
        }
        return rowData;
    }

    public static class Import {
        public static <T> List<T> processTemplate(ImportTemplate<T> template, ReadableWorkbook workbook) {
            List<T> result = new ArrayList<>();

            if (template == null || CollectionUtils.isEmpty(template.getHeader()))
                return result;

            var header = template.getHeader();
            var rowExtractor = template.getRowExtractor();

            try {
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
            try (FileInputStream inputStream = new FileInputStream(Paths.get(filePath).toFile());
                 ReadableWorkbook workbook = new ReadableWorkbook(inputStream)) {
                return processTemplate(template, workbook);
            } catch (Exception e) {
                log.error("processTemplate", e);
                return new ArrayList<>();
            }
        }

        public static <T> List<T> processTemplate(ImportTemplate<T> template, byte[] bytes) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                 ReadableWorkbook workbook = new ReadableWorkbook(inputStream)) {
                return processTemplate(template, workbook);
            } catch (Exception e) {
                log.error("processTemplate", e);
                return new ArrayList<>();
            }
        }

        public static <T> List<T> processTemplate(ImportTemplate<T> template, MultipartFile file) {
            try (ReadableWorkbook workbook = new ReadableWorkbook(file.getInputStream())) {
                return processTemplate(template, workbook);
            } catch (Exception e) {
                log.error("processTemplate", e);
                return new ArrayList<>();
            }
        }
    }

}
