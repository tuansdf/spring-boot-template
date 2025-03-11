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

    private static final List<String> header = List.of("Order", "ID", "Username", "Email", "Name", "Status", "Created At", "Updated At");
    private static final Function<List<Object>, UserDTO> rowPreProcessor = row -> {
        UserDTO result = new UserDTO();
        result.setId(ConversionUtils.toUUID(row.get(1)));
        result.setUsername(ConversionUtils.safeToString(row.get(2)));
        result.setEmail(ConversionUtils.safeToString(row.get(3)));
        result.setName(ConversionUtils.safeToString(row.get(4)));
        result.setStatus(ConversionUtils.toInt(row.get(5)));
        result.setCreatedAt(DateUtils.toInstant(row.get(8)));
        result.setUpdatedAt(DateUtils.toInstant(row.get(9)));
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
