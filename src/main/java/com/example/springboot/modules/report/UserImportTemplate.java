package com.example.springboot.modules.report;

import com.example.springboot.modules.user.dtos.UserDTO;
import com.example.springboot.utils.ConversionUtils;
import com.example.springboot.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Function;

@Slf4j
public class UserImportTemplate implements ImportTemplate<UserDTO> {

    private static final List<String> HEADER = List.of("ID", "Username", "Email", "Name", "Status", "Created By", "Updated By", "Created At", "Updated At");
    private static final Function<List<Object>, UserDTO> ROW_EXTRACTOR = row -> {
        UserDTO result = new UserDTO();
        result.setId(ConversionUtils.toUUID(row.get(0)));
        result.setUsername(ConversionUtils.toString(row.get(1)));
        result.setEmail(ConversionUtils.toString(row.get(2)));
        result.setName(ConversionUtils.toString(row.get(3)));
        result.setStatus(ConversionUtils.toString(row.get(4)));
        result.setCreatedBy(ConversionUtils.toUUID(row.get(5)));
        result.setUpdatedBy(ConversionUtils.toUUID(row.get(6)));
        result.setCreatedAt(DateUtils.toOffsetDateTime(row.get(7)));
        result.setUpdatedAt(DateUtils.toOffsetDateTime(row.get(8)));
        return result;
    };

    @Override
    public List<String> getHeader() {
        return HEADER;
    }

    @Override
    public Function<List<Object>, UserDTO> getRowExtractor() {
        return ROW_EXTRACTOR;
    }

}
