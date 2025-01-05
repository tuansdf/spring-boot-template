package org.tuanna.xcloneserver.modules.report;

import lombok.extern.slf4j.Slf4j;
import org.tuanna.xcloneserver.dtos.TestUser;
import org.tuanna.xcloneserver.utils.DateUtils;
import org.tuanna.xcloneserver.utils.UUIDUtils;

import java.util.List;
import java.util.function.Function;

@Slf4j
public class TestUserImportTemplate implements ImportTemplate<TestUser> {

    private static final List<String> HEADER = List.of("ID", "Username", "Email", "Name", "Address", "Street", "City", "Country", "Created At", "Updated At");
    private static final Function<List<String>, TestUser> ROW_EXTRACTOR = row -> {
        TestUser result = new TestUser();
        result.setId(UUIDUtils.fromString(row.get(0)));
        result.setUsername(row.get(1));
        result.setEmail(row.get(2));
        result.setName(row.get(3));
        result.setAddress(row.get(4));
        result.setStreet(row.get(5));
        result.setCity(row.get(6));
        result.setCountry(row.get(7));
        result.setCreatedAt(DateUtils.toZonedDateTime(row.get(8), DateUtils.Formatter.DATE_TIME_BE));
        result.setUpdatedAt(DateUtils.toZonedDateTime(row.get(9), DateUtils.Formatter.DATE_TIME_BE));
        return result;
    };

    @Override
    public List<String> getHeader() {
        return HEADER;
    }

    @Override
    public Function<List<String>, TestUser> getRowExtractor() {
        return ROW_EXTRACTOR;
    }

}
