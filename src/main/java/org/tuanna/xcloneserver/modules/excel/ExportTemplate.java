package org.tuanna.xcloneserver.modules.excel;

import java.util.List;
import java.util.function.Function;

public interface ExportTemplate<T> {

    String[] getHeader();

    List<T> getBody();

    Function<T, Object[]> getRowExtractor();

}
