package com.example.sbt.features.auth.service;

import com.example.sbt.common.dto.RequestContext;
import com.example.sbt.features.auth.dto.*;
import com.example.sbt.features.user.dto.ChangePasswordRequest;

import java.util.UUID;

public interface AuthService {
    LoginResponse login(LoginRequest requestDTO, RequestContext requestContext);

    void register(RegisterRequest requestDTO, RequestContext requestContext);

    void changePassword(ChangePasswordRequest requestDTO, UUID userId);

    void requestResetPassword(RequestResetPasswordRequest requestDTO);

    void resetPassword(ResetPasswordRequest requestDTO);

    LoginResponse refreshAccessToken(String refreshJwt, RequestContext requestContext);

    LoginResponse exchangeOauth2Token(String jwt, RequestContext requestContext);

    void requestActivateAccount(RequestActivateAccountRequest requestDTO);

    void activateAccount(String jwt);

    EnableOtpResponse enableOtp(EnableOtpRequest requestDTO, UUID userId);

    void confirmOtp(ConfirmOtpRequest requestDTO, UUID userId);

    void disableOtp(DisableOtpRequest requestDTO, UUID userId);
}
