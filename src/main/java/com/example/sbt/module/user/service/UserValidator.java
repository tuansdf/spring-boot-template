package com.example.sbt.module.user.service;

import com.example.sbt.common.dto.LocaleKey;
import com.example.sbt.infrastructure.exception.CustomException;
import com.example.sbt.infrastructure.helper.LocaleHelper;
import com.example.sbt.infrastructure.helper.ValidationHelper;
import com.example.sbt.module.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserValidator {
    private final LocaleHelper localeHelper;
    private final ValidationHelper validationHelper;

    public void validateUpdate(UserDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("validation.error.missing", new LocaleKey("field.request")));
        }
        if (requestDTO.getUsername() != null) {
            String usernameError = validationHelper.validateUsername(requestDTO.getUsername());
            if (usernameError != null) {
                throw new CustomException(usernameError);
            }
        }
        if (requestDTO.getEmail() != null) {
            String emailError = validationHelper.validateEmail(requestDTO.getEmail());
            if (emailError != null) {
                throw new CustomException(emailError);
            }
        }
        if (requestDTO.getName() != null && requestDTO.getName().length() > 255) {
            throw new CustomException(localeHelper.getMessage("validation.error.over_max_length", new LocaleKey("field.name"), 255));
        }
    }
}
