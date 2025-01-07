package org.tuanna.xcloneserver.modules.role.dtos;

import jakarta.persistence.Tuple;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.tuanna.xcloneserver.utils.CommonUtils;
import org.tuanna.xcloneserver.utils.DateUtils;

import java.io.Serializable;
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
public class RoleDTO implements Serializable {

    private Long id;
    private String code;
    private String name;
    private String description;
    private String status;
    private UUID createdBy;
    private UUID updatedBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static RoleDTO fromTuple(Tuple tuple) {
        RoleDTO result = new RoleDTO();
        result.setId(CommonUtils.getValue(tuple, "id", Long.class));
        result.setCode(CommonUtils.getValue(tuple, "code", String.class));
        result.setName(CommonUtils.getValue(tuple, "name", String.class));
        result.setDescription(CommonUtils.getValue(tuple, "description", String.class));
        result.setStatus(CommonUtils.getValue(tuple, "status", String.class));
        result.setCreatedBy(CommonUtils.getValue(tuple, "created_by", UUID.class));
        result.setUpdatedBy(CommonUtils.getValue(tuple, "updated_by", UUID.class));
        result.setCreatedAt(DateUtils.toOffsetDateTime(CommonUtils.getValue(tuple, "created_at", Instant.class)));
        result.setUpdatedAt(DateUtils.toOffsetDateTime(CommonUtils.getValue(tuple, "updated_at", Instant.class)));
        return result;
    }

    public static List<RoleDTO> fromTuples(List<Tuple> tuples) {
        if (CollectionUtils.isEmpty(tuples)) return new ArrayList<>();
        return tuples.stream().map(RoleDTO::fromTuple).toList();
    }

}
