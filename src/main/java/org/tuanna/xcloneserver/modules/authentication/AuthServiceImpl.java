package org.tuanna.xcloneserver.modules.authentication;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tuanna.xcloneserver.constants.ConfigurationCode;
import org.tuanna.xcloneserver.constants.CommonStatus;
import org.tuanna.xcloneserver.constants.CommonType;
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
    private final UserRepository userRepository;

    @Override
    public AuthDTO login(LoginRequestDTO requestDTO, Locale locale) throws CustomException {
        requestDTO.validate();

        UserDTO userDTO = userService.findOneByUsername(requestDTO.getUsername());
        if (userDTO == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }

        boolean isActive = CommonStatus.ACTIVE.equals(userDTO.getStatus());
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
    public void register(RegisterRequestDTO requestDTO, Locale locale) throws CustomException {
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
        user.setUsername(ConversionUtils.toString(requestDTO.getUsername()).trim());
        user.setEmail(ConversionUtils.toString(requestDTO.getEmail()).trim());
        user.setPassword(hashedPassword);
        user.setName(requestDTO.getName());
        user.setStatus(CommonStatus.PENDING);
        user = userRepository.save(user);

        TokenDTO tokenDTO = tokenService.createActivateAccountToken(user.getId());
        emailService.sendActivateAccountEmail(user.getEmail(), user.getName(), tokenDTO.getValue(), user.getId(), locale);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequestDTO requestDTO, Locale locale) throws CustomException {
        requestDTO.validate();
        UserDTO userDTO = userService.findOneByEmail(requestDTO.getEmail());
        if (userDTO == null) return;
        TokenDTO tokenDTO = tokenService.createResetPasswordToken(userDTO.getId());
        emailService.sendResetPasswordEmail(userDTO.getEmail(), userDTO.getName(), tokenDTO.getValue(), userDTO.getId(), locale);
    }

    @Override
    public void resetPassword(ResetPasswordRequestDTO requestDTO, Locale locale) throws CustomException {
        requestDTO.validate();
        JWTPayload jwtPayload = jwtService.verify(requestDTO.getToken());
        if (jwtPayload == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        String tokenType = CommonType.fromIndex(jwtPayload.getType());
        if (!CommonType.RESET_PASSWORD.equals(tokenType)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        TokenDTO tokenDTO = tokenService.findOneActiveById(ConversionUtils.toUUID(jwtPayload.getTokenId()));
        if (tokenDTO == null || !CommonType.RESET_PASSWORD.equals(tokenDTO.getType())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        userRepository.updatePasswordByUserId(tokenDTO.getOwnerId(), requestDTO.getNewPassword());
        tokenService.deactivatePastTokens(tokenDTO.getOwnerId(), CommonType.RESET_PASSWORD);
        tokenService.deactivatePastTokens(tokenDTO.getOwnerId(), CommonType.REFRESH_TOKEN);
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
        String tokenType = CommonType.fromIndex(jwtPayload.getType());
        if (!CommonType.REFRESH_TOKEN.equals(tokenType)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        TokenDTO tokenDTO = tokenService.findOneActiveById(ConversionUtils.toUUID(jwtPayload.getTokenId()));
        if (tokenDTO == null || !CommonType.REFRESH_TOKEN.equals(tokenDTO.getType())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        UserDTO user = userService.findOneById(ConversionUtils.toUUID(tokenDTO.getOwnerId()));
        if (user == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        if (!CommonStatus.ACTIVE.equals(user.getStatus())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        List<String> permissions = permissionService.findAllCodesByUserId(user.getId());
        JWTPayload accessJwt = jwtService.createAccessJwt(user.getId(), permissions);
        return AuthDTO.builder()
                .accessToken(accessJwt.getValue())
                .build();
    }

    @Override
    public void activateAccount(String jwt) throws CustomException {
        if (StringUtils.isEmpty(jwt)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        JWTPayload jwtPayload = jwtService.verify(jwt);
        if (jwtPayload == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        String tokenType = CommonType.fromIndex(jwtPayload.getType());
        if (!CommonType.ACTIVATE_ACCOUNT.equals(tokenType)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        TokenDTO tokenDTO = tokenService.findOneActiveById(ConversionUtils.toUUID(jwtPayload.getTokenId()));
        if (tokenDTO == null || !CommonType.ACTIVATE_ACCOUNT.equals(tokenDTO.getType())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        UserDTO user = userService.findOneById(ConversionUtils.toUUID(tokenDTO.getOwnerId()));
        if (user == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        userRepository.updateStatusByUserId(user.getId(), CommonStatus.ACTIVE);
        tokenService.deactivatePastTokens(user.getId(), CommonType.ACTIVATE_ACCOUNT);
    }

}
