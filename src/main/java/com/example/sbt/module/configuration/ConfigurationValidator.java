package com.example.sbt.module.configuration;

import com.example.sbt.common.constant.CommonStatus;
import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.util.LocaleHelper;
import com.example.sbt.common.util.LocaleHelper.LocaleKey;
import com.example.sbt.common.util.ValidationUtils;
import com.example.sbt.module.configuration.dto.ConfigurationDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ConfigurationValidator {

    private static final List<String> validStatus = List.of(CommonStatus.ACTIVE, CommonStatus.INACTIVE);

    public void validateUpdate(ConfigurationDTO requestDTO) {
        if (StringUtils.isNotEmpty(requestDTO.getValue()) && requestDTO.getValue().length() > 255) {
            throw new CustomException(LocaleHelper.getMessage("form.error.over_max_length", new LocaleKey("field.value")));
        }
        if (StringUtils.isNotEmpty(requestDTO.getDescription()) && requestDTO.getDescription().length() > 255) {
            throw new CustomException(LocaleHelper.getMessage("form.error.over_max_length", new LocaleKey("field.description")));
        }
        if (StringUtils.isEmpty(requestDTO.getStatus())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", new LocaleKey("field.status")));
        }
        if (!validStatus.contains(requestDTO.getStatus())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.invalid", new LocaleKey("field.status")));
        }
    }


    public void validateCreate(ConfigurationDTO requestDTO) {
        if (StringUtils.isBlank(requestDTO.getCode())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", new LocaleKey("field.code")));
        }
        String codeError = ValidationUtils.validateCode(requestDTO.getCode());
        if (codeError != null) {
            throw new CustomException(codeError);
        }
        validateUpdate(requestDTO);
    }

}
