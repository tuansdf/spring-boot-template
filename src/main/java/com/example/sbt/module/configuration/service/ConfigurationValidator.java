package com.example.sbt.module.configuration.service;

import com.example.sbt.core.constant.CommonStatus;
import com.example.sbt.core.dto.LocaleKey;
import com.example.sbt.core.exception.CustomException;
import com.example.sbt.core.helper.LocaleHelper;
import com.example.sbt.core.helper.ValidationHelper;
import com.example.sbt.module.configuration.dto.ConfigurationDTO;
import com.example.sbt.module.configuration.repository.ConfigurationRepository;
import com.example.sbt.shared.util.ConversionUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ConfigurationValidator {
    private static final List<String> VALID_STATUS = List.of(CommonStatus.ACTIVE, CommonStatus.INACTIVE);

    private final LocaleHelper localeHelper;
    private final ValidationHelper validationHelper;
    private final ConfigurationRepository configurationRepository;

    public void cleanRequest(ConfigurationDTO requestDTO) {
        if (requestDTO == null) return;
        requestDTO.setCode(ConversionUtils.safeTrim(requestDTO.getCode()).toUpperCase());
        requestDTO.setDescription(ConversionUtils.safeTrim(requestDTO.getDescription()));
        requestDTO.setValue(ConversionUtils.safeToString(requestDTO.getCode()));
        if (!VALID_STATUS.contains(requestDTO.getStatus())) {
            requestDTO.setStatus(CommonStatus.INACTIVE);
        }
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
        if (StringUtils.isBlank(requestDTO.getStatus())) {
            throw new CustomException(localeHelper.getMessage("validation.error.required", new LocaleKey("field.status")));
        }
        if (!VALID_STATUS.contains(requestDTO.getStatus())) {
            throw new CustomException(localeHelper.getMessage("validation.error.invalid", new LocaleKey("field.status")));
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
