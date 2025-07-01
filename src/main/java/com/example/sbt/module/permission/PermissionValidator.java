package com.example.sbt.module.permission;

import com.example.sbt.core.exception.CustomException;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.core.util.LocaleHelper;
import com.example.sbt.core.util.LocaleHelper.LocaleKey;
import com.example.sbt.core.util.ValidationUtils;
import com.example.sbt.core.constant.Constants;
import com.example.sbt.module.permission.dto.PermissionDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PermissionValidator {

    private final PermissionRepository permissionRepository;

    public void cleanRequest(PermissionDTO requestDTO) {
        if (requestDTO == null) return;
        requestDTO.setCode(ConversionUtils.safeTrim(requestDTO.getCode()).toUpperCase());
        requestDTO.setName(ConversionUtils.safeTrim(requestDTO.getName()));
    }

    public void validateUpdate(PermissionDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", new LocaleKey("field.request")));
        }
        if (StringUtils.isNotEmpty(requestDTO.getName()) && requestDTO.getName().length() > 255) {
            throw new CustomException(LocaleHelper.getMessage("form.error.over_max_length", new LocaleKey("field.name"), 255));
        }
    }

    public void validateCreate(PermissionDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", new LocaleKey("field.request")));
        }
        if (StringUtils.isBlank(requestDTO.getCode())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.required", new LocaleKey("field.code")));
        }
        if (!requestDTO.getCode().startsWith(Constants.PERMISSION_STARTS_WITH)) {
            throw new CustomException(LocaleHelper.getMessage("form.error.not_start_with", new LocaleKey("field.code"), Constants.PERMISSION_STARTS_WITH));
        }
        String codeError = ValidationUtils.validateCode(requestDTO.getCode());
        if (codeError != null) {
            throw new CustomException(codeError);
        }
        if (permissionRepository.existsByCode(requestDTO.getCode())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.duplicated", new LocaleKey("field.code")));
        }
    }

}
