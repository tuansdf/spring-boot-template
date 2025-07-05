package com.example.sbt.module.permission.service;

import com.example.sbt.core.constant.Constants;
import com.example.sbt.core.dto.LocaleKey;
import com.example.sbt.core.exception.CustomException;
import com.example.sbt.core.helper.LocaleHelper;
import com.example.sbt.core.helper.ValidationHelper;
import com.example.sbt.module.permission.dto.PermissionDTO;
import com.example.sbt.module.permission.repository.PermissionRepository;
import com.example.sbt.shared.util.ConversionUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PermissionValidator {
    private final LocaleHelper localeHelper;
    private final ValidationHelper validationHelper;
    private final PermissionRepository permissionRepository;

    public void cleanRequest(PermissionDTO requestDTO) {
        if (requestDTO == null) return;
        requestDTO.setCode(ConversionUtils.safeTrim(requestDTO.getCode()).toUpperCase());
        requestDTO.setName(ConversionUtils.safeTrim(requestDTO.getName()));
    }

    public void validateUpdate(PermissionDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("validation.error.missing", new LocaleKey("field.request")));
        }
        if (StringUtils.isNotEmpty(requestDTO.getName()) && requestDTO.getName().length() > 255) {
            throw new CustomException(localeHelper.getMessage("validation.error.over_max_length", new LocaleKey("field.name"), 255));
        }
    }

    public void validateCreate(PermissionDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("validation.error.missing", new LocaleKey("field.request")));
        }
        if (StringUtils.isBlank(requestDTO.getCode())) {
            throw new CustomException(localeHelper.getMessage("validation.error.required", new LocaleKey("field.code")));
        }
        if (!requestDTO.getCode().startsWith(Constants.PERMISSION_STARTS_WITH)) {
            throw new CustomException(localeHelper.getMessage("validation.error.not_start_with", new LocaleKey("field.code"), Constants.PERMISSION_STARTS_WITH));
        }
        String codeError = validationHelper.validateCode(requestDTO.getCode());
        if (codeError != null) {
            throw new CustomException(codeError);
        }
        if (permissionRepository.existsByCode(requestDTO.getCode())) {
            throw new CustomException(localeHelper.getMessage("validation.error.duplicated", new LocaleKey("field.code")));
        }
    }
}
