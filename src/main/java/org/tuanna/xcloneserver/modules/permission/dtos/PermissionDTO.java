package org.tuanna.xcloneserver.modules.permission.dtos;

import jakarta.persistence.Tuple;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.tuanna.xcloneserver.constants.Constants;
import org.tuanna.xcloneserver.constants.Status;
import org.tuanna.xcloneserver.exception.CustomException;
import org.tuanna.xcloneserver.utils.CommonUtils;
import org.tuanna.xcloneserver.utils.DateUtils;
import org.tuanna.xcloneserver.utils.ValidationUtils;

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
public class PermissionDTO implements Serializable {

    private Long id;
    private String code;
    private String name;
    private String status;
    private UUID createdBy;
    private UUID updatedBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static PermissionDTO fromTuple(Tuple tuple) {
        PermissionDTO result = new PermissionDTO();
        result.setId(CommonUtils.getValue(tuple, "id", Long.class));
        result.setCode(CommonUtils.getValue(tuple, "code", String.class));
        result.setName(CommonUtils.getValue(tuple, "name", String.class));
        result.setStatus(CommonUtils.getValue(tuple, "status", String.class));
        result.setCreatedBy(CommonUtils.getValue(tuple, "created_by", UUID.class));
        result.setUpdatedBy(CommonUtils.getValue(tuple, "updated_by", UUID.class));
        result.setCreatedAt(DateUtils.toOffsetDateTime(CommonUtils.getValue(tuple, "created_at", Instant.class)));
        result.setUpdatedAt(DateUtils.toOffsetDateTime(CommonUtils.getValue(tuple, "updated_at", Instant.class)));
        return result;
    }

    public static List<PermissionDTO> fromTuples(List<Tuple> tuples) {
        if (CollectionUtils.isEmpty(tuples)) return new ArrayList<>();
        return tuples.stream().map(PermissionDTO::fromTuple).toList();
    }

    public void validateCreate() throws CustomException {
        ValidationUtils.notEmpty(this.code, "Code is required");
        ValidationUtils.startsWith(this.code, Constants.PERMISSION_STARTS_WITH, "Code must start with " + Constants.PERMISSION_STARTS_WITH);
        ValidationUtils.maxLength(this.code, 255, "Code exceeds the maximum length of 255 characters");
        ValidationUtils.maxLength(this.name, 255, "Name exceeds the maximum length of 255 characters");
        ValidationUtils.isIn(this.status, List.of(Status.ACTIVE, Status.INACTIVE), "Status is invalid");
    }

    public void validateUpdate() throws CustomException {
        ValidationUtils.maxLength(this.name, 255, "Name exceeds the maximum length of 255 characters");
        ValidationUtils.isIn(this.status, List.of(Status.ACTIVE, Status.INACTIVE), "Status is invalid");
    }

}
