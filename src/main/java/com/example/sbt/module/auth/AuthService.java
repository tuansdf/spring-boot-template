package com.example.sbt.module.auth;

import com.example.sbt.module.auth.dto.*;
import com.example.sbt.module.user.dto.ChangePasswordRequestDTO;
import com.example.sbt.module.user.dto.UserDTO;

import java.util.UUID;

public interface AuthService {

    AuthDTO login(LoginRequestDTO requestDTO);

    void register(RegisterRequestDTO requestDTO);

    UserDTO changePassword(ChangePasswordRequestDTO requestDTO, UUID userId);

    void forgotPassword(ForgotPasswordRequestDTO requestDTO);

    void resetPassword(ResetPasswordRequestDTO requestDTO);

    AuthDTO refreshAccessToken(String refreshJwt);

    void activateAccount(String jwt);

    AuthDTO enableOtp(UUID userId);

    void confirmOtp(AuthDTO requestDTO, UUID userId);

    void disableOtp(AuthDTO requestDTO, UUID userId);

}
