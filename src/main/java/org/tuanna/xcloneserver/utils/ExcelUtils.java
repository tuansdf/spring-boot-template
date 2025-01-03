package org.tuanna.xcloneserver.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;
import org.tuanna.xcloneserver.dtos.ExcelCellConfig;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExcelUtils {

    public static void exportReport(List<ExcelCellConfig> excelCellConfigs, String outputPath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        CellStyle centerCellStyle = workbook.createCellStyle();
        centerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        centerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        for (ExcelCellConfig config : excelCellConfigs) {
            try {
                if (config.getValue() == null) continue;

                boolean isList = config.getValue() instanceof List<?>;
                if (!isList) {
                    if (config.getColMergedTo() >= 0 || config.getRowMergedTo() >= 0) {
                        int toCol = config.getColMergedTo() >= 0 ? config.getColMergedTo() : config.getCol();
                        int toRow = config.getRowMergedTo() >= 0 ? config.getRowMergedTo() : config.getRow();
                        CellRangeAddress mergedRegion = new CellRangeAddress(config.getRow(), toRow, config.getCol(), toCol);
                        sheet.addMergedRegion(mergedRegion);
                    }
                    Cell cell = getCell(getRow(sheet, config.getRow()), config.getCol());
                    cell.setCellValue(CommonUtils.safeToString(config.getValue()));
                    cell.setCellStyle(centerCellStyle);
                    continue;
                }

                if (CollectionUtils.isEmpty(config.getMapper())) continue;

                List<?> dataList = (List<?>) config.getValue();
                if (CollectionUtils.isEmpty(dataList)) continue;

                List<String> mapperKeys = new ArrayList<>(config.getMapper().keySet());
                Class<?> dataClass = dataList.getFirst().getClass();
                for (int i = 0; i < dataList.size(); i++) {
                    Object data = dataList.get(i);
                    Row row = getRow(sheet, config.getRow() + i);
                    for (String key : mapperKeys) {
                        int col = config.getCol() + config.getMapper().get(key);
                        String value = CommonUtils.safeToString(dataClass.getMethod(key).invoke(data));
                        getCell(row, col).setCellValue(value);
                    }
                }
            } catch (Exception e) {
                log.error("error for cell {}", config, e);
            }
        }

        FileOutputStream outputStream = new FileOutputStream(outputPath);
        workbook.write(outputStream);

        outputStream.close();
        workbook.close();

        System.out.println("Report exported to " + outputPath);
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