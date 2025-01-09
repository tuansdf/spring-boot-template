package org.tuanna.xcloneserver.modules.user.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.tuanna.xcloneserver.constants.Status;
import org.tuanna.xcloneserver.exception.CustomException;
import org.tuanna.xcloneserver.utils.ValidationUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserDTO {

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

    // SEARCH_USER_CONTACT
    public UserDTO(UUID id, String username) {
        this.id = id;
        this.username = username;
    }

    // SEARCH_USER
    public UserDTO(
            UUID id, String username, String email, String name, String status, UUID createdBy, UUID updatedBy,
            OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.status = status;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
