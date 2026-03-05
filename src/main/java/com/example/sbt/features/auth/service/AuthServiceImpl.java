package com.example.sbt.features.auth.service;

import com.example.sbt.common.dto.RequestContext;
import com.example.sbt.common.dto.RequestContextHolder;
import com.example.sbt.common.util.CommonUtils;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.EmailUtils;
import com.example.sbt.common.util.TOTPUtils;
import com.example.sbt.features.auth.dto.*;
import com.example.sbt.features.authtoken.dto.AuthTokenDTO;
import com.example.sbt.features.authtoken.entity.AuthToken;
import com.example.sbt.features.authtoken.service.AuthTokenService;
import com.example.sbt.features.authtoken.service.JWTService;
import com.example.sbt.features.configuration.service.Configurations;
import com.example.sbt.features.email.service.EmailService;
import com.example.sbt.features.loginaudit.service.LoginAuditService;
import com.example.sbt.features.notification.service.NotificationService;
import com.example.sbt.features.permission.service.PermissionService;
import com.example.sbt.features.user.dto.ChangePasswordRequest;
import com.example.sbt.features.user.dto.UserDTO;
import com.example.sbt.features.user.entity.User;
import com.example.sbt.features.user.mapper.UserMapper;
import com.example.sbt.features.user.repository.UserRepository;
import com.example.sbt.infrastructure.exception.CustomException;
import com.example.sbt.infrastructure.exception.NoRollbackException;
import com.example.sbt.infrastructure.security.AuthHelper;
import com.example.sbt.infrastructure.web.helper.LocaleHelper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
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
        String accessJwt = jwtService.createAccessJwt(userId, permissionCodes);
        AuthTokenDTO refreshToken = authTokenService.createRefreshToken(userId);
        return LoginResponse.builder()
                .accessToken(accessJwt)
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
    @Transactional(noRollbackFor = NoRollbackException.class)
    public LoginResponse login(LoginRequest requestDTO, RequestContext requestContext) {
        validateIp(requestContext.getIp());

        authValidator.sanitizeLogin(requestDTO);

        checkRateLimit(requestDTO.getUsername());

        UserDTO userDTO = findUserByUsernameOrThrow(requestDTO.getUsername());
        verifyPassword(requestDTO.getPassword(), userDTO);
        verifyOtpIfEnabled(requestDTO.getOtpCode(), userDTO);
        checkEmailDomain(userDTO.getEmail(), configurations.getLoginEmailDomains());
        checkAccountEnabled(userDTO);

        loginAuditService.add(userDTO.getId(), true);

        List<String> permissions = permissionService.findAllCodesByUserId(userDTO.getId());

        return createLoginResponse(userDTO.getId(), permissions);
    }

    private void checkRateLimit(String username) {
        Integer maxAttempts = configurations.getLoginMaxAttempts();
        Integer timeWindow = configurations.getLoginTimeWindow();
        if (CommonUtils.isPositive(maxAttempts) && CommonUtils.isPositive(timeWindow)) {
            long attempts = loginAuditService.countRecentlyFailedAttemptsByUsername(username, Instant.now().minusSeconds(timeWindow));
            if (attempts >= maxAttempts) {
                throw new CustomException(localeHelper.getMessage("auth.error.login_attempts_exceeded"), HttpStatus.UNAUTHORIZED);
            }
        }
    }

    private UserDTO findUserByUsernameOrThrow(String username) {
        UserDTO userDTO = userRepository.findTopByUsername(username).map(userMapper::toDTO).orElse(null);
        if (userDTO == null) {
            throw new CustomException(localeHelper.getMessage("auth.error.invalid_credentials"), HttpStatus.UNAUTHORIZED);
        }
        return userDTO;
    }

    private void verifyPassword(String rawPassword, UserDTO userDTO) {
        boolean isPasswordCorrect = authHelper.verifyPassword(rawPassword, userDTO.getPassword());
        if (!isPasswordCorrect) {
            loginAuditService.add(userDTO.getId(), false);
            throw new CustomException(localeHelper.getMessage("auth.error.invalid_credentials"), HttpStatus.UNAUTHORIZED);
        }
    }

    private void verifyOtpIfEnabled(String otpCode, UserDTO userDTO) {
        if (!ConversionUtils.safeToBoolean(userDTO.getIsOtpEnabled())) return;
        if (StringUtils.isBlank(otpCode)) {
            throw new CustomException(localeHelper.getMessage("auth.error.missing_otp_code"), HttpStatus.UNAUTHORIZED);
        }
        boolean isOtpCorrect = TOTPUtils.verify(otpCode, userDTO.getOtpSecret());
        if (!isOtpCorrect) {
            loginAuditService.add(userDTO.getId(), false);
            throw new CustomException(localeHelper.getMessage("auth.error.invalid_otp_code"), HttpStatus.UNAUTHORIZED);
        }
    }

    private void checkEmailDomain(String email, List<String> validDomains) {
        if (validDomains == null) return;
        String emailDomain = EmailUtils.extractDomain(email);
        if (!validDomains.contains(emailDomain)) {
            throw new CustomException(localeHelper.getMessage("auth.error.invalid_domains"), HttpStatus.BAD_REQUEST);
        }
    }

    private void checkAccountEnabled(UserDTO userDTO) {
        if (!ConversionUtils.safeToBoolean(userDTO.getIsEnabled())) {
            throw new CustomException(localeHelper.getMessage("auth.error.account_disabled"), HttpStatus.UNAUTHORIZED);
        }
    }


    @Override
    @Transactional
    public void register(RegisterRequest requestDTO, RequestContext requestContext) {
        validateIp(requestContext.getIp());

        authValidator.sanitizeRegister(requestDTO);
        authValidator.validateRegisterBusiness(requestDTO);

        Boolean isRegistrationEnabled = configurations.isRegistrationEnabled();
        if (isRegistrationEnabled != null && !isRegistrationEnabled) {
            throw new CustomException(localeHelper.getMessage("auth.error.registration_disabled"), HttpStatus.BAD_REQUEST);
        }

        List<String> validDomains = configurations.getRegisterEmailDomains();
        if (validDomains != null) {
            String emailDomain = EmailUtils.extractDomain(requestDTO.getEmail());
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
        user.setTenantId(RequestContextHolder.get().getTenantId());
        user = userRepository.save(user);

        AuthTokenDTO authTokenDTO = authTokenService.createActivateAccountToken(user.getId());
        String name = ObjectUtils.firstNonNull(user.getName(), user.getUsername(), user.getEmail(), "");
        emailService.sendActivateAccountEmail(user.getEmail(), name, authTokenDTO.getValue(), user.getId());
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest requestDTO, UUID userId) {

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
    @Transactional
    public LoginResponse refreshAccessToken(String refreshJwt, RequestContext requestContext) {
        validateIp(requestContext.getIp());

        AuthTokenDTO authTokenDTO = authTokenService.findOneAndVerifyJwt(refreshJwt, AuthToken.Type.REFRESH_TOKEN);
        if (authTokenDTO == null) {
            throw new CustomException(localeHelper.getMessage("auth.error.invalid_credentials"), HttpStatus.UNAUTHORIZED);
        }
        UserDTO userDTO = userRepository.findById(authTokenDTO.getUserId()).map(userMapper::toDTO).orElse(null);
        if (userDTO == null) {
            throw new CustomException(localeHelper.getMessage("auth.error.invalid_credentials"), HttpStatus.UNAUTHORIZED);
        }
        if (!ConversionUtils.safeToBoolean(userDTO.getIsEnabled())) {
            throw new CustomException(localeHelper.getMessage("auth.error.account_disabled"), HttpStatus.UNAUTHORIZED);
        }
        List<String> permissions = permissionService.findAllCodesByUserId(authTokenDTO.getUserId());
        return LoginResponse.builder()
                .accessToken(jwtService.createAccessJwt(authTokenDTO.getUserId(), permissions))
                .build();
    }

    @Override
    @Transactional
    public LoginResponse exchangeOauth2Token(String jwt, RequestContext requestContext) {
        validateIp(requestContext.getIp());

        AuthTokenDTO authTokenDTO = authTokenService.findOneAndVerifyJwt(jwt, AuthToken.Type.OAUTH2);
        if (authTokenDTO == null) {
            throw new CustomException(localeHelper.getMessage("auth.error.invalid_credentials"), HttpStatus.UNAUTHORIZED);
        }
        UserDTO userDTO = userRepository.findById(authTokenDTO.getUserId()).map(userMapper::toDTO).orElse(null);
        if (userDTO == null) {
            throw new CustomException(localeHelper.getMessage("auth.error.invalid_credentials"), HttpStatus.UNAUTHORIZED);
        }
        if (!ConversionUtils.safeToBoolean(userDTO.getIsEnabled())) {
            throw new CustomException(localeHelper.getMessage("auth.error.account_disabled"), HttpStatus.UNAUTHORIZED);
        }
        List<String> permissions = permissionService.findAllCodesByUserId(userDTO.getId());
        return createLoginResponse(userDTO.getId(), permissions);
    }

    @Override
    @Transactional(noRollbackFor = NoRollbackException.class)
    public void requestResetPassword(RequestResetPasswordRequest requestDTO) {
        authValidator.sanitizeRequestResetPassword(requestDTO);
        User user = userRepository.findTopByEmailAndIsEnabled(requestDTO.getEmail(), true).orElse(null);
        if (user == null) {
            return;
        }
        authTokenService.invalidateByUserIdAndType(user.getId(), AuthToken.Type.RESET_PASSWORD);
        AuthTokenDTO authTokenDTO = authTokenService.createResetPasswordToken(user.getId());
        String name = ObjectUtils.firstNonNull(user.getName(), user.getUsername(), "");
        emailService.sendResetPasswordEmail(user.getEmail(), name, authTokenDTO.getValue(), user.getId());
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest requestDTO) {

        AuthTokenDTO authTokenDTO = authTokenService.findOneAndVerifyJwt(requestDTO.getToken(), AuthToken.Type.RESET_PASSWORD);
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
    @Transactional(noRollbackFor = NoRollbackException.class)
    public void requestActivateAccount(RequestActivateAccountRequest requestDTO) {
        authValidator.sanitizeRequestActivateAccount(requestDTO);
        User user = userRepository.findTopByEmailAndIsEnabled(requestDTO.getEmail(), false).orElse(null);
        if (user == null) {
            throw new CustomException(localeHelper.getMessage("auth.activate_account_email_sent"), HttpStatus.UNAUTHORIZED);
        }
        authTokenService.invalidateByUserIdAndType(user.getId(), AuthToken.Type.ACTIVATE_ACCOUNT);
        AuthTokenDTO authTokenDTO = authTokenService.createActivateAccountToken(user.getId());
        String name = ObjectUtils.firstNonNull(user.getName(), user.getUsername(), "");
        emailService.sendActivateAccountEmail(user.getEmail(), name, authTokenDTO.getValue(), user.getId());
    }

    @Override
    @Transactional
    public void activateAccount(String jwt) {
        AuthTokenDTO authTokenDTO = authTokenService.findOneAndVerifyJwt(jwt, AuthToken.Type.ACTIVATE_ACCOUNT);
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
    @Transactional
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
    @Transactional
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
    @Transactional
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