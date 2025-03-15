package com.example.demo.module.configuration;

import com.example.demo.common.constant.CommonStatus;
import com.example.demo.common.exception.CustomException;
import com.example.demo.common.util.I18nHelper;
import com.example.demo.common.util.ValidationUtils;
import com.example.demo.module.configuration.dto.ConfigurationDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ConfigurationValidator {

    private final static List<Integer> validStatus = List.of(CommonStatus.ACTIVE, CommonStatus.INACTIVE);

    public void validateCreate(ConfigurationDTO requestDTO) {
        if (StringUtils.isBlank(requestDTO.getCode())) {
            throw new CustomException(I18nHelper.getMessageX("form.error.missing", "field.code"), HttpStatus.BAD_REQUEST);
        }
        String codeError = ValidationUtils.validateCode(requestDTO.getCode());
        if (codeError != null) {
            throw new CustomException(codeError);
        }
        if (StringUtils.isNotEmpty(requestDTO.getValue()) && requestDTO.getValue().length() > 255) {
            throw new CustomException(I18nHelper.getMessageX("form.error.over_max_length", "field.value", 255));
        }
        if (StringUtils.isNotEmpty(requestDTO.getDescription()) && requestDTO.getDescription().length() > 255) {
            throw new CustomException(I18nHelper.getMessageX("form.error.over_max_length", "field.description", 255));
        }
        if (requestDTO.getStatus() != null && !validStatus.contains(requestDTO.getStatus())) {
            throw new CustomException(I18nHelper.getMessageX("form.error.invalid", "field.status"));
        }
    }

    public void validateUpdate(ConfigurationDTO requestDTO) {
        if (StringUtils.isNotEmpty(requestDTO.getValue()) && requestDTO.getValue().length() > 255) {
            throw new CustomException(I18nHelper.getMessageX("form.error.over_max_length", "field.value"));
        }
        if (StringUtils.isNotEmpty(requestDTO.getDescription()) && requestDTO.getDescription().length() > 255) {
            throw new CustomException(I18nHelper.getMessageX("form.error.over_max_length", "field.description"));
        }
        if (requestDTO.getStatus() != null && !validStatus.contains(requestDTO.getStatus())) {
            throw new CustomException(I18nHelper.getMessageX("form.error.invalid", "field.status"));
        }
    }

}
