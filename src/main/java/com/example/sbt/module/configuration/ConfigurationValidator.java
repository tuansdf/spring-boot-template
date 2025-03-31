package com.example.sbt.module.configuration;

import com.example.sbt.common.constant.CommonStatus;
import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.util.LocaleHelper;
import com.example.sbt.common.util.ValidationUtils;
import com.example.sbt.module.configuration.dto.ConfigurationDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ConfigurationValidator {

    private final static List<Integer> validStatus = List.of(CommonStatus.ACTIVE, CommonStatus.INACTIVE);

    public void validateCreate(ConfigurationDTO requestDTO) {
        if (StringUtils.isBlank(requestDTO.getCode())) {
            throw new CustomException(LocaleHelper.getMessageX("form.error.missing", "##field.code"));
        }
        String codeError = ValidationUtils.validateCode(requestDTO.getCode());
        if (codeError != null) {
            throw new CustomException(codeError);
        }
        if (StringUtils.isNotEmpty(requestDTO.getValue()) && requestDTO.getValue().length() > 255) {
            throw new CustomException(LocaleHelper.getMessageX("form.error.over_max_length", "##field.value", 255));
        }
        if (StringUtils.isNotEmpty(requestDTO.getDescription()) && requestDTO.getDescription().length() > 255) {
            throw new CustomException(LocaleHelper.getMessageX("form.error.over_max_length", "##field.description", 255));
        }
        if (requestDTO.getStatus() != null && !validStatus.contains(requestDTO.getStatus())) {
            throw new CustomException(LocaleHelper.getMessageX("form.error.invalid", "##field.status"));
        }
    }

    public void validateUpdate(ConfigurationDTO requestDTO) {
        if (StringUtils.isNotEmpty(requestDTO.getValue()) && requestDTO.getValue().length() > 255) {
            throw new CustomException(LocaleHelper.getMessageX("form.error.over_max_length", "##field.value"));
        }
        if (StringUtils.isNotEmpty(requestDTO.getDescription()) && requestDTO.getDescription().length() > 255) {
            throw new CustomException(LocaleHelper.getMessageX("form.error.over_max_length", "##field.description"));
        }
        if (requestDTO.getStatus() != null && !validStatus.contains(requestDTO.getStatus())) {
            throw new CustomException(LocaleHelper.getMessageX("form.error.invalid", "##field.status"));
        }
    }

}
