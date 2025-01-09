package org.tuanna.xcloneserver.modules.authentication;

import org.tuanna.xcloneserver.exception.CustomException;
import org.tuanna.xcloneserver.modules.authentication.dtos.AuthResponseDTO;
import org.tuanna.xcloneserver.modules.authentication.dtos.LoginRequestDTO;
import org.tuanna.xcloneserver.modules.authentication.dtos.RegisterRequestDTO;

public interface AuthService {

    AuthResponseDTO login(LoginRequestDTO requestDTO) throws CustomException;

    AuthResponseDTO register(RegisterRequestDTO requestDTO) throws CustomException;

    AuthResponseDTO refreshAccessToken(String refreshJwt) throws CustomException;
}
