package com.example.springboot.modules.authentication;

import com.example.springboot.exception.CustomException;
import com.example.springboot.modules.authentication.dtos.ForgotPasswordRequestDTO;
import com.example.springboot.modules.authentication.dtos.LoginRequestDTO;
import com.example.springboot.modules.authentication.dtos.RegisterRequestDTO;
import com.example.springboot.modules.authentication.dtos.ResetPasswordRequestDTO;
import com.example.springboot.utils.I18nHelper;
import com.example.springboot.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthValidator {

    private final I18nHelper i18nHelper;

    public void validateRegister(RegisterRequestDTO requestDTO) throws CustomException {
        ValidationUtils.notEmpty(requestDTO.getUsername(), i18nHelper.getMessageX("form.error.missing", "field.username"));
        ValidationUtils.maxLength(requestDTO.getUsername(), 255, i18nHelper.getMessage("form.error.over_max_length", 255));
        ValidationUtils.notEmpty(requestDTO.getEmail(), i18nHelper.getMessageX("form.error.missing", "field.email"));
        ValidationUtils.maxLength(requestDTO.getEmail(), 255, i18nHelper.getMessageX("form.error.over_max_length", 255));
        ValidationUtils.notEmpty(requestDTO.getPassword(), i18nHelper.getMessageX("form.error.missing", "field.password"));
        ValidationUtils.betweenLength(requestDTO.getPassword(), 12, 255, i18nHelper.getMessageX("form.error.not_between_length", "field.password", 12, 255));
    }

    public void validateLogin(LoginRequestDTO requestDTO) throws CustomException {
        ValidationUtils.notEmpty(requestDTO.getUsername(), i18nHelper.getMessageX("form.error.missing", "field.username"));
        ValidationUtils.maxLength(requestDTO.getUsername(), 255, i18nHelper.getMessageX("form.error.over_max_length", "field.username"));
        ValidationUtils.notEmpty(requestDTO.getPassword(), i18nHelper.getMessageX("form.error.missing", "field.password"));
        ValidationUtils.betweenLength(requestDTO.getPassword(), 12, 255, i18nHelper.getMessageX("form.error.not_between_length", "field.password", 12, 255));
    }

    public void validateForgotPassword(ForgotPasswordRequestDTO requestDTO) throws CustomException {
        ValidationUtils.notEmpty(requestDTO.getEmail(), i18nHelper.getMessageX("form.error.missing", "field.email"));
        ValidationUtils.isEmail(requestDTO.getEmail(), i18nHelper.getMessageX("form.error.missing", "field.email"));
        ValidationUtils.maxLength(requestDTO.getEmail(), 255, i18nHelper.getMessageX("form.error.missing", "field.email"));
    }

    public void validateResetPassword(ResetPasswordRequestDTO requestDTO) throws CustomException {
        ValidationUtils.notEmpty(requestDTO.getToken(), i18nHelper.getMessageX("form.error.missing", "field.token"));
        ValidationUtils.notEmpty(requestDTO.getNewPassword(), i18nHelper.getMessageX("form.error.missing", "field.password"));
        ValidationUtils.betweenLength(requestDTO.getNewPassword(), 12, 255, i18nHelper.getMessageX("form.error.not_between_length", "field.password"));
    }

}
