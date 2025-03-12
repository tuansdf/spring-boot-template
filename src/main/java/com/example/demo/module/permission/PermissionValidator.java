package com.example.demo.module.permission;

import com.example.demo.common.constant.CommonRegex;
import com.example.demo.common.constant.CommonStatus;
import com.example.demo.common.constant.Constants;
import com.example.demo.module.permission.dto.PermissionDTO;
import com.example.demo.common.util.I18nHelper;
import com.example.demo.common.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PermissionValidator {

    private static final List<Integer> validStatus = List.of(CommonStatus.ACTIVE, CommonStatus.INACTIVE);

    public void validateCreate(PermissionDTO requestDTO) {
        ValidationUtils.notEmpty(requestDTO.getCode(), I18nHelper.getMessageX("form.error.missing", "field.code"));
        ValidationUtils.startsWith(requestDTO.getCode(), Constants.PERMISSION_STARTS_WITH, I18nHelper.getMessageX("form.error.not_start_with", "field.code", Constants.PERMISSION_STARTS_WITH));
        ValidationUtils.maxLength(requestDTO.getCode(), 255, I18nHelper.getMessageX("form.error.over_max_length", "field.code", 255));
        ValidationUtils.isPattern(requestDTO.getCode(), CommonRegex.CODE, I18nHelper.getMessageX("form.error.invalid", "field.code"));
        ValidationUtils.maxLength(requestDTO.getName(), 255, I18nHelper.getMessageX("form.error.over_max_length", "field.name", 255));
        ValidationUtils.isIn(requestDTO.getStatus(), validStatus, I18nHelper.getMessageX("form.error.invalid", "field.status"));
    }

    public void validateUpdate(PermissionDTO requestDTO) {
        ValidationUtils.maxLength(requestDTO.getName(), 255, I18nHelper.getMessageX("form.error.over_max_length", "field.name", 255));
        ValidationUtils.isIn(requestDTO.getStatus(), validStatus, I18nHelper.getMessageX("form.error.invalid", "field.status"));
    }

}
