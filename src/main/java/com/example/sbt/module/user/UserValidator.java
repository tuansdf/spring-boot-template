package com.example.sbt.module.user;

import com.example.sbt.common.constant.CommonStatus;
import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.util.LocaleHelper;
import com.example.sbt.common.util.ValidationUtils;
import com.example.sbt.module.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class UserValidator {

    private static final List<Integer> validStatus = List.of(CommonStatus.ACTIVE, CommonStatus.INACTIVE, CommonStatus.PENDING);

    public void validateUpdate(UserDTO requestDTO) {
        String usernameError = ValidationUtils.validateUsername(requestDTO.getUsername());
        if (usernameError != null) {
            throw new CustomException(usernameError);
        }
        String emailError = ValidationUtils.validateEmail(requestDTO.getEmail());
        if (emailError != null) {
            throw new CustomException(emailError);
        }
        if (StringUtils.isNotEmpty(requestDTO.getName()) && requestDTO.getName().length() > 255) {
            throw new CustomException(LocaleHelper.getMessageX("form.error.over_max_length", "##field.name", 255));
        }
        if (requestDTO.getStatus() != null && !validStatus.contains(requestDTO.getStatus())) {
            throw new CustomException(LocaleHelper.getMessageX("form.error.invalid", "##field.status"));
        }
    }

}
