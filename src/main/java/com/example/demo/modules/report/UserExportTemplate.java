package com.example.demo.modules.report;

import com.example.demo.modules.user.dtos.UserDTO;
import lombok.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserExportTemplate implements ExportTemplate<UserDTO> {

    private static final List<String> HEADER = List.of("Order", "ID", "Username", "Email", "Name", "Status", "Created At", "Updated At");
    private static final BiFunction<UserDTO, Integer, List<Object>> ROW_EXTRACTOR = (user, index) -> Arrays.asList(
            index,
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getName(),
            user.getStatus(),
            user.getCreatedAt(),
            user.getUpdatedAt());

    private List<UserDTO> body;
    @Setter
    @Getter
    @Builder.Default
    private boolean skipHeader = false;

    @Override
    public List<String> getHeader() {
        if (skipHeader) return null;
        return HEADER;
    }

    @Override
    public List<UserDTO> getBody() {
        return body;
    }

    @Override
    public BiFunction<UserDTO, Integer, List<Object>> getRowExtractor() {
        return ROW_EXTRACTOR;
    }

}
