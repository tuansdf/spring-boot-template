package com.example.sbt.module.auth.service;

import com.example.sbt.module.auth.dto.*;
import com.example.sbt.module.user.dto.ChangePasswordRequestDTO;

import java.util.UUID;

public interface AuthService {
    AuthDTO login(LoginRequestDTO requestDTO);

    void register(RegisterRequestDTO requestDTO);

    void changePassword(ChangePasswordRequestDTO requestDTO, UUID userId);

    void requestResetPassword(RequestResetPasswordRequestDTO requestDTO);

    void resetPassword(ResetPasswordRequestDTO requestDTO);

    RefreshTokenResponseDTO refreshAccessToken(String refreshJwt);

    void requestActivateAccount(RequestActivateAccountRequestDTO requestDTO);

    void activateAccount(String jwt);

    EnableOtpResponseDTO enableOtp(EnableOtpRequestDTO requestDTO, UUID userId);

    void confirmOtp(ConfirmOtpRequestDTO requestDTO, UUID userId);

    void disableOtp(DisableOtpRequestDTO requestDTO, UUID userId);
}
