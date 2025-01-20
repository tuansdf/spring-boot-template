package com.example.springboot.modules.authentication;

import com.example.springboot.exception.CustomException;
import com.example.springboot.modules.authentication.dtos.*;

public interface AuthService {

    AuthDTO login(LoginRequestDTO requestDTO) throws CustomException;

    void register(RegisterRequestDTO requestDTO) throws CustomException;

    void forgotPassword(ForgotPasswordRequestDTO requestDTO) throws CustomException;

    void resetPassword(ResetPasswordRequestDTO requestDTO) throws CustomException;

    AuthDTO refreshAccessToken(String refreshJwt) throws CustomException;

    void activateAccount(String jwt) throws CustomException;

}
