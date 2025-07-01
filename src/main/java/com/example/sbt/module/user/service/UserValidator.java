package com.example.sbt.module.user.service;

import com.example.sbt.core.constant.CommonStatus;
import com.example.sbt.core.dto.LocaleKey;
import com.example.sbt.core.exception.CustomException;
import com.example.sbt.core.helper.LocaleHelper;
import com.example.sbt.core.helper.ValidationHelper;
import com.example.sbt.module.user.dto.UserDTO;
import com.example.sbt.shared.util.ConversionUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class UserValidator {

    private static final List<String> validStatus = List.of(CommonStatus.ACTIVE, CommonStatus.INACTIVE);

    private final LocaleHelper localeHelper;
    private final ValidationHelper validationHelper;

    public void validateUpdate(UserDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("form.error.missing", new LocaleKey("field.request")));
        }
        requestDTO.setUsername(ConversionUtils.safeTrim(requestDTO.getUsername()));
        requestDTO.setEmail(ConversionUtils.safeTrim(requestDTO.getEmail()));
        requestDTO.setName(ConversionUtils.safeTrim(requestDTO.getName()));
        String usernameError = validationHelper.validateUsername(requestDTO.getUsername());
        if (usernameError != null) {
            throw new CustomException(usernameError);
        }
        String emailError = validationHelper.validateEmail(requestDTO.getEmail());
        if (emailError != null) {
            throw new CustomException(emailError);
        }
        if (StringUtils.isNotEmpty(requestDTO.getName()) && requestDTO.getName().length() > 255) {
            throw new CustomException(localeHelper.getMessage("form.error.over_max_length", new LocaleKey("field.name"), 255));
        }
        if (StringUtils.isNotEmpty(requestDTO.getStatus()) && !validStatus.contains(requestDTO.getStatus())) {
            throw new CustomException(localeHelper.getMessage("form.error.invalid", new LocaleKey("field.status")));
        }
    }

}
