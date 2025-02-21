package com.example.demo.modules.report;

import com.example.demo.modules.user.dtos.UserDTO;
import com.example.demo.utils.ConversionUtils;
import com.example.demo.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserImportTemplate implements ImportTemplate<UserDTO> {

    private static final List<String> header = List.of("ID", "Username", "Email", "Name", "Status", "Created By", "Updated By", "Created At", "Updated At");
    private static final Function<List<Object>, UserDTO> rowPreProcessor = row -> {
        UserDTO result = new UserDTO();
        result.setId(ConversionUtils.toUUID(row.get(0)));
        result.setUsername(ConversionUtils.safeToString(row.get(1)));
        result.setEmail(ConversionUtils.safeToString(row.get(2)));
        result.setName(ConversionUtils.safeToString(row.get(3)));
        result.setStatus(ConversionUtils.safeToString(row.get(4)));
        result.setCreatedBy(ConversionUtils.toUUID(row.get(5)));
        result.setUpdatedBy(ConversionUtils.toUUID(row.get(6)));
        result.setCreatedAt(DateUtils.toOffsetDateTime(row.get(7)));
        result.setUpdatedAt(DateUtils.toOffsetDateTime(row.get(8)));
        return result;
    };

    private Consumer<UserDTO> rowProcessor;

    @Override
    public List<String> getHeader() {
        return header;
    }

    @Override
    public Function<List<Object>, UserDTO> getRowPreProcessor() {
        return rowPreProcessor;
    }

    @Override
    public Consumer<UserDTO> getRowProcessor() {
        return rowProcessor;
    }

}
