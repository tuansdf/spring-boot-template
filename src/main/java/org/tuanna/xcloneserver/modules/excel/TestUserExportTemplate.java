package org.tuanna.xcloneserver.modules.excel;

import org.tuanna.xcloneserver.dtos.TestUser;
import org.tuanna.xcloneserver.utils.DateUtils;

import java.util.List;
import java.util.function.Function;

public class TestUserExportTemplate implements ExportTemplate<TestUser> {

    private static final String[] header = new String[]{"ID", "Username", "Email", "Name", "Address", "Street", "City", "Country", "Created At", "Updated At"};
    private static final Function<TestUser, Object[]> rowExtractor = user -> new Object[]{
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getName(),
            user.getAddress(),
            user.getStreet(),
            user.getCity(),
            user.getCountry(),
            DateUtils.toFormat(user.getCreatedAt(), DateUtils.Formatter.DATE_TIME_BE),
            DateUtils.toFormat(user.getUpdatedAt(), DateUtils.Formatter.DATE_TIME_BE),
    };

    private final List<TestUser> body;

    public TestUserExportTemplate(List<TestUser> body) {
        this.body = body;
    }

    @Override
    public String[] getHeader() {
        return header;
    }

    @Override
    public List<TestUser> getBody() {
        return body;
    }

    @Override
    public Function<TestUser, Object[]> getRowExtractor() {
        return rowExtractor;
    }

}
