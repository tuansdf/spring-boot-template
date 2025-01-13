package org.tuanna.xcloneserver.modules.authentication;

import org.tuanna.xcloneserver.exception.CustomException;
import org.tuanna.xcloneserver.modules.authentication.dtos.*;

import java.util.Locale;

public interface AuthService {

    AuthDTO login(LoginRequestDTO requestDTO) throws CustomException;

    AuthDTO register(RegisterRequestDTO requestDTO) throws CustomException;

    String forgotPassword(ForgotPasswordRequestDTO requestDTO, Locale locale) throws CustomException;

    String resetPassword(ResetPasswordRequestDTO requestDTO, Locale locale) throws CustomException;

    AuthDTO refreshAccessToken(String refreshJwt) throws CustomException;
}
