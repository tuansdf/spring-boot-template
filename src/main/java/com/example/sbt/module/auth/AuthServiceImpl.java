package com.example.sbt.module.auth;

import com.example.sbt.common.util.CommonUtils;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.TOTPUtils;
import com.example.sbt.core.constant.ApplicationProperties;
import com.example.sbt.core.constant.CommonStatus;
import com.example.sbt.core.constant.CommonType;
import com.example.sbt.core.constant.ConfigurationCode;
import com.example.sbt.core.exception.CustomException;
import com.example.sbt.core.helper.LocaleHelper;
import com.example.sbt.module.auth.dto.*;
import com.example.sbt.module.configuration.ConfigurationService;
import com.example.sbt.module.email.EmailService;
import com.example.sbt.module.jwt.JWTService;
import com.example.sbt.module.jwt.dto.JWTPayload;
import com.example.sbt.module.loginaudit.LoginAuditService;
import com.example.sbt.module.notification.NotificationService;
import com.example.sbt.module.permission.PermissionService;
import com.example.sbt.module.role.RoleService;
import com.example.sbt.module.token.TokenService;
import com.example.sbt.module.token.dto.TokenDTO;
import com.example.sbt.module.user.UserService;
import com.example.sbt.module.user.dto.ChangePasswordRequestDTO;
import com.example.sbt.module.user.dto.UserDTO;
import com.example.sbt.module.user.entity.User;
import com.example.sbt.module.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class AuthServiceImpl implements AuthService {

    private final LocaleHelper localeHelper;
    private final ApplicationProperties applicationProperties;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final UserService userService;
    private final PermissionService permissionService;
    private final RoleService roleService;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final ConfigurationService configurationService;
    private final UserRepository userRepository;
    private final AuthValidator authValidator;
    private final NotificationService notificationService;
    private final LoginAuditService loginAuditService;

    @Override
    public AuthDTO login(LoginRequestDTO requestDTO) {
        authValidator.validateLogin(requestDTO);

        UserDTO userDTO = userService.findOneByUsername(requestDTO.getUsername());
        if (userDTO == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }

        boolean isActive = CommonStatus.ACTIVE.equals(userDTO.getStatus());
        if (!isActive) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }

        Integer maxAttempts = applicationProperties.getLoginMaxAttempts();
        Integer timeWindow = applicationProperties.getLoginTimeWindow();
        if (maxAttempts != null && maxAttempts > 0 && timeWindow != null && timeWindow > 0) {
            long attempts = loginAuditService.countRecentlyFailedAttemptsByUserId(userDTO.getId(), Instant.now().minusSeconds(timeWindow));
            if (attempts >= maxAttempts) {
                throw new CustomException(localeHelper.getMessage("auth.error.login_attempts_exceeded"), HttpStatus.UNAUTHORIZED);
            }
        }

        if (ConversionUtils.safeToBoolean(userDTO.getOtpEnabled())) {
            if (StringUtils.isBlank(requestDTO.getOtpCode())) {
                throw new CustomException(HttpStatus.UNAUTHORIZED);
            }
            boolean isOtpCorrect = TOTPUtils.verify(requestDTO.getOtpCode(), userDTO.getOtpSecret());
            if (!isOtpCorrect) {
                loginAuditService.add(userDTO.getId(), false);
                throw new CustomException(HttpStatus.UNAUTHORIZED);
            }
        }

        boolean isPasswordCorrect = passwordEncoder.matches(requestDTO.getPassword(), userDTO.getPassword());
        if (!isPasswordCorrect) {
            loginAuditService.add(userDTO.getId(), false);
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }

        loginAuditService.add(userDTO.getId(), true);

        List<String> permissions = permissionService.findAllCodesByUserId(userDTO.getId());
        List<String> roles = roleService.findAllCodesByUserId(userDTO.getId());

        AuthDTO responseDTO = createAuthResponse(userDTO.getId(), permissions);
        responseDTO.setUserId(userDTO.getId());
        responseDTO.setUsername(userDTO.getUsername());
        responseDTO.setEmail(userDTO.getEmail());
        responseDTO.setName(userDTO.getName());
        responseDTO.setPermissions(permissions);
        responseDTO.setRoles(roles);

        return responseDTO;
    }

    @Override
    public void register(RegisterRequestDTO requestDTO) {
        authValidator.validateRegister(requestDTO);

        Boolean isRegistrationEnabled = ConversionUtils.toBoolean(configurationService.findValueByCode(ConfigurationCode.REGISTRATION_ENABLED));
        if (isRegistrationEnabled != null && !isRegistrationEnabled) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }

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
        user.setStatus(CommonStatus.INACTIVE);
        user.setIsVerified(false);
        user = userRepository.save(user);

        TokenDTO tokenDTO = tokenService.createActivateAccountToken(user.getId());
        String name = CommonUtils.coalesce(user.getName(), user.getUsername(), user.getEmail(), "");
        emailService.sendActivateAccountEmail(user.getEmail(), name, tokenDTO.getValue(), user.getId());
    }

    @Override
    public void changePassword(ChangePasswordRequestDTO requestDTO, UUID userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        boolean isPasswordCorrect = passwordEncoder.matches(requestDTO.getOldPassword(), user.getPassword());
        if (!isPasswordCorrect) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        user.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        user = userRepository.save(user);
        tokenService.deactivatePastTokens(user.getId());
    }


    @Override
    public void requestResetPassword(RequestResetPasswordRequestDTO requestDTO) {
        authValidator.validateRequestResetPassword(requestDTO);
        UserDTO userDTO = userService.findOneByEmail(requestDTO.getEmail());
        if (userDTO == null) return;
        tokenService.deactivatePastTokens(userDTO.getId(), CommonType.RESET_PASSWORD);
        TokenDTO tokenDTO = tokenService.createResetPasswordToken(userDTO.getId());
        String name = CommonUtils.coalesce(userDTO.getName(), userDTO.getUsername(), userDTO.getEmail(), "");
        emailService.sendResetPasswordEmail(userDTO.getEmail(), name, tokenDTO.getValue(), userDTO.getId());
    }

    @Override
    public void resetPassword(ResetPasswordRequestDTO requestDTO) {
        authValidator.validateResetPassword(requestDTO);
        JWTPayload jwtPayload = jwtService.verify(requestDTO.getToken());
        if (jwtPayload == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        String tokenType = CommonType.fromIndex(jwtPayload.getType());
        if (!CommonType.RESET_PASSWORD.equals(tokenType)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        UUID tokenId = ConversionUtils.toUUID(jwtPayload.getSubject());
        TokenDTO tokenDTO = tokenService.findOneActiveById(tokenId);
        if (tokenDTO == null || !CommonType.RESET_PASSWORD.equals(tokenDTO.getType())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        User user = userRepository.findTopByIdAndStatus(tokenDTO.getOwnerId(), CommonStatus.ACTIVE).orElse(null);
        if (user == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        user.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        userRepository.save(user);
        tokenService.deactivatePastTokens(tokenDTO.getOwnerId());
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
    public RefreshTokenResponseDTO refreshAccessToken(String refreshJwt) {
        if (StringUtils.isBlank(refreshJwt)) {
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
        UUID tokenId = ConversionUtils.toUUID(jwtPayload.getSubject());
        TokenDTO tokenDTO = tokenService.findOneActiveById(tokenId);
        if (tokenDTO == null || !CommonType.REFRESH_TOKEN.equals(tokenDTO.getType())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        boolean isUserValid = userRepository.existsByIdAndStatus(ConversionUtils.toUUID(tokenDTO.getOwnerId()), CommonStatus.ACTIVE);
        if (isUserValid) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        List<String> permissions = permissionService.findAllCodesByUserId(tokenDTO.getOwnerId());
        JWTPayload accessJwt = jwtService.createAccessJwt(tokenDTO.getOwnerId(), permissions);
        return RefreshTokenResponseDTO.builder()
                .accessToken(accessJwt.getValue())
                .build();
    }

    @Override
    public void requestActivateAccount(RequestActivateAccountRequestDTO requestDTO) {
        authValidator.validateRequestActivateAccount(requestDTO);
        UserDTO userDTO = userService.findOneByEmail(requestDTO.getEmail());
        if (userDTO == null || !CommonStatus.PENDING.equals(userDTO.getStatus())) return;
        tokenService.deactivatePastTokens(userDTO.getId(), CommonType.ACTIVATE_ACCOUNT);
        TokenDTO tokenDTO = tokenService.createActivateAccountToken(userDTO.getId());
        String name = CommonUtils.coalesce(userDTO.getName(), userDTO.getUsername(), userDTO.getEmail(), "");
        emailService.sendActivateAccountEmail(userDTO.getEmail(), name, tokenDTO.getValue(), userDTO.getId());
    }

    @Override
    public void activateAccount(String jwt) {
        if (StringUtils.isBlank(jwt)) {
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
        UUID tokenId = ConversionUtils.toUUID(jwtPayload.getSubject());
        TokenDTO tokenDTO = tokenService.findOneActiveById(tokenId);
        if (tokenDTO == null || !CommonType.ACTIVATE_ACCOUNT.equals(tokenDTO.getType())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        User user = userRepository.findById(tokenDTO.getOwnerId()).orElse(null);
        if (user == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        if (!CommonStatus.INACTIVE.equals(user.getStatus())) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        user.setStatus(CommonStatus.ACTIVE);
        user.setIsVerified(true);
        user = userRepository.save(user);
        tokenService.deactivatePastTokens(tokenDTO.getOwnerId());
        if (!ConversionUtils.safeToBoolean(user.getIsVerified())) {
            notificationService.sendNewComerNotification(tokenDTO.getOwnerId());
        }
    }

    @Override
    public EnableOtpResponseDTO enableOtp(EnableOtpRequestDTO requestDTO, UUID userId) {
        User user = userRepository.findTopByIdAndStatus(userId, CommonStatus.ACTIVE).orElse(null);
        if (user == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        boolean isPasswordCorrect = passwordEncoder.matches(requestDTO.getPassword(), user.getPassword());
        if (!isPasswordCorrect) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        if (ConversionUtils.safeToBoolean(user.getOtpEnabled())) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        String secret = TOTPUtils.generateSecret();
        user.setOtpSecret(secret);
        user.setOtpEnabled(false);
        userRepository.save(user);
        return EnableOtpResponseDTO.builder().otpSecret(secret).build();
    }

    @Override
    public void confirmOtp(ConfirmOtpRequestDTO requestDTO, UUID userId) {
        User user = userRepository.findTopByIdAndStatus(userId, CommonStatus.ACTIVE).orElse(null);
        if (user == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        if (ConversionUtils.safeToBoolean(user.getOtpEnabled()) || StringUtils.isBlank(user.getOtpSecret())) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        boolean isOtpCorrect = TOTPUtils.verify(requestDTO.getOtpCode(), user.getOtpSecret());
        if (!isOtpCorrect) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        user.setOtpEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void disableOtp(DisableOtpRequestDTO requestDTO, UUID userId) {
        User user = userRepository.findTopByIdAndStatus(userId, CommonStatus.ACTIVE).orElse(null);
        if (user == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        if (!ConversionUtils.safeToBoolean(user.getOtpEnabled())) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        boolean isOtpCorrect = TOTPUtils.verify(requestDTO.getOtpCode(), user.getOtpSecret());
        if (!isOtpCorrect) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        boolean isPasswordCorrect = passwordEncoder.matches(requestDTO.getPassword(), user.getPassword());
        if (!isPasswordCorrect) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        user.setOtpSecret(null);
        user.setOtpEnabled(false);
        userRepository.save(user);
    }

}