package com.example.springboot.modules.report;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ImportTemplate<T> {

    List<String> getHeader();

    Function<List<Object>, T> getRowPreProcessor();

    Consumer<T> getRowProcessor();

}
