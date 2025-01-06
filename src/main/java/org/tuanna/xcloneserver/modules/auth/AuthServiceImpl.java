package org.tuanna.xcloneserver.modules.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tuanna.xcloneserver.constants.PermissionCode;
import org.tuanna.xcloneserver.constants.Status;
import org.tuanna.xcloneserver.entities.Token;
import org.tuanna.xcloneserver.entities.User;
import org.tuanna.xcloneserver.exception.CustomException;
import org.tuanna.xcloneserver.modules.auth.dtos.AuthResponseDTO;
import org.tuanna.xcloneserver.modules.auth.dtos.LoginRequestDTO;
import org.tuanna.xcloneserver.modules.auth.dtos.RegisterRequestDTO;
import org.tuanna.xcloneserver.modules.jwt.JWTService;
import org.tuanna.xcloneserver.modules.jwt.dtos.JWTPayload;
import org.tuanna.xcloneserver.modules.permission.PermissionService;
import org.tuanna.xcloneserver.modules.permission.dtos.PermissionDTO;
import org.tuanna.xcloneserver.modules.token.TokenService;
import org.tuanna.xcloneserver.modules.user.UserRepository;
import org.tuanna.xcloneserver.utils.ConversionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PermissionService permissionService;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final TokenService tokenService;

    @Override
    public AuthResponseDTO login(LoginRequestDTO requestDTO) throws CustomException {
        Optional<User> userOptional = userRepository.findTopByUsernameOrEmail(requestDTO.getUsername(), requestDTO.getUsername());
        if (userOptional.isEmpty()) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }

        User user = userOptional.get();
        boolean isPasswordCorrect = passwordEncoder.matches(requestDTO.getPassword(), user.getPassword());
        if (!isPasswordCorrect) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }

        boolean isActive = Status.ACTIVE.equals(user.getStatus());
        if (!isActive) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }

        List<PermissionDTO> permissions = permissionService.findAllByUserId(user.getId());

        AuthResponseDTO responseDTO = createAuthResponse(user.getId(), permissions.stream().map(PermissionDTO::getCode).toList());
        responseDTO.setUserId(user.getId());
        responseDTO.setUsername(user.getUsername());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setName(user.getName());

        return responseDTO;
    }

    @Override
    public AuthResponseDTO register(RegisterRequestDTO requestDTO) throws CustomException {
        boolean isUserExisted = userRepository.existsByUsernameOrEmail(requestDTO.getUsername(), requestDTO.getEmail());
        if (isUserExisted) {
            throw new CustomException(HttpStatus.CONFLICT);
        }

        String hashedPassword = passwordEncoder.encode(requestDTO.getPassword());
        User user = new User();
        user.setUsername(requestDTO.getUsername());
        user.setEmail(requestDTO.getEmail());
        user.setPassword(hashedPassword);
        user.setName(requestDTO.getName());
        user.setStatus(Status.ACTIVE);
        user = userRepository.save(user);

        AuthResponseDTO responseDTO = createAuthResponse(user.getId(), new ArrayList<>());
        responseDTO.setUserId(user.getId());
        responseDTO.setUsername(user.getUsername());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setName(user.getName());

        return responseDTO;
    }

    private AuthResponseDTO createAuthResponse(UUID userId, List<String> permissions) {
        JWTPayload accessPayload = new JWTPayload();
        accessPayload.setSubjectId(userId.toString());
        if (!CollectionUtils.isEmpty(permissions)) {
            accessPayload.setPermissions(PermissionCode.toIndexes(permissions));
        }
        String accessJwt = jwtService.createAccessJwt(accessPayload);
        Token refreshToken = tokenService.createRefreshJwt(JWTPayload.builder().subjectId(userId.toString()).build());
        String refreshJwt = refreshToken.getValue();
        return AuthResponseDTO.builder()
                .accessToken(accessJwt)
                .refreshToken(refreshJwt)
                .build();
    }

    @Override
    public AuthResponseDTO refreshAccessToken(UUID userId) throws CustomException {
        if (userId == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        User user = userOptional.get();
        if (!Status.ACTIVE.equals(user.getStatus())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        List<PermissionDTO> permissions = permissionService.findAllByUserId(user.getId());
        String accessJwt = jwtService.createAccessJwt(JWTPayload.builder()
                .subjectId(ConversionUtils.safeToString(userId))
                .permissions(PermissionCode.toIndexes(permissions.stream().map(PermissionDTO::getCode).toList()))
                .build());
        return AuthResponseDTO.builder()
                .accessToken(accessJwt)
                .build();
    }

}
