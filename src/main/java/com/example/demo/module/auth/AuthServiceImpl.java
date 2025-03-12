package com.example.demo.module.auth;

import com.example.demo.config.RequestContextHolder;
import com.example.demo.constant.CommonStatus;
import com.example.demo.constant.CommonType;
import com.example.demo.constant.ConfigurationCode;
import com.example.demo.entity.User;
import com.example.demo.exception.CustomException;
import com.example.demo.module.auth.dto.*;
import com.example.demo.module.configuration.ConfigurationService;
import com.example.demo.module.email.EmailService;
import com.example.demo.module.jwt.JWTService;
import com.example.demo.module.jwt.dto.JWTPayload;
import com.example.demo.module.notification.NotificationService;
import com.example.demo.module.permission.PermissionService;
import com.example.demo.module.token.TokenService;
import com.example.demo.module.token.dto.TokenDTO;
import com.example.demo.module.user.UserRepository;
import com.example.demo.module.user.UserService;
import com.example.demo.module.user.dto.UserDTO;
import com.example.demo.util.CommonUtils;
import com.example.demo.util.ConversionUtils;
import com.example.demo.util.TOTPHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private final AuthValidator authValidator;
    private final NotificationService notificationService;

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

        if (ConversionUtils.safeToBool(userDTO.getOtpEnabled())) {
            if (StringUtils.isBlank(requestDTO.getOtp())) {
                throw new CustomException(HttpStatus.UNAUTHORIZED);
            }
            boolean isOtpCorrect = TOTPHelper.verify(requestDTO.getOtp(), userDTO.getOtpSecret());
            if (!isOtpCorrect) {
                throw new CustomException(HttpStatus.UNAUTHORIZED);
            }
        }

        boolean isPasswordCorrect = passwordEncoder.matches(requestDTO.getPassword(), userDTO.getPassword());
        if (!isPasswordCorrect) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }

        Set<String> permissions = permissionService.findAllCodesByUserId(userDTO.getId());

        AuthDTO responseDTO = createAuthResponse(userDTO.getId(), permissions);
        responseDTO.setUserId(userDTO.getId());
        responseDTO.setUsername(userDTO.getUsername());
        responseDTO.setEmail(userDTO.getEmail());
        responseDTO.setName(userDTO.getName());
        responseDTO.setPermissions(permissions);

        return responseDTO;
    }

    @Override
    public void register(RegisterRequestDTO requestDTO) {
        authValidator.validateRegister(requestDTO);

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
        user.setUsername(ConversionUtils.safeToString(requestDTO.getUsername()).trim());
        user.setEmail(ConversionUtils.safeToString(requestDTO.getEmail()).trim());
        user.setPassword(hashedPassword);
        user.setName(requestDTO.getName());
        user.setStatus(CommonStatus.PENDING);
        user = userRepository.save(user);

        TokenDTO tokenDTO = tokenService.createActivateAccountToken(user.getId(), false);
        emailService.sendActivateAccountEmail(user.getEmail(), CommonUtils.coalesce(user.getName(), user.getUsername(), user.getEmail()), tokenDTO.getValue(), user.getId());
    }

    @Override
    public void forgotPassword(ForgotPasswordRequestDTO requestDTO) {
        authValidator.validateForgotPassword(requestDTO);
        UserDTO userDTO = userService.findOneByEmail(requestDTO.getEmail());
        if (userDTO == null || CommonStatus.ACTIVE.equals(userDTO.getStatus())) return;
        TokenDTO tokenDTO = tokenService.createResetPasswordToken(userDTO.getId());
        emailService.sendResetPasswordEmail(userDTO.getEmail(), CommonUtils.coalesce(userDTO.getName(), userDTO.getUsername(), userDTO.getEmail()), tokenDTO.getValue(), userDTO.getId());
    }

    @Override
    public void resetPassword(ResetPasswordRequestDTO requestDTO) {
        authValidator.validateResetPassword(requestDTO);
        JWTPayload jwtPayload = jwtService.verify(requestDTO.getToken());
        if (jwtPayload == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        Integer tokenType = jwtPayload.getType();
        if (!CommonType.RESET_PASSWORD.equals(tokenType)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        UUID tokenId = ConversionUtils.toUUID(jwtPayload.getTokenId());
        TokenDTO tokenDTO = tokenService.findOneActiveById(tokenId);
        if (tokenDTO == null || !CommonType.RESET_PASSWORD.equals(tokenDTO.getType())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        Optional<User> userOptional = userRepository.findById(tokenDTO.getOwnerId());
        if (userOptional.isEmpty()) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        User user = userOptional.get();
        if (!CommonStatus.ACTIVE.equals(user.getStatus())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        user.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        userRepository.save(user);
        tokenService.deactivatePastTokens(tokenDTO.getOwnerId(), CommonType.RESET_PASSWORD);
        tokenService.deactivatePastTokens(tokenDTO.getOwnerId(), CommonType.REFRESH_TOKEN);
    }

    private AuthDTO createAuthResponse(UUID userId, Set<String> permissionCodes) {
        JWTPayload accessJwt = jwtService.createAccessJwt(userId, permissionCodes);
        TokenDTO refreshToken = tokenService.createRefreshToken(userId);
        return AuthDTO.builder()
                .accessToken(accessJwt.getValue())
                .refreshToken(refreshToken.getValue())
                .build();
    }

    @Override
    public AuthDTO refreshAccessToken(String refreshJwt) {
        if (StringUtils.isBlank(refreshJwt)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        JWTPayload jwtPayload = jwtService.verify(refreshJwt);
        if (jwtPayload == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        Integer tokenType = jwtPayload.getType();
        if (!CommonType.REFRESH_TOKEN.equals(tokenType)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        UUID tokenId = ConversionUtils.toUUID(jwtPayload.getTokenId());
        TokenDTO tokenDTO = tokenService.findOneActiveById(tokenId);
        if (tokenDTO == null || !CommonType.REFRESH_TOKEN.equals(tokenDTO.getType())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        if (!CommonStatus.ACTIVE.equals(userService.findStatusByUserId(tokenDTO.getOwnerId()))) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        Set<String> permissions = permissionService.findAllCodesByUserId(tokenDTO.getOwnerId());
        JWTPayload accessJwt = jwtService.createAccessJwt(tokenDTO.getOwnerId(), permissions);
        return AuthDTO.builder()
                .accessToken(accessJwt.getValue())
                .build();
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
        Integer tokenType = jwtPayload.getType();
        List<Integer> validTypes = List.of(CommonType.ACTIVATE_ACCOUNT, CommonType.REACTIVATE_ACCOUNT);
        if (!validTypes.contains(tokenType)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        UUID tokenId = ConversionUtils.toUUID(jwtPayload.getTokenId());
        TokenDTO tokenDTO = tokenService.findOneActiveById(tokenId);
        if (tokenDTO == null || !validTypes.contains(tokenDTO.getType())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        UserDTO userDTO = userService.findOneById(ConversionUtils.toUUID(tokenDTO.getOwnerId()));
        if (userDTO == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        if (CommonStatus.ACTIVE.equals(userDTO.getStatus())) {
            return;
        }
        userRepository.updateStatusByUserId(tokenDTO.getOwnerId(), CommonStatus.ACTIVE);
        tokenService.deactivatePastTokens(tokenDTO.getOwnerId(), tokenType);
        if (CommonType.ACTIVATE_ACCOUNT.equals(tokenType)) {
            notificationService.sendNewComerNotification(tokenDTO.getOwnerId());
        }
    }

    @Override
    public AuthDTO enableOtp() {
        Optional<User> userOptional = userRepository.findById(RequestContextHolder.get().getUserId());
        if (userOptional.isEmpty()) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        User user = userOptional.get();
        if (!CommonStatus.ACTIVE.equals(user.getStatus())) {
            throw new CustomException(HttpStatus.FORBIDDEN);
        }
        if (ConversionUtils.safeToBool(user.getOtpEnabled())) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        String secret = TOTPHelper.generateSecret();
        user.setOtpSecret(secret);
        user.setOtpEnabled(false);
        userRepository.save(user);
        return AuthDTO.builder().otpSecret(secret).build();
    }

    @Override
    public void confirmOtp(AuthDTO requestDTO) {
        Optional<User> userOptional = userRepository.findById(RequestContextHolder.get().getUserId());
        if (userOptional.isEmpty()) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        User user = userOptional.get();
        if (!CommonStatus.ACTIVE.equals(user.getStatus())) {
            throw new CustomException(HttpStatus.FORBIDDEN);
        }
        if (ConversionUtils.safeToBool(user.getOtpEnabled()) || StringUtils.isBlank(user.getOtpSecret())) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        boolean isOtpCorrect = TOTPHelper.verify(requestDTO.getOtpCode(), user.getOtpSecret());
        if (!isOtpCorrect) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        user.setOtpEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void disableOtp(AuthDTO requestDTO) {
        Optional<User> userOptional = userRepository.findById(RequestContextHolder.get().getUserId());
        if (userOptional.isEmpty()) {
            throw new CustomException(HttpStatus.UNAUTHORIZED);
        }
        User user = userOptional.get();
        if (!CommonStatus.ACTIVE.equals(user.getStatus())) {
            throw new CustomException(HttpStatus.FORBIDDEN);
        }
        if (!ConversionUtils.safeToBool(user.getOtpEnabled())) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        boolean isOtpCorrect = TOTPHelper.verify(requestDTO.getOtpCode(), user.getOtpSecret());
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