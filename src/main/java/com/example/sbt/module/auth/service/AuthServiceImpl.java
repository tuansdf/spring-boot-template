package com.example.sbt.module.auth.service;

import com.example.sbt.core.constant.ApplicationProperties;
import com.example.sbt.core.constant.CommonStatus;
import com.example.sbt.core.constant.CommonType;
import com.example.sbt.core.constant.ConfigurationCode;
import com.example.sbt.core.dto.JWTPayload;
import com.example.sbt.core.exception.CustomException;
import com.example.sbt.core.exception.NoRollbackException;
import com.example.sbt.core.helper.AuthHelper;
import com.example.sbt.core.helper.LocaleHelper;
import com.example.sbt.module.auth.dto.*;
import com.example.sbt.module.configuration.service.ConfigurationService;
import com.example.sbt.module.email.service.EmailService;
import com.example.sbt.module.loginaudit.service.LoginAuditService;
import com.example.sbt.module.notification.service.NotificationService;
import com.example.sbt.module.permission.service.PermissionService;
import com.example.sbt.module.role.service.RoleService;
import com.example.sbt.module.token.dto.TokenDTO;
import com.example.sbt.module.token.service.JWTService;
import com.example.sbt.module.token.service.TokenService;
import com.example.sbt.module.user.dto.ChangePasswordRequestDTO;
import com.example.sbt.module.user.dto.UserDTO;
import com.example.sbt.module.user.entity.User;
import com.example.sbt.module.user.repository.UserRepository;
import com.example.sbt.module.user.service.UserService;
import com.example.sbt.shared.util.CommonUtils;
import com.example.sbt.shared.util.ConversionUtils;
import com.example.sbt.shared.util.TOTPUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class, dontRollbackOn = NoRollbackException.class)
public class AuthServiceImpl implements AuthService {
    private final ApplicationProperties applicationProperties;
    private final UserRepository userRepository;
    private final AuthHelper authHelper;
    private final LocaleHelper localeHelper;
    private final AuthValidator authValidator;
    private final JWTService jwtService;
    private final UserService userService;
    private final PermissionService permissionService;
    private final RoleService roleService;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final ConfigurationService configurationService;
    private final NotificationService notificationService;
    private final LoginAuditService loginAuditService;

