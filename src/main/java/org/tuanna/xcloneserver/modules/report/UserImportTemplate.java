package org.tuanna.xcloneserver.modules.report;

import lombok.extern.slf4j.Slf4j;
import org.tuanna.xcloneserver.modules.user.dtos.UserDTO;
import org.tuanna.xcloneserver.utils.ConversionUtils;
import org.tuanna.xcloneserver.utils.DateUtils;

import java.util.List;
import java.util.function.Function;

@Slf4j
public class UserImportTemplate implements ImportTemplate<UserDTO> {

    private static final List<String> HEADER = List.of("ID", "Username", "Email", "Name", "Status", "Created By", "Updated By", "Created At", "Updated At");
    private static final Function<List<String>, UserDTO> ROW_EXTRACTOR = row -> {
        UserDTO result = new UserDTO();
        result.setId(ConversionUtils.safeToUUID(row.get(0)));
        result.setUsername(row.get(1));
        result.setEmail(row.get(2));
        result.setName(row.get(3));
        result.setStatus(row.get(4));
        result.setCreatedBy(ConversionUtils.safeToUUID(row.get(5)));
        result.setUpdatedBy(ConversionUtils.safeToUUID(row.get(6)));
        result.setCreatedAt(DateUtils.toZonedDateTime(row.get(7), DateUtils.Formatter.DATE_TIME_BE));
        result.setUpdatedAt(DateUtils.toZonedDateTime(row.get(8), DateUtils.Formatter.DATE_TIME_BE));
        return result;
    };

    @Override
    public List<String> getHeader() {
        return HEADER;
    }

    @Override
    public Function<List<String>, UserDTO> getRowExtractor() {
        return ROW_EXTRACTOR;
    }

}
