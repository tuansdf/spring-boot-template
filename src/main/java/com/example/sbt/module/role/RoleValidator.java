package com.example.sbt.module.role;

import com.example.sbt.common.constant.CommonStatus;
import com.example.sbt.common.constant.Constants;
import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.util.LocaleHelper;
import com.example.sbt.common.util.ValidationUtils;
import com.example.sbt.module.role.dto.RoleDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class RoleValidator {

    private static final List<Integer> validStatus = List.of(CommonStatus.ACTIVE, CommonStatus.INACTIVE);

    public void validateCreate(RoleDTO requestDTO) {
        if (StringUtils.isEmpty(requestDTO.getCode())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", "#field.code"));
        }
        if (!requestDTO.getCode().startsWith(Constants.ROLE_STARTS_WITH)) {
            throw new CustomException(LocaleHelper.getMessage("form.error.not_start_with", "#field.code", Constants.ROLE_STARTS_WITH));
        }
        String codeError = ValidationUtils.validateCode(requestDTO.getCode());
        if (codeError != null) {
            throw new CustomException(codeError);
        }
        if (StringUtils.isNotEmpty(requestDTO.getName()) && requestDTO.getName().length() > 255) {
            throw new CustomException(LocaleHelper.getMessage("form.error.over_max_length", "#field.name", 255));
        }
        if (StringUtils.isNotEmpty(requestDTO.getDescription()) && requestDTO.getDescription().length() > 255) {
            throw new CustomException(LocaleHelper.getMessage("form.error.over_max_length", "#field.description", 255));
        }
        if (requestDTO.getStatus() != null && !validStatus.contains(requestDTO.getStatus())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.invalid", "#field.status"));
        }
    }

    public void validateUpdate(RoleDTO requestDTO) {
        if (StringUtils.isNotEmpty(requestDTO.getName()) && requestDTO.getName().length() > 255) {
            throw new CustomException(LocaleHelper.getMessage("form.error.over_max_length", "#field.name", 255));
        }
        if (StringUtils.isNotEmpty(requestDTO.getDescription()) && requestDTO.getDescription().length() > 255) {
            throw new CustomException(LocaleHelper.getMessage("form.error.over_max_length", "#field.description", 255));
        }
        if (requestDTO.getStatus() != null && !validStatus.contains(requestDTO.getStatus())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.invalid", "#field.status"));
        }
    }

}