    private AuthDTO createAuthResponse(UUID userId, List<String> permissionCodes) {
        JWTPayload accessJwt = jwtService.createAccessJwt(userId, permissionCodes);
        TokenDTO refreshToken = tokenService.createRefreshToken(userId);
        return AuthDTO.builder()
                .accessToken(accessJwt.getValue())
                .refreshToken(refreshToken.getValue())
                .build();
    }

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
                throw new NoRollbackException(HttpStatus.UNAUTHORIZED);
            }
        }

        boolean isPasswordCorrect = authHelper.verifyPassword(requestDTO.getPassword(), userDTO.getPassword());
        if (!isPasswordCorrect) {
            loginAuditService.add(userDTO.getId(), false);
            throw new NoRollbackException(HttpStatus.UNAUTHORIZED);
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

        String hashedPassword = authHelper.hashPassword(requestDTO.getPassword());
        User user = new User();
        user.setUsername(requestDTO.getUsername());
        user.setEmail(requestDTO.getEmail());
        user.setPassword(hashedPassword);
        user.setName(requestDTO.getName());
        user.setStatus(CommonStatus.INACTIVE);
        user.setVerified(false);
        user = userRepository.save(user);

        TokenDTO tokenDTO = tokenService.createActivateAccountToken(user.getId());
        String name = CommonUtils.coalesce(user.getName(), user.getUsername(), "");
        emailService.sendActivateAccountEmail(user.getEmail(), name, tokenDTO.getValue(), user.getId());
    }

    @Override
    public void changePassword(ChangePasswordRequestDTO requestDTO, UUID userId) {
        authValidator.validateChangePassword(requestDTO);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        if (ConversionUtils.safeToBoolean(user.getOtpEnabled())) {
            if (StringUtils.isBlank(requestDTO.getOtpCode()) || !TOTPUtils.verify(requestDTO.getOtpCode(), user.getOtpSecret())) {
                throw new CustomException(HttpStatus.UNAUTHORIZED);
            }
        }
        boolean isPasswordCorrect = authHelper.verifyPassword(requestDTO.getOldPassword(), user.getPassword());
        if (!isPasswordCorrect) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        user.setPassword(authHelper.hashPassword(requestDTO.getNewPassword()));
        user = userRepository.save(user);
        tokenService.deactivateByUserId(user.getId());
    }

    @Override
    public RefreshTokenResponseDTO refreshAccessToken(String refreshJwt) {
        TokenDTO tokenDTO = tokenService.findOneAndVerifyJwt(refreshJwt, CommonType.REFRESH_TOKEN);
        if (tokenDTO == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        UUID userId = ConversionUtils.toUUID(tokenDTO.getUserId());
        if (userId == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        boolean isUserValid = userRepository.existsByIdAndStatus(userId, CommonStatus.ACTIVE);
        if (isUserValid) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        List<String> permissions = permissionService.findAllCodesByUserId(tokenDTO.getUserId());
        JWTPayload accessJwt = jwtService.createAccessJwt(tokenDTO.getUserId(), permissions);
        return RefreshTokenResponseDTO.builder()
                .accessToken(accessJwt.getValue())
                .build();
    }

    @Override
    public void requestResetPassword(RequestResetPasswordRequestDTO requestDTO) {
        authValidator.validateRequestResetPassword(requestDTO);
        User user = userRepository.findTopByEmailAndStatus(requestDTO.getEmail(), CommonStatus.ACTIVE).orElse(null);
        if (user == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        tokenService.deactivateByUserIdAndType(user.getId(), CommonType.RESET_PASSWORD);
        TokenDTO tokenDTO = tokenService.createResetPasswordToken(user.getId());
        String name = CommonUtils.coalesce(user.getName(), user.getUsername(), "");
        emailService.sendResetPasswordEmail(user.getEmail(), name, tokenDTO.getValue(), user.getId());
    }

    @Override
    public void resetPassword(ResetPasswordRequestDTO requestDTO) {
        authValidator.validateResetPassword(requestDTO);
        TokenDTO tokenDTO = tokenService.findOneAndVerifyJwt(requestDTO.getToken(), CommonType.RESET_PASSWORD);
        if (tokenDTO == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        User user = userRepository.findTopByIdAndStatus(tokenDTO.getUserId(), CommonStatus.ACTIVE).orElse(null);
        if (user == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        user.setPassword(authHelper.hashPassword(requestDTO.getNewPassword()));
        userRepository.save(user);
        tokenService.deactivateByUserId(tokenDTO.getUserId());
    }

    @Override
    public void requestActivateAccount(RequestActivateAccountRequestDTO requestDTO) {
        authValidator.validateRequestActivateAccount(requestDTO);
        User user = userRepository.findTopByEmailAndStatus(requestDTO.getEmail(), CommonStatus.INACTIVE).orElse(null);
        if (user == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        tokenService.deactivateByUserIdAndType(user.getId(), CommonType.ACTIVATE_ACCOUNT);
        TokenDTO tokenDTO = tokenService.createActivateAccountToken(user.getId());
        String name = CommonUtils.coalesce(user.getName(), user.getUsername(), user.getEmail(), "");
        emailService.sendActivateAccountEmail(user.getEmail(), name, tokenDTO.getValue(), user.getId());
    }

    @Override
    public void activateAccount(String jwt) {
        TokenDTO tokenDTO = tokenService.findOneAndVerifyJwt(jwt, CommonType.ACTIVATE_ACCOUNT);
        if (tokenDTO == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        User user = userRepository.findById(tokenDTO.getUserId()).orElse(null);
        if (user == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        if (!CommonStatus.INACTIVE.equals(user.getStatus())) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        user.setStatus(CommonStatus.ACTIVE);
        user.setVerified(true);
        user = userRepository.save(user);
        tokenService.deactivateByUserId(tokenDTO.getUserId());
        if (!ConversionUtils.safeToBoolean(user.getVerified())) {
            notificationService.sendNewComerNotification(tokenDTO.getUserId());
        }
    }

    @Override
    public EnableOtpResponseDTO enableOtp(EnableOtpRequestDTO requestDTO, UUID userId) {
        User user = userRepository.findTopByIdAndStatus(userId, CommonStatus.ACTIVE).orElse(null);
        if (user == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        boolean isPasswordCorrect = authHelper.verifyPassword(requestDTO.getPassword(), user.getPassword());
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
        boolean isPasswordCorrect = authHelper.verifyPassword(requestDTO.getPassword(), user.getPassword());
        if (!isPasswordCorrect) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        user.setOtpSecret(null);
        user.setOtpEnabled(false);
        userRepository.save(user);
    }
}