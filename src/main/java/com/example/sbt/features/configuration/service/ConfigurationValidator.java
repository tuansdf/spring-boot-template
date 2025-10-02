package com.example.sbt.features.configuration.service;

import com.example.sbt.common.dto.LocaleKey;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.infrastructure.exception.CustomException;
import com.example.sbt.infrastructure.web.helper.LocaleHelper;
import com.example.sbt.infrastructure.web.helper.ValidationHelper;
import com.example.sbt.features.configuration.dto.ConfigurationDTO;
import com.example.sbt.features.configuration.repository.ConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ConfigurationValidator {
    private final LocaleHelper localeHelper;
    private final ValidationHelper validationHelper;
    private final ConfigurationRepository configurationRepository;

    public void cleanRequest(ConfigurationDTO requestDTO) {
        if (requestDTO == null) return;
        requestDTO.setCode(ConversionUtils.safeToString(requestDTO.getCode()).trim().toUpperCase());
        requestDTO.setDescription(ConversionUtils.safeTrim(requestDTO.getDescription()));
        requestDTO.setValue(ConversionUtils.safeToString(requestDTO.getValue()));
        requestDTO.setIsEnabled(ConversionUtils.safeToBoolean(requestDTO.getIsEnabled()));
        requestDTO.setIsPublic(ConversionUtils.safeToBoolean(requestDTO.getIsPublic()));
    }

    public void validateUpdate(ConfigurationDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("validation.error.missing", new LocaleKey("field.request")));
        }
        if (requestDTO.getValue() != null && requestDTO.getValue().length() > 255) {
            throw new CustomException(localeHelper.getMessage("validation.error.over_max_length", new LocaleKey("field.value")));
        }
        if (requestDTO.getDescription() != null && requestDTO.getDescription().length() > 255) {
            throw new CustomException(localeHelper.getMessage("validation.error.over_max_length", new LocaleKey("field.description")));
        }
    }

    public void validateCreate(ConfigurationDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("validation.error.missing", new LocaleKey("field.request")));
        }
        String codeError = validationHelper.validateCode(requestDTO.getCode());
        if (codeError != null) {
            throw new CustomException(codeError);
        }
        if (configurationRepository.existsByCode(requestDTO.getCode())) {
            throw new CustomException(localeHelper.getMessage("validation.error.duplicated", new LocaleKey("field.code")));
        }
    }
}
