package com.example.sbt.module.role.service;

import com.example.sbt.core.constant.Constants;
import com.example.sbt.core.dto.LocaleKey;
import com.example.sbt.core.exception.CustomException;
import com.example.sbt.core.helper.LocaleHelper;
import com.example.sbt.core.helper.ValidationHelper;
import com.example.sbt.module.permission.repository.PermissionRepository;
import com.example.sbt.module.role.dto.RoleDTO;
import com.example.sbt.module.role.repository.RoleRepository;
import com.example.sbt.shared.util.ConversionUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RoleValidator {
    private final LocaleHelper localeHelper;
    private final ValidationHelper validationHelper;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public void cleanRequest(RoleDTO requestDTO) {
        if (requestDTO == null) return;
        requestDTO.setCode(ConversionUtils.safeTrim(requestDTO.getCode()).toUpperCase());
        requestDTO.setName(ConversionUtils.safeTrim(requestDTO.getName()));
        requestDTO.setDescription(ConversionUtils.safeTrim(requestDTO.getDescription()));
    }

    public void validateUpdate(RoleDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("form.error.missing", new LocaleKey("field.request")));
        }
        if (StringUtils.isNotEmpty(requestDTO.getName()) && requestDTO.getName().length() > 255) {
            throw new CustomException(localeHelper.getMessage("form.error.over_max_length", new LocaleKey("field.name"), 255));
        }
        if (StringUtils.isNotEmpty(requestDTO.getDescription()) && requestDTO.getDescription().length() > 255) {
            throw new CustomException(localeHelper.getMessage("form.error.over_max_length", new LocaleKey("field.description"), 255));
        }
        if (CollectionUtils.isEmpty(requestDTO.getPermissionIds())) {
            throw new CustomException(localeHelper.getMessage("form.error.required", new LocaleKey("field.permission")));
        }
        if (permissionRepository.countByIdIn(requestDTO.getPermissionIds()) != requestDTO.getPermissionIds().size()) {
            throw new CustomException(localeHelper.getMessage("form.error.invalid", new LocaleKey("field.permission")));
        }
    }

    public void validateCreate(RoleDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("form.error.missing", new LocaleKey("field.request")));
        }
        if (StringUtils.isBlank(requestDTO.getCode())) {
            throw new CustomException(localeHelper.getMessage("form.error.required", new LocaleKey("field.code")));
        }
        if (!requestDTO.getCode().startsWith(Constants.ROLE_STARTS_WITH)) {
            throw new CustomException(localeHelper.getMessage("form.error.not_start_with", new LocaleKey("field.code"), Constants.ROLE_STARTS_WITH));
        }
        String codeError = validationHelper.validateCode(requestDTO.getCode());
        if (codeError != null) {
            throw new CustomException(codeError);
        }
        if (roleRepository.existsByCode(requestDTO.getCode())) {
            throw new CustomException(localeHelper.getMessage("form.error.duplicated", new LocaleKey("field.code")));
        }
    }
}
