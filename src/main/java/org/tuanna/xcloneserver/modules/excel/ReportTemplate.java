package org.tuanna.xcloneserver.modules.excel;

import java.util.List;
import java.util.Map;

public interface ReportTemplate {

    List<String> getHeader();

    List<?> getBody();

    Map<String, Integer> getMapper();

}
