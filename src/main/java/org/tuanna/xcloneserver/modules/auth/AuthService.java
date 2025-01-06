package org.tuanna.xcloneserver.modules.auth;

import org.tuanna.xcloneserver.exception.CustomException;
import org.tuanna.xcloneserver.modules.auth.dtos.AuthResponseDTO;
import org.tuanna.xcloneserver.modules.auth.dtos.LoginRequestDTO;
import org.tuanna.xcloneserver.modules.auth.dtos.RegisterRequestDTO;

public interface AuthService {

    AuthResponseDTO login(LoginRequestDTO requestDTO) throws CustomException;

    AuthResponseDTO register(RegisterRequestDTO requestDTO) throws CustomException;

    AuthResponseDTO refreshAccessToken(String refreshJwt) throws CustomException;
}
