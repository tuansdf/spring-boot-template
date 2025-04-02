package com.example.sbt.module.permission;

import com.example.sbt.common.constant.Constants;
import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.util.LocaleHelper;
import com.example.sbt.common.util.ValidationUtils;
import com.example.sbt.module.permission.dto.PermissionDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PermissionValidator {

    public void validateCreate(PermissionDTO requestDTO) {
        if (StringUtils.isEmpty(requestDTO.getCode())) {
            throw new CustomException(LocaleHelper.getMessageX("form.error.missing", "##field.code"));
        }
        if (!requestDTO.getCode().startsWith(Constants.PERMISSION_STARTS_WITH)) {
            throw new CustomException(LocaleHelper.getMessageX("form.error.not_start_with", "##field.code", Constants.PERMISSION_STARTS_WITH));
        }
        String codeError = ValidationUtils.validateCode(requestDTO.getCode());
        if (codeError != null) {
            throw new CustomException(codeError);
        }
        if (StringUtils.isNotEmpty(requestDTO.getName()) && requestDTO.getName().length() > 255) {
            throw new CustomException(LocaleHelper.getMessageX("form.error.over_max_length", "##field.name", 255));
        }
    }

    public void validateUpdate(PermissionDTO requestDTO) {
        if (StringUtils.isNotEmpty(requestDTO.getName()) && requestDTO.getName().length() > 255) {
            throw new CustomException(LocaleHelper.getMessageX("form.error.over_max_length", "##field.name", 255));
        }
    }

}
