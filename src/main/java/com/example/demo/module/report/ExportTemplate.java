package com.example.demo.module.report;

import java.util.List;
import java.util.function.BiFunction;

public interface ExportTemplate<T> {

    List<String> getHeader();

    List<T> getBody();

    BiFunction<T, Integer, List<Object>> getRowExtractor();

}
