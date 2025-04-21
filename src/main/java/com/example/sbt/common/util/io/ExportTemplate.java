package com.example.sbt.common.util.io;

import java.util.List;
import java.util.function.BiFunction;

public interface ExportTemplate<T> {

    List<Object> getHeader();

    List<T> getBody();

    BiFunction<T, Integer, List<Object>> getRowExtractor();

}
