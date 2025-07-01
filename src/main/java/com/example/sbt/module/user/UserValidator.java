package com.example.sbt.module.user;

import com.example.sbt.core.exception.CustomException;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.core.util.LocaleHelper;
import com.example.sbt.core.util.LocaleHelper.LocaleKey;
import com.example.sbt.core.util.ValidationUtils;
import com.example.sbt.core.constant.CommonStatus;
import com.example.sbt.module.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class UserValidator {

    private static final List<String> validStatus = List.of(CommonStatus.ACTIVE, CommonStatus.INACTIVE);

    public void validateUpdate(UserDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", new LocaleKey("field.request")));
        }
        requestDTO.setUsername(ConversionUtils.safeTrim(requestDTO.getUsername()));
        requestDTO.setEmail(ConversionUtils.safeTrim(requestDTO.getEmail()));
        requestDTO.setName(ConversionUtils.safeTrim(requestDTO.getName()));
        String usernameError = ValidationUtils.validateUsername(requestDTO.getUsername());
        if (usernameError != null) {
            throw new CustomException(usernameError);
        }
        String emailError = ValidationUtils.validateEmail(requestDTO.getEmail());
        if (emailError != null) {
            throw new CustomException(emailError);
        }
        if (StringUtils.isNotEmpty(requestDTO.getName()) && requestDTO.getName().length() > 255) {
            throw new CustomException(LocaleHelper.getMessage("form.error.over_max_length", new LocaleKey("field.name"), 255));
        }
        if (StringUtils.isNotEmpty(requestDTO.getStatus()) && !validStatus.contains(requestDTO.getStatus())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.invalid", new LocaleKey("field.status")));
        }
    }

}
