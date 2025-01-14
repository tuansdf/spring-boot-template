package org.tuanna.xcloneserver.modules.authentication;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tuanna.xcloneserver.constants.ConfigurationCode;
import org.tuanna.xcloneserver.constants.Status;
import org.tuanna.xcloneserver.constants.TokenType;
import org.tuanna.xcloneserver.entities.User;
import org.tuanna.xcloneserver.exception.CustomException;
import org.tuanna.xcloneserver.modules.authentication.dtos.*;
import org.tuanna.xcloneserver.modules.configuration.ConfigurationService;
import org.tuanna.xcloneserver.modules.email.EmailService;
import org.tuanna.xcloneserver.modules.jwt.JWTService;
import org.tuanna.xcloneserver.modules.jwt.dtos.JWTPayload;
import org.tuanna.xcloneserver.modules.permission.PermissionService;
import org.tuanna.xcloneserver.modules.token.TokenService;
import org.tuanna.xcloneserver.modules.token.dtos.TokenDTO;
import org.tuanna.xcloneserver.modules.user.UserRepository;
import org.tuanna.xcloneserver.modules.user.UserService;
import org.tuanna.xcloneserver.modules.user.dtos.UserDTO;
import org.tuanna.xcloneserver.utils.ConversionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final UserService userService;
    private final PermissionService permissionService;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final ConfigurationService configurationService;
    private final MessageSource messageSource;
    private final UserRepository userRepository;

    @Override
    public AuthDTO login(LoginRequestDTO requestDTO) throws CustomException {
        requestDTO.validate();

        UserDTO userDTO = userService.findOneByUsername(requestDTO.getUsername());
        if (userDTO == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }

        boolean isActive = Status.ACTIVE.equals(userDTO.getStatus());
        if (!isActive) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }

        boolean isPasswordCorrect = passwordEncoder.matches(requestDTO.getPassword(), userDTO.getPassword());
        if (!isPasswordCorrect) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }

        List<String> permissions = permissionService.findAllCodesByUserId(userDTO.getId());

        AuthDTO responseDTO = createAuthResponse(userDTO.getId(), permissions);
        responseDTO.setUserId(userDTO.getId());
        responseDTO.setUsername(userDTO.getUsername());
        responseDTO.setEmail(userDTO.getEmail());
        responseDTO.setName(userDTO.getName());
        responseDTO.setPermissions(permissions);

        return responseDTO;
    }

    @Override
    public AuthDTO register(RegisterRequestDTO requestDTO) throws CustomException {
        requestDTO.validate();

        Boolean isRegistrationEnabled = ConversionUtils.toBool(configurationService.findValueByCode(ConfigurationCode.IS_REGISTRATION_ENABLED));
        if (isRegistrationEnabled != null && !isRegistrationEnabled) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }

        boolean isUserExisted = userService.existsByUsernameOrEmail(requestDTO.getUsername(), requestDTO.getEmail());
        if (isUserExisted) {
            throw new CustomException(HttpStatus.CONFLICT);
        }

        String hashedPassword = passwordEncoder.encode(requestDTO.getPassword());
        User user = new User();
        user.setUsername(requestDTO.getUsername());
        user.setEmail(requestDTO.getEmail());
        user.setPassword(hashedPassword);
        user.setName(requestDTO.getName());
        user.setStatus(Status.PENDING);
        user = userRepository.save(user);

        AuthDTO responseDTO = createAuthResponse(user.getId(), new ArrayList<>());
        responseDTO.setUserId(user.getId());
        responseDTO.setUsername(user.getUsername());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setName(user.getName());

        return responseDTO;
    }

    @Override
    public String forgotPassword(ForgotPasswordRequestDTO requestDTO, Locale locale) throws CustomException {
        requestDTO.validate();
        String result = messageSource.getMessage("reset_password.email_sent", null, locale);
        UserDTO userDTO = userService.findOneByEmail(requestDTO.getEmail());
        if (userDTO == null) {
            return result;
        }
        TokenDTO tokenDTO = tokenService.createResetPasswordToken(userDTO.getId());
        emailService.sendResetPasswordEmail(userDTO.getEmail(), userDTO.getName(), tokenDTO.getValue(), userDTO.getId(), locale);
        return result;
    }

    @Override
    public String resetPassword(ResetPasswordRequestDTO requestDTO, Locale locale) throws CustomException {
        requestDTO.validate();
        JWTPayload jwtPayload = jwtService.verify(requestDTO.getToken());
        if (jwtPayload == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        String tokenType = TokenType.fromIndex(jwtPayload.getType());
        if (!TokenType.RESET_PASSWORD.equals(tokenType)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        TokenDTO tokenDTO = tokenService.findOneActiveById(ConversionUtils.toUUID(jwtPayload.getTokenId()));
        if (tokenDTO == null || !TokenType.RESET_PASSWORD.equals(tokenDTO.getType())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        userRepository.updatePasswordByUserId(tokenDTO.getOwnerId(), requestDTO.getNewPassword());
        tokenService.deactivatePastToken(tokenDTO.getOwnerId(), TokenType.RESET_PASSWORD);
        tokenService.deactivatePastToken(tokenDTO.getOwnerId(), TokenType.REFRESH_TOKEN);
        return messageSource.getMessage("reset_password.success", null, locale);
    }

    private AuthDTO createAuthResponse(UUID userId, List<String> permissionCodes) {
        JWTPayload accessJwt = jwtService.createAccessJwt(userId, permissionCodes);
        TokenDTO refreshToken = tokenService.createRefreshToken(userId);
        return AuthDTO.builder()
                .accessToken(accessJwt.getValue())
                .refreshToken(refreshToken.getValue())
                .build();
    }

    @Override
    public AuthDTO refreshAccessToken(String refreshJwt) throws CustomException {
        if (StringUtils.isEmpty(refreshJwt)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        JWTPayload jwtPayload = jwtService.verify(refreshJwt);
        if (jwtPayload == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        String tokenType = TokenType.fromIndex(jwtPayload.getType());
        if (!TokenType.REFRESH_TOKEN.equals(tokenType)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        TokenDTO tokenDTO = tokenService.findOneActiveById(ConversionUtils.toUUID(jwtPayload.getTokenId()));
        if (tokenDTO == null || !TokenType.REFRESH_TOKEN.equals(tokenDTO.getType())) {
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
        JWTPayload accessJwt = jwtService.createAccessJwt(user.getId(), permissions);
        return AuthDTO.builder()
                .accessToken(accessJwt.getValue())
                .build();
    }

}
