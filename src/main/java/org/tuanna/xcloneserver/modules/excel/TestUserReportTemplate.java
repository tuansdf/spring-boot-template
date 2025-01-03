package org.tuanna.xcloneserver.modules.excel;

import java.util.List;
import java.util.Map;

public class TestUserReportTemplate implements ReportTemplate {

    private static final List<String> header = List.of("ID", "Username", "Email", "Name", "Address", "Street", "City", "Country");
    private static final Map<String, Integer> mapper = Map.of(
            "id", 0,
            "username", 1,
            "email", 2,
            "name", 3,
            "address", 4,
            "street", 5,
            "city", 6,
            "country", 7);

    private final List<?> body;

    public TestUserReportTemplate(List<?> body) {
        this.body = body;
    }

    @Override
    public List<String> getHeader() {
        return header;
    }

    @Override
    public List<?> getBody() {
        return body;
    }

    @Override
    public Map<String, Integer> getMapper() {
        return mapper;
    }

}
