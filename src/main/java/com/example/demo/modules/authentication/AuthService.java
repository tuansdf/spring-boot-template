package com.example.demo.modules.authentication;

import com.example.demo.modules.authentication.dtos.*;

public interface AuthService {

    AuthDTO login(LoginRequestDTO requestDTO);

    void register(RegisterRequestDTO requestDTO);

    void forgotPassword(ForgotPasswordRequestDTO requestDTO);

    void resetPassword(ResetPasswordRequestDTO requestDTO);

    AuthDTO refreshAccessToken(String refreshJwt);

    void activateAccount(String jwt);

    AuthDTO enableOtp();

    void confirmOtp(AuthDTO requestDTO);

    void disableOtp(AuthDTO requestDTO);

}
