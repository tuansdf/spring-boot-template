package org.tuanna.xcloneserver.modules.auth;

import org.tuanna.xcloneserver.modules.exception.CustomException;
import org.tuanna.xcloneserver.modules.auth.dtos.AuthResponseDTO;
import org.tuanna.xcloneserver.modules.auth.dtos.LoginRequestDTO;
import org.tuanna.xcloneserver.modules.auth.dtos.RegisterRequestDTO;

import java.util.List;

public interface AuthService {

    AuthResponseDTO login(LoginRequestDTO requestDTO) throws CustomException;

    AuthResponseDTO register(RegisterRequestDTO requestDTO) throws CustomException;

    AuthResponseDTO createAccessToken(String userId, List<String> permissions);

}
