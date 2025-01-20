package com.example.springboot.modules.permission.dtos;

import com.example.springboot.constants.CommonRegex;
import com.example.springboot.constants.CommonStatus;
import com.example.springboot.constants.Constants;
import com.example.springboot.exception.CustomException;
import com.example.springboot.utils.ValidationUtils;
import lombok.*;

import java.time.OffsetDateTime;
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
    private UUID createdBy;
    private UUID updatedBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public void validateCreate() throws CustomException {
        ValidationUtils.notEmpty(this.code, "Code is required");
        ValidationUtils.startsWith(this.code, Constants.PERMISSION_STARTS_WITH, "Code must start with " + Constants.PERMISSION_STARTS_WITH);
        ValidationUtils.maxLength(this.code, 255, "Code exceeds the maximum length of 255 characters");
        ValidationUtils.isPattern(this.code, CommonRegex.CODE, "Code is invalid");
        ValidationUtils.maxLength(this.name, 255, "Name exceeds the maximum length of 255 characters");
        ValidationUtils.isIn(this.status, List.of(CommonStatus.ACTIVE, CommonStatus.INACTIVE), "Status is invalid");
    }

    public void validateUpdate() throws CustomException {
        ValidationUtils.maxLength(this.name, 255, "Name exceeds the maximum length of 255 characters");
        ValidationUtils.isIn(this.status, List.of(CommonStatus.ACTIVE, CommonStatus.INACTIVE), "Status is invalid");
    }

}
