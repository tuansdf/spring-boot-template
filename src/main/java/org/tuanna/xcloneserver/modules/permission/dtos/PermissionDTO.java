package org.tuanna.xcloneserver.modules.permission.dtos;

import jakarta.persistence.Tuple;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.tuanna.xcloneserver.utils.CommonUtils;
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
        result.setId(CommonUtils.getValue(tuple, "id", Long.class));
        result.setCode(CommonUtils.getValue(tuple, "code", String.class));
        result.setName(CommonUtils.getValue(tuple, "name", String.class));
        result.setStatus(CommonUtils.getValue(tuple, "status", String.class));
        result.setUpdatedBy(CommonUtils.getValue(tuple, "updated_by", UUID.class));
        result.setCreatedAt(DateUtils.toOffsetDateTime(CommonUtils.getValue(tuple, "created_at", Instant.class)));
        result.setUpdatedAt(DateUtils.toOffsetDateTime(CommonUtils.getValue(tuple, "updated_at", Instant.class)));
        return result;
    }

    public static List<PermissionDTO> fromTuples(List<Tuple> tuples) {
        if (CollectionUtils.isEmpty(tuples)) return new ArrayList<>();
        return tuples.stream().map(PermissionDTO::fromTuple).toList();
    }

}
