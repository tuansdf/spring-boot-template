package com.example.sbt.module.auth.service;

import com.example.sbt.common.dto.RequestContext;
import com.example.sbt.module.auth.dto.*;
import com.example.sbt.module.user.dto.ChangePasswordRequest;

import java.util.UUID;

public interface AuthService {
    LoginResponse login(LoginRequest requestDTO, RequestContext requestContext);

    void register(RegisterRequest requestDTO, RequestContext requestContext);

    void changePassword(ChangePasswordRequest requestDTO, UUID userId);

    RefreshTokenResponse exchangeOauth2Token(String jwt, RequestContext requestContext);

    void requestResetPassword(RequestResetPasswordRequest requestDTO);

    void resetPassword(ResetPasswordRequest requestDTO);

    RefreshTokenResponse refreshAccessToken(String refreshJwt, RequestContext requestContext);

    void requestActivateAccount(RequestActivateAccountRequest requestDTO);

    void activateAccount(String jwt);

    EnableOtpResponse enableOtp(EnableOtpRequest requestDTO, UUID userId);

    void confirmOtp(ConfirmOtpRequest requestDTO, UUID userId);

    void disableOtp(DisableOtpRequest requestDTO, UUID userId);
}
