package com.example.sbt.module.auth.service;

import com.example.sbt.core.constant.CommonType;
import com.example.sbt.core.dto.JWTPayload;
import com.example.sbt.core.dto.RequestContext;
import com.example.sbt.core.exception.CustomException;
import com.example.sbt.core.exception.NoRollbackException;
import com.example.sbt.core.helper.AuthHelper;
import com.example.sbt.core.helper.LocaleHelper;
import com.example.sbt.module.auth.dto.*;
import com.example.sbt.module.authtoken.dto.AuthTokenDTO;
import com.example.sbt.module.authtoken.service.AuthTokenService;
import com.example.sbt.module.authtoken.service.JWTService;
import com.example.sbt.module.configuration.service.Configurations;
import com.example.sbt.module.email.service.EmailService;
import com.example.sbt.module.loginaudit.service.LoginAuditService;
import com.example.sbt.module.notification.service.NotificationService;
import com.example.sbt.module.permission.service.PermissionService;
import com.example.sbt.module.user.dto.ChangePasswordRequest;
import com.example.sbt.module.user.dto.UserDTO;
import com.example.sbt.module.user.entity.User;
import com.example.sbt.module.user.mapper.UserMapper;
import com.example.sbt.module.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final AuthHelper authHelper;
    private final LocaleHelper localeHelper;
    private final AuthValidator authValidator;
    private final Configurations configurations;
    private final UserMapper userMapper;
    private final JWTService jwtService;
    private final PermissionService permissionService;
    private final AuthTokenService authTokenService;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final LoginAuditService loginAuditService;

    private LoginResponse createLoginResponse(UUID userId, List<String> permissionCodes) {
        JWTPayload accessJwt = jwtService.createAccessJwt(userId, permissionCodes);
        AuthTokenDTO refreshToken = authTokenService.createRefreshToken(userId);
        return LoginResponse.builder()
                .accessToken(accessJwt.getValue())
                .refreshToken(refreshToken.getValue())
                .build();
    }

    private void validateIp(String ip) {
        List<String> whitelistedIps = configurations.getWhitelistedIps();
        if (whitelistedIps != null) {
            if (StringUtils.isBlank(ip) || !whitelistedIps.contains(ip)) {
                throw new CustomException(localeHelper.getMessage("auth.error.invalid_ip"), HttpStatus.BAD_REQUEST);
            }
        }
    }

    @Override
    public LoginResponse login(LoginRequest requestDTO, RequestContext requestContext) {
        validateIp(requestContext.getIp());

        authValidator.validateLogin(requestDTO);

        UserDTO userDTO = userRepository.findTopByUsername(requestDTO.getUsername()).map(userMapper::toDTO).orElse(null);
        if (userDTO == null) {
            throw new CustomException(localeHelper.getMessage("auth.error.invalid_credentials"), HttpStatus.UNAUTHORIZED);
        }

        if (!ConversionUtils.safeToBoolean(userDTO.getIsEnabled())) {
            throw new CustomException(localeHelper.getMessage("auth.error.invalid_credentials"), HttpStatus.UNAUTHORIZED);
        }

        Integer maxAttempts = configurations.getLoginMaxAttempts();
        Integer timeWindow = configurations.getLoginTimeWindow();
        if (CommonUtils.isPositive(maxAttempts) && CommonUtils.isPositive(timeWindow)) {
            long attempts = loginAuditService.countRecentlyFailedAttemptsByUserId(userDTO.getId(), Instant.now().minusSeconds(timeWindow));
            if (attempts >= maxAttempts) {
                throw new CustomException(localeHelper.getMessage("auth.error.login_attempts_exceeded"), HttpStatus.UNAUTHORIZED);
            }
        }

        if (ConversionUtils.safeToBoolean(userDTO.getIsOtpEnabled())) {
            if (StringUtils.isBlank(requestDTO.getOtpCode())) {
                throw new CustomException(localeHelper.getMessage("auth.error.missing_otp_code"), HttpStatus.UNAUTHORIZED);
            }
            boolean isOtpCorrect = TOTPUtils.verify(requestDTO.getOtpCode(), userDTO.getOtpSecret());
            if (!isOtpCorrect) {
                loginAuditService.add(userDTO.getId(), false);
                throw new CustomException(localeHelper.getMessage("auth.error.invalid_otp_code"), HttpStatus.UNAUTHORIZED);
            }
        }

        boolean isPasswordCorrect = authHelper.verifyPassword(requestDTO.getPassword(), userDTO.getPassword());
        if (!isPasswordCorrect) {
            loginAuditService.add(userDTO.getId(), false);
            throw new CustomException(localeHelper.getMessage("auth.error.invalid_credentials"), HttpStatus.UNAUTHORIZED);
        }

        List<String> validDomains = configurations.getLoginEmailDomains();
        if (validDomains != null) {
            String emailDomain = userDTO.getEmail().substring(userDTO.getEmail().lastIndexOf("@") + 1);
            if (!validDomains.contains(emailDomain)) {
                throw new CustomException(localeHelper.getMessage("auth.error.invalid_domains"), HttpStatus.BAD_REQUEST);
            }
        }

        loginAuditService.add(userDTO.getId(), true);

        List<String> permissions = permissionService.findAllCodesByUserId(userDTO.getId());

        return createLoginResponse(userDTO.getId(), permissions);
    }

    @Override
    public void register(RegisterRequest requestDTO, RequestContext requestContext) {
        validateIp(requestContext.getIp());

        authValidator.validateRegister(requestDTO);

        Boolean isRegistrationEnabled = configurations.isRegistrationEnabled();
        if (isRegistrationEnabled != null && !isRegistrationEnabled) {
            throw new CustomException(localeHelper.getMessage("auth.error.registration_disabled"), HttpStatus.BAD_REQUEST);
        }

        List<String> validDomains = configurations.getRegisterEmailDomains();
        if (validDomains != null) {
            String emailDomain = requestDTO.getEmail().substring(requestDTO.getEmail().lastIndexOf("@") + 1);
            if (!validDomains.contains(emailDomain)) {
                throw new CustomException(localeHelper.getMessage("auth.error.invalid_domains"), HttpStatus.BAD_REQUEST);
            }
        }

        boolean isUserExisted = userRepository.existsByUsernameOrEmail(requestDTO.getUsername(), requestDTO.getEmail());
        if (isUserExisted) {
            throw new CustomException(localeHelper.getMessage("auth.error.user_already_exists"), HttpStatus.CONFLICT);
        }

        String hashedPassword = authHelper.hashPassword(requestDTO.getPassword());
        User user = new User();
        user.setUsername(requestDTO.getUsername());
        user.setEmail(requestDTO.getEmail());
        user.setPassword(hashedPassword);
        user.setName(requestDTO.getName());
        user.setIsEnabled(false);
        user.setIsVerified(false);
        user = userRepository.save(user);

        AuthTokenDTO authTokenDTO = authTokenService.createActivateAccountToken(user.getId());
        String name = CommonUtils.coalesce(user.getName(), user.getUsername(), user.getEmail(), "");
        emailService.sendActivateAccountEmail(user.getEmail(), name, authTokenDTO.getValue(), user.getId());
    }

    @Override
    public void changePassword(ChangePasswordRequest requestDTO, UUID userId) {
        authValidator.validateChangePassword(requestDTO);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new CustomException(localeHelper.getMessage("auth.error.user_not_found"), HttpStatus.NOT_FOUND);
        }
        if (ConversionUtils.safeToBoolean(user.getIsOtpEnabled())) {
            if (StringUtils.isBlank(requestDTO.getOtpCode()) || !TOTPUtils.verify(requestDTO.getOtpCode(), user.getOtpSecret())) {
                throw new CustomException(localeHelper.getMessage("auth.error.invalid_otp_code"), HttpStatus.UNAUTHORIZED);
            }
        }
        boolean isPasswordCorrect = authHelper.verifyPassword(requestDTO.getOldPassword(), user.getPassword());
        if (!isPasswordCorrect) {
            throw new CustomException(localeHelper.getMessage("auth.error.invalid_password"), HttpStatus.UNAUTHORIZED);
        }
        user.setPassword(authHelper.hashPassword(requestDTO.getNewPassword()));
        user = userRepository.save(user);
        authTokenService.invalidateByUserId(user.getId());
    }

    @Override
    public RefreshTokenResponse refreshAccessToken(String refreshJwt, RequestContext requestContext) {
        validateIp(requestContext.getIp());

        AuthTokenDTO authTokenDTO = authTokenService.findOneAndVerifyJwt(refreshJwt, CommonType.REFRESH_TOKEN);
        if (authTokenDTO == null) {
            throw new CustomException(localeHelper.getMessage("auth.error.invalid_credentials"), HttpStatus.UNAUTHORIZED);
        }
        UUID userId = ConversionUtils.toUUID(authTokenDTO.getUserId());
        if (userId == null) {
            throw new CustomException(localeHelper.getMessage("auth.error.user_not_found"), HttpStatus.UNAUTHORIZED);
        }
        boolean isUserValid = userRepository.existsByIdAndIsEnabled(userId, true);
        if (!isUserValid) {
            throw new CustomException(localeHelper.getMessage("auth.error.user_not_found"), HttpStatus.UNAUTHORIZED);
        }
        List<String> permissions = permissionService.findAllCodesByUserId(authTokenDTO.getUserId());
        JWTPayload accessJwt = jwtService.createAccessJwt(authTokenDTO.getUserId(), permissions);
        return RefreshTokenResponse.builder()
                .accessToken(accessJwt.getValue())
                .build();
    }

    @Override
    public void requestResetPassword(RequestResetPasswordRequest requestDTO) {
        authValidator.validateRequestResetPassword(requestDTO);
        User user = userRepository.findTopByEmailAndIsEnabled(requestDTO.getEmail(), true).orElse(null);
        if (user == null) {
            throw new CustomException(localeHelper.getMessage("auth.reset_password_email_sent"), HttpStatus.BAD_REQUEST);
        }
        authTokenService.invalidateByUserIdAndType(user.getId(), CommonType.RESET_PASSWORD);
        AuthTokenDTO authTokenDTO = authTokenService.createResetPasswordToken(user.getId());
        String name = CommonUtils.coalesce(user.getName(), user.getUsername(), "");
        emailService.sendResetPasswordEmail(user.getEmail(), name, authTokenDTO.getValue(), user.getId());
    }

    @Override
    public void resetPassword(ResetPasswordRequest requestDTO) {
        authValidator.validateResetPassword(requestDTO);
        AuthTokenDTO authTokenDTO = authTokenService.findOneAndVerifyJwt(requestDTO.getToken(), CommonType.RESET_PASSWORD);
        if (authTokenDTO == null) {
            throw new CustomException(localeHelper.getMessage("auth.error.invalid_credentials"), HttpStatus.UNAUTHORIZED);
        }
        User user = userRepository.findTopByIdAndIsEnabled(authTokenDTO.getUserId(), true).orElse(null);
        if (user == null) {
            throw new CustomException(localeHelper.getMessage("auth.error.user_not_found"), HttpStatus.UNAUTHORIZED);
        }
        user.setPassword(authHelper.hashPassword(requestDTO.getNewPassword()));
        userRepository.save(user);
        authTokenService.invalidateByUserId(authTokenDTO.getUserId());
    }

    @Override
    public void requestActivateAccount(RequestActivateAccountRequest requestDTO) {
        authValidator.validateRequestActivateAccount(requestDTO);
        User user = userRepository.findTopByEmailAndIsEnabled(requestDTO.getEmail(), false).orElse(null);
        if (user == null) {
            throw new CustomException(localeHelper.getMessage("auth.activate_account_email_sent"), HttpStatus.UNAUTHORIZED);
        }
        authTokenService.invalidateByUserIdAndType(user.getId(), CommonType.ACTIVATE_ACCOUNT);
        AuthTokenDTO authTokenDTO = authTokenService.createActivateAccountToken(user.getId());
        String name = CommonUtils.coalesce(user.getName(), user.getUsername(), "");
        emailService.sendActivateAccountEmail(user.getEmail(), name, authTokenDTO.getValue(), user.getId());
    }

    @Override
    public void activateAccount(String jwt) {
        AuthTokenDTO authTokenDTO = authTokenService.findOneAndVerifyJwt(jwt, CommonType.ACTIVATE_ACCOUNT);
        if (authTokenDTO == null) {
            throw new CustomException(localeHelper.getMessage("auth.error.invalid_credentials"), HttpStatus.UNAUTHORIZED);
        }
        User user = userRepository.findById(authTokenDTO.getUserId()).orElse(null);
        if (user == null) {
            throw new CustomException(localeHelper.getMessage("auth.error.user_not_found"), HttpStatus.UNAUTHORIZED);
        }
        if (ConversionUtils.safeToBoolean(user.getIsEnabled())) {
            throw new CustomException(localeHelper.getMessage("auth.error.account_already_active"), HttpStatus.BAD_REQUEST);
        }
        boolean isVerified = user.getIsVerified();
        user.setIsEnabled(true);
        user.setIsVerified(true);
        userRepository.save(user);
        authTokenService.invalidateByUserId(authTokenDTO.getUserId());
        if (!ConversionUtils.safeToBoolean(isVerified)) {
            notificationService.sendNewComerNotification(authTokenDTO.getUserId());
        }
    }

    @Override
    public EnableOtpResponse enableOtp(EnableOtpRequest requestDTO, UUID userId) {
        User user = userRepository.findTopByIdAndIsEnabled(userId, true).orElse(null);
        if (user == null) {
            throw new CustomException(localeHelper.getMessage("auth.error.user_not_found"), HttpStatus.UNAUTHORIZED);
        }
        if (ConversionUtils.safeToBoolean(user.getIsOtpEnabled())) {
            throw new CustomException(localeHelper.getMessage("auth.error.otp_already_enabled"), HttpStatus.BAD_REQUEST);
        }
        boolean isPasswordCorrect = authHelper.verifyPassword(requestDTO.getPassword(), user.getPassword());
        if (!isPasswordCorrect) {
            throw new CustomException(localeHelper.getMessage("auth.error.invalid_password"), HttpStatus.UNAUTHORIZED);
        }
        String secret = TOTPUtils.generateSecret();
        user.setOtpSecret(secret);
        user.setIsOtpEnabled(false);
        userRepository.save(user);
        return EnableOtpResponse.builder().otpSecret(secret).build();
    }

    @Override
    public void confirmOtp(ConfirmOtpRequest requestDTO, UUID userId) {
        User user = userRepository.findTopByIdAndIsEnabled(userId, true).orElse(null);
        if (user == null) {
            throw new CustomException(localeHelper.getMessage("auth.error.user_not_found"), HttpStatus.UNAUTHORIZED);
        }
        if (ConversionUtils.safeToBoolean(user.getIsOtpEnabled()) || StringUtils.isBlank(user.getOtpSecret())) {
            throw new CustomException(localeHelper.getMessage("auth.error.otp_already_enabled"), HttpStatus.BAD_REQUEST);
        }
        boolean isOtpCorrect = TOTPUtils.verify(requestDTO.getOtpCode(), user.getOtpSecret());
        if (!isOtpCorrect) {
            throw new CustomException(localeHelper.getMessage("auth.error.invalid_otp_code"), HttpStatus.BAD_REQUEST);
        }
        user.setIsOtpEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void disableOtp(DisableOtpRequest requestDTO, UUID userId) {
        User user = userRepository.findTopByIdAndIsEnabled(userId, true).orElse(null);
        if (user == null) {
            throw new CustomException(localeHelper.getMessage("auth.error.user_not_found"), HttpStatus.UNAUTHORIZED);
        }
        if (!ConversionUtils.safeToBoolean(user.getIsOtpEnabled())) {
            throw new CustomException(localeHelper.getMessage("auth.error.otp_not_enabled"), HttpStatus.BAD_REQUEST);
        }
        boolean isOtpCorrect = TOTPUtils.verify(requestDTO.getOtpCode(), user.getOtpSecret());
        if (!isOtpCorrect) {
            throw new CustomException(localeHelper.getMessage("auth.error.invalid_otp_code"), HttpStatus.BAD_REQUEST);
        }
        boolean isPasswordCorrect = authHelper.verifyPassword(requestDTO.getPassword(), user.getPassword());
        if (!isPasswordCorrect) {
            throw new CustomException(localeHelper.getMessage("auth.error.invalid_password"), HttpStatus.UNAUTHORIZED);
        }
        user.setOtpSecret(null);
        user.setIsOtpEnabled(false);
        userRepository.save(user);
    }
}