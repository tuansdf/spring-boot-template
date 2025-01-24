package com.example.springboot.modules.report;

import java.util.List;
import java.util.function.Function;

public interface ExportTemplate<T> {

    List<String> getHeader();

    List<T> getBody();

    Function<T, List<Object>> getRowExtractor();

}
