package org.tuanna.xcloneserver.modules.report;

import org.tuanna.xcloneserver.dtos.TestUser;
import org.tuanna.xcloneserver.utils.DateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class TestUserExportTemplate implements ExportTemplate<TestUser> {

    private static final List<String> header = List.of("ID", "Username", "Email", "Name", "Address", "Street", "City", "Country", "Created At", "Updated At");
    private static final Function<TestUser, List<Object>> rowExtractor = user -> new ArrayList<>(Arrays.asList(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getName(),
            user.getAddress(),
            user.getStreet(),
            user.getCity(),
            user.getCountry(),
            DateUtils.toFormat(user.getCreatedAt(), DateUtils.Formatter.DATE_TIME_BE),
            DateUtils.toFormat(user.getUpdatedAt(), DateUtils.Formatter.DATE_TIME_BE)));

    private final List<TestUser> body;

    public TestUserExportTemplate(List<TestUser> body) {
        this.body = body;
    }

    @Override
    public List<String> getHeader() {
        return header;
    }

    @Override
    public List<TestUser> getBody() {
        return body;
    }

    @Override
    public Function<TestUser, List<Object>> getRowExtractor() {
        return rowExtractor;
    }

}
