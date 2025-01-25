package com.example.springboot.modules.authentication;

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

    public void validateRegister(RegisterRequestDTO requestDTO) {
        ValidationUtils.notEmpty(requestDTO.getUsername(), I18nHelper.getMessageX("form.error.missing", "field.username"));
        ValidationUtils.maxLength(requestDTO.getUsername(), 255, I18nHelper.getMessage("form.error.over_max_length", 255));
        ValidationUtils.notEmpty(requestDTO.getEmail(), I18nHelper.getMessageX("form.error.missing", "field.email"));
        ValidationUtils.maxLength(requestDTO.getEmail(), 255, I18nHelper.getMessageX("form.error.over_max_length", 255));
        ValidationUtils.notEmpty(requestDTO.getPassword(), I18nHelper.getMessageX("form.error.missing", "field.password"));
        ValidationUtils.betweenLength(requestDTO.getPassword(), 12, 255, I18nHelper.getMessageX("form.error.not_between_length", "field.password", 12, 255));
    }

    public void validateLogin(LoginRequestDTO requestDTO) {
        ValidationUtils.notEmpty(requestDTO.getUsername(), I18nHelper.getMessageX("form.error.missing", "field.username"));
        ValidationUtils.maxLength(requestDTO.getUsername(), 255, I18nHelper.getMessageX("form.error.over_max_length", "field.username"));
        ValidationUtils.notEmpty(requestDTO.getPassword(), I18nHelper.getMessageX("form.error.missing", "field.password"));
        ValidationUtils.betweenLength(requestDTO.getPassword(), 12, 255, I18nHelper.getMessageX("form.error.not_between_length", "field.password", 12, 255));
    }

    public void validateForgotPassword(ForgotPasswordRequestDTO requestDTO) {
        ValidationUtils.notEmpty(requestDTO.getEmail(), I18nHelper.getMessageX("form.error.missing", "field.email"));
        ValidationUtils.isEmail(requestDTO.getEmail(), I18nHelper.getMessageX("form.error.missing", "field.email"));
        ValidationUtils.maxLength(requestDTO.getEmail(), 255, I18nHelper.getMessageX("form.error.missing", "field.email"));
    }

    public void validateResetPassword(ResetPasswordRequestDTO requestDTO) {
        ValidationUtils.notEmpty(requestDTO.getToken(), I18nHelper.getMessageX("form.error.missing", "field.token"));
        ValidationUtils.notEmpty(requestDTO.getNewPassword(), I18nHelper.getMessageX("form.error.missing", "field.password"));
        ValidationUtils.betweenLength(requestDTO.getNewPassword(), 12, 255, I18nHelper.getMessageX("form.error.not_between_length", "field.password"));
    }

}
