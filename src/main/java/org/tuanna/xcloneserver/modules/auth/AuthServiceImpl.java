package org.tuanna.xcloneserver.modules.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tuanna.xcloneserver.constants.PermissionCode;
import org.tuanna.xcloneserver.constants.Status;
import org.tuanna.xcloneserver.constants.TokenType;
import org.tuanna.xcloneserver.entities.Token;
import org.tuanna.xcloneserver.exception.CustomException;
import org.tuanna.xcloneserver.modules.auth.dtos.AuthResponseDTO;
import org.tuanna.xcloneserver.modules.auth.dtos.LoginRequestDTO;
import org.tuanna.xcloneserver.modules.auth.dtos.RegisterRequestDTO;
import org.tuanna.xcloneserver.modules.jwt.JWTService;
import org.tuanna.xcloneserver.modules.jwt.dtos.JWTPayload;
import org.tuanna.xcloneserver.modules.permission.PermissionService;
import org.tuanna.xcloneserver.modules.token.TokenService;
import org.tuanna.xcloneserver.modules.user.UserService;
import org.tuanna.xcloneserver.modules.user.dtos.UserDTO;
import org.tuanna.xcloneserver.utils.ConversionUtils;
import org.tuanna.xcloneserver.utils.UUIDUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final UserService userService;
    private final PermissionService permissionService;
    private final TokenService tokenService;

    @Override
    public AuthResponseDTO login(LoginRequestDTO requestDTO) throws CustomException {
        UserDTO user = userService.findOneByUsername(requestDTO.getUsername());
        if (user == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }

        boolean isPasswordCorrect = passwordEncoder.matches(requestDTO.getPassword(), user.getPassword());
        if (!isPasswordCorrect) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }

        boolean isActive = Status.ACTIVE.equals(user.getStatus());
        if (!isActive) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }

        List<String> permissions = permissionService.findAllCodesByUserId(user.getId());

        AuthResponseDTO responseDTO = createAuthResponse(user.getId(), permissions);
        responseDTO.setUserId(user.getId());
        responseDTO.setUsername(user.getUsername());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setName(user.getName());

        return responseDTO;
    }

    @Override
    public AuthResponseDTO register(RegisterRequestDTO requestDTO) throws CustomException {
        requestDTO.validate();

        boolean isUserExisted = userService.existsByUsernameOrEmail(requestDTO.getUsername(), requestDTO.getEmail());
        if (isUserExisted) {
            throw new CustomException(HttpStatus.CONFLICT);
        }

        String hashedPassword = passwordEncoder.encode(requestDTO.getPassword());
        UserDTO user = new UserDTO();
        user.setId(UUIDUtils.generateId());
        user.setUsername(requestDTO.getUsername());
        user.setEmail(requestDTO.getEmail());
        user.setPassword(hashedPassword);
        user.setName(requestDTO.getName());
        user.setStatus(Status.ACTIVE);
        user = userService.save(user, user.getId());

        AuthResponseDTO responseDTO = createAuthResponse(user.getId(), new ArrayList<>());
        responseDTO.setUserId(user.getId());
        responseDTO.setUsername(user.getUsername());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setName(user.getName());

        return responseDTO;
    }

    private AuthResponseDTO createAuthResponse(UUID userId, List<String> permissionCodes) {
        JWTPayload accessPayload = new JWTPayload();
        accessPayload.setSubjectId(ConversionUtils.toString(userId));
        if (!CollectionUtils.isEmpty(permissionCodes)) {
            accessPayload.setPermissions(PermissionCode.toIndexes(permissionCodes));
        }
        String accessJwt = jwtService.createAccessJwt(accessPayload);
        Token refreshToken = tokenService.createRefreshJwt(JWTPayload.builder().subjectId(ConversionUtils.toString(userId)).build());
        String refreshJwt = refreshToken.getValue();
        return AuthResponseDTO.builder()
                .accessToken(accessJwt)
                .refreshToken(refreshJwt)
                .build();
    }

    @Override
    public AuthResponseDTO refreshAccessToken(String refreshJwt) throws CustomException {
        if (StringUtils.isEmpty(refreshJwt)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        JWTPayload jwtPayload = jwtService.verify(refreshJwt);
        if (StringUtils.isEmpty(jwtPayload.getTokenId()) || !TokenType.REFRESH_TOKEN.equals(TokenType.fromIndex(jwtPayload.getType()))) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        boolean isTokenValid = tokenService.validateToken(ConversionUtils.toUUID(jwtPayload.getTokenId()), refreshJwt, TokenType.REFRESH_TOKEN);
        if (!isTokenValid) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        UserDTO user = userService.findOneById(ConversionUtils.toUUID(jwtPayload.getSubjectId()));
        if (user == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        if (!Status.ACTIVE.equals(user.getStatus())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        List<String> permissions = permissionService.findAllCodesByUserId(user.getId());
        String accessJwt = jwtService.createAccessJwt(JWTPayload.builder()
                .subjectId(ConversionUtils.toString(user.getId()))
                .permissions(PermissionCode.toIndexes(permissions))
                .build());
        return AuthResponseDTO.builder()
                .accessToken(accessJwt)
                .build();
    }

}
