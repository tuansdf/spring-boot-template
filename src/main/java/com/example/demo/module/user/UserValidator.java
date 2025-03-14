package com.example.demo.module.user;

import com.example.demo.common.constant.CommonStatus;
import com.example.demo.common.exception.CustomException;
import com.example.demo.common.util.I18nHelper;
import com.example.demo.common.util.ValidationUtils;
import com.example.demo.module.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class UserValidator {

    private static final List<Integer> validStatus = List.of(CommonStatus.ACTIVE, CommonStatus.INACTIVE, CommonStatus.PENDING);

    public void validateUpdate(UserDTO requestDTO) {
        if (StringUtils.isEmpty(requestDTO.getUsername())) {
            throw new CustomException(I18nHelper.getMessageX("form.error.missing", "field.username"));
        }
        if (requestDTO.getUsername().length() > 255) {
            throw new CustomException(I18nHelper.getMessageX("form.error.over_max_length", "field.username", 255));
        }
        if (StringUtils.isEmpty(requestDTO.getEmail())) {
            throw new CustomException(I18nHelper.getMessageX("form.error.missing", "field.email"));
        }
        String emailError = ValidationUtils.validateEmail(requestDTO.getEmail());
        if (emailError != null) {
            throw new CustomException(emailError);
        }
        if (StringUtils.isNotEmpty(requestDTO.getEmail()) && requestDTO.getEmail().length() > 255) {
            throw new CustomException(I18nHelper.getMessageX("form.error.over_max_length", "field.email", 255));
        }
        if (StringUtils.isNotEmpty(requestDTO.getName()) && requestDTO.getName().length() > 255) {
            throw new CustomException(I18nHelper.getMessageX("form.error.over_max_length", "field.name", 255));
        }
        if (requestDTO.getStatus() != null && !validStatus.contains(requestDTO.getStatus())) {
            throw new CustomException(I18nHelper.getMessageX("form.error.invalid", "field.status"));
        }
    }

}
