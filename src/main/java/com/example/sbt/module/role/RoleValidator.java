package com.example.sbt.module.role;

import com.example.sbt.common.constant.Constants;
import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.LocaleHelper;
import com.example.sbt.common.util.LocaleHelper.LocaleKey;
import com.example.sbt.common.util.ValidationUtils;
import com.example.sbt.module.permission.PermissionRepository;
import com.example.sbt.module.role.dto.RoleDTO;
import com.example.sbt.module.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RoleValidator {

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
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", new LocaleKey("field.request")));
        }
        if (StringUtils.isNotEmpty(requestDTO.getName()) && requestDTO.getName().length() > 255) {
            throw new CustomException(LocaleHelper.getMessage("form.error.over_max_length", new LocaleKey("field.name"), 255));
        }
        if (StringUtils.isNotEmpty(requestDTO.getDescription()) && requestDTO.getDescription().length() > 255) {
            throw new CustomException(LocaleHelper.getMessage("form.error.over_max_length", new LocaleKey("field.description"), 255));
        }
        if (CollectionUtils.isEmpty(requestDTO.getPermissionIds())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.required", new LocaleKey("field.permission")));
        }
        if (permissionRepository.countByIdIn(requestDTO.getPermissionIds()) != requestDTO.getPermissionIds().size()) {
            throw new CustomException(LocaleHelper.getMessage("form.error.invalid", new LocaleKey("field.permission")));
        }
    }

    public void validateCreate(RoleDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", new LocaleKey("field.request")));
        }
        if (StringUtils.isBlank(requestDTO.getCode())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.required", new LocaleKey("field.code")));
        }
        if (!requestDTO.getCode().startsWith(Constants.ROLE_STARTS_WITH)) {
            throw new CustomException(LocaleHelper.getMessage("form.error.not_start_with", new LocaleKey("field.code"), Constants.ROLE_STARTS_WITH));
        }
        String codeError = ValidationUtils.validateCode(requestDTO.getCode());
        if (codeError != null) {
            throw new CustomException(codeError);
        }
        if (roleRepository.existsByCode(requestDTO.getCode())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.duplicated", new LocaleKey("field.code")));
        }
    }

}
