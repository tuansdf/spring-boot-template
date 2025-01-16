package com.example.springboot.modules.report;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;
import java.util.function.Function;

public interface ExportTemplate<T> {

    List<String> getHeader();

    List<T> getBody();

    Function<T, List<Object>> getRowDataExtractor(boolean formatAsString);

    List<CellStyle> getColStyles(Workbook workbook);

}
