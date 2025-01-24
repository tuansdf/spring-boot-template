package com.example.springboot.modules.permission;

import com.example.springboot.constants.CommonRegex;
import com.example.springboot.constants.CommonStatus;
import com.example.springboot.constants.Constants;
import com.example.springboot.exception.CustomException;
import com.example.springboot.modules.permission.dtos.PermissionDTO;
import com.example.springboot.utils.I18nHelper;
import com.example.springboot.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PermissionValidator {

    private static final List<String> validStatus = List.of(CommonStatus.ACTIVE, CommonStatus.INACTIVE);
    private final I18nHelper i18nHelper;

    public void validateCreate(PermissionDTO requestDTO) {
        ValidationUtils.notEmpty(requestDTO.getCode(), i18nHelper.getMessageX("form.error.missing", "field.code"));
        ValidationUtils.startsWith(requestDTO.getCode(), Constants.PERMISSION_STARTS_WITH, i18nHelper.getMessageX("form.error.not_start_with", "field.code", Constants.PERMISSION_STARTS_WITH));
        ValidationUtils.maxLength(requestDTO.getCode(), 255, i18nHelper.getMessageX("form.error.over_max_length", "field.code", 255));
        ValidationUtils.isPattern(requestDTO.getCode(), CommonRegex.CODE, i18nHelper.getMessageX("form.error.invalid", "field.code"));
        ValidationUtils.maxLength(requestDTO.getName(), 255, i18nHelper.getMessageX("form.error.over_max_length", "field.name", 255));
        ValidationUtils.isIn(requestDTO.getStatus(), validStatus, i18nHelper.getMessageX("form.error.invalid", "field.status"));
    }

    public void validateUpdate(PermissionDTO requestDTO) {
        ValidationUtils.maxLength(requestDTO.getName(), 255, i18nHelper.getMessageX("form.error.over_max_length", "field.name", 255));
        ValidationUtils.isIn(requestDTO.getStatus(), validStatus, i18nHelper.getMessageX("form.error.invalid", "field.status"));
    }

}
