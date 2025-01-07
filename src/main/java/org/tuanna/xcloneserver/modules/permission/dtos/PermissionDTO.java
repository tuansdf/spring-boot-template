package org.tuanna.xcloneserver.modules.permission.dtos;

import jakarta.persistence.Tuple;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.tuanna.xcloneserver.utils.DateUtils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PermissionDTO {

    private Long id;
    private String code;
    private String name;
    private String status;
    private UUID updatedBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static PermissionDTO fromTuple(Tuple tuple) {
        PermissionDTO result = new PermissionDTO();
        result.id = tuple.get("id", Long.class);
        result.code = tuple.get("code", String.class);
        result.name = tuple.get("name", String.class);
        result.status = tuple.get("status", String.class);
        result.updatedBy = tuple.get("updated_by", UUID.class);
        result.createdAt = DateUtils.toOffsetDateTime(tuple.get("created_at", Instant.class));
        result.updatedAt = DateUtils.toOffsetDateTime(tuple.get("updated_at", Instant.class));
        return result;
    }

    public static List<PermissionDTO> fromTuples(List<Tuple> tuples) {
        if (CollectionUtils.isEmpty(tuples)) return new ArrayList<>();
        return tuples.stream().map(PermissionDTO::fromTuple).toList();
    }

}
