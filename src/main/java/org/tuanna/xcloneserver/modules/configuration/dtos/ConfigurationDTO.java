package org.tuanna.xcloneserver.modules.configuration.dtos;

import lombok.*;
import org.tuanna.xcloneserver.constants.CommonStatus;
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
public class ConfigurationDTO {

    private Long id;
    private String code;
    private String value;
    private String description;
    private String status;
    private UUID createdBy;
    private UUID updatedBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public void validateCreate() throws CustomException {
        ValidationUtils.notEmpty(this.code, "Code is required");
        ValidationUtils.maxLength(this.code, 255, "Code exceeds the maximum length of 255 characters");
        ValidationUtils.maxLength(this.value, 255, "Value exceeds the maximum length of 255 characters");
        ValidationUtils.maxLength(this.description, 255, "Description exceeds the maximum length of 255 characters");
        ValidationUtils.isIn(this.status, List.of(CommonStatus.ACTIVE, CommonStatus.INACTIVE), "Status is invalid");
    }

    public void validateUpdate() throws CustomException {
        ValidationUtils.maxLength(this.value, 255, "Name exceeds the maximum length of 255 characters");
        ValidationUtils.maxLength(this.description, 255, "Description exceeds the maximum length of 255 characters");
        ValidationUtils.isIn(this.status, List.of(CommonStatus.ACTIVE, CommonStatus.INACTIVE), "Status is invalid");
    }

}
