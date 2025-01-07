package org.tuanna.xcloneserver.modules.user.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

}
