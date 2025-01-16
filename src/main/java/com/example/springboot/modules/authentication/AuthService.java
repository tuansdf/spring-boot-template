package com.example.springboot.modules.authentication;

import com.example.springboot.exception.CustomException;
import com.example.springboot.modules.authentication.dtos.*;

import java.util.Locale;

public interface AuthService {

    AuthDTO login(LoginRequestDTO requestDTO, Locale locale) throws CustomException;

    void register(RegisterRequestDTO requestDTO, Locale locale) throws CustomException;

    void forgotPassword(ForgotPasswordRequestDTO requestDTO, Locale locale) throws CustomException;

    void resetPassword(ResetPasswordRequestDTO requestDTO, Locale locale) throws CustomException;

    AuthDTO refreshAccessToken(String refreshJwt) throws CustomException;

    void activateAccount(String jwt) throws CustomException;

}
