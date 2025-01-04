package org.tuanna.xcloneserver.modules.excel;

import java.util.List;
import java.util.function.Function;

public interface ReportTemplate<T> {

    String[] getHeader();

    List<T> getBody();

    Function<T, Object[]> getRowExtractor();

}
