package org.tuanna.xcloneserver.modules.report;

import java.util.List;
import java.util.function.Function;

public interface ImportTemplate<T> {

    List<String> getHeader();

    Function<List<Object>, T> getRowExtractor();

}
