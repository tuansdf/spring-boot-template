package com.example.springboot.modules.report;

import com.example.springboot.modules.user.dtos.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserExportTemplate implements ExportTemplate<UserDTO> {

    private static final List<String> HEADER = List.of("ID", "Username", "Email", "Name", "Status", "Created By", "Updated By", "Created At", "Updated At");
    private static final Function<UserDTO, List<Object>> ROW_DATA_EXTRACTOR = user -> Arrays.asList(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getName(),
            user.getStatus(),
            user.getCreatedBy(),
            user.getUpdatedBy(),
            user.getCreatedAt(),
            user.getUpdatedAt());

    private List<UserDTO> body;

    @Override
    public List<String> getHeader() {
        return HEADER;
    }

    @Override
    public List<UserDTO> getBody() {
        return body;
    }

    @Override
    public Function<UserDTO, List<Object>> getRowDataExtractor() {
        return ROW_DATA_EXTRACTOR;
    }

}
