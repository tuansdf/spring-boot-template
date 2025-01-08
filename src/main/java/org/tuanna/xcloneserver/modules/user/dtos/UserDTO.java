package org.tuanna.xcloneserver.modules.user.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Tuple;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
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
public class UserDTO implements Serializable {

    private UUID id;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private String name;
    private String status;
    private UUID createdBy;
    private UUID updatedBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static UserDTO fromTuple(Tuple tuple) {
        UserDTO result = new UserDTO();
        result.setId(CommonUtils.getValue(tuple, "id", UUID.class));
        result.setUsername(CommonUtils.getValue(tuple, "username", String.class));
        result.setEmail(CommonUtils.getValue(tuple, "email", String.class));
        result.setName(CommonUtils.getValue(tuple, "name", String.class));
        result.setStatus(CommonUtils.getValue(tuple, "status", String.class));
        result.setCreatedBy(CommonUtils.getValue(tuple, "created_by", UUID.class));
        result.setUpdatedBy(CommonUtils.getValue(tuple, "updated_by", UUID.class));
        result.setCreatedAt(DateUtils.toOffsetDateTime(CommonUtils.getValue(tuple, "created_at", Instant.class)));
        result.setUpdatedAt(DateUtils.toOffsetDateTime(CommonUtils.getValue(tuple, "updated_at", Instant.class)));
        return result;
    }

    public static List<UserDTO> fromTuples(List<Tuple> tuples) {
        if (CollectionUtils.isEmpty(tuples)) return new ArrayList<>();
        return tuples.stream().map(UserDTO::fromTuple).toList();
    }

    public void validate() throws CustomException {
        ValidationUtils.notEmpty(this.username, "Username is required");
        ValidationUtils.maxLength(this.username, 255, "Username exceeds the maximum length of 255 characters");
        ValidationUtils.notEmpty(this.email, "Email is required");
        ValidationUtils.isEmail(this.email, "Email is invalid");
        ValidationUtils.maxLength(this.email, 255, "Email exceeds the maximum length of 255 characters");
        ValidationUtils.notEmpty(this.password, "Password is required");
        ValidationUtils.maxLength(this.password, 255, "Password exceeds the maximum length of 255 characters");
        ValidationUtils.isIn(this.status, List.of(Status.ACTIVE, Status.INACTIVE, Status.PENDING), "Status is invalid");
    }

}
