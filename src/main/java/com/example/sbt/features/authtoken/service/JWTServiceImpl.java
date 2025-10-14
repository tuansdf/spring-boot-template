package com.example.sbt.features.authtoken.service;

import com.example.sbt.common.util.DateUtils;
import com.example.sbt.features.authtoken.entity.AuthToken;
import com.example.sbt.infrastructure.security.JWTKeyID;
import com.example.sbt.infrastructure.security.JWTLifetime;
import com.example.sbt.infrastructure.security.JWTPayload;
import com.example.sbt.infrastructure.security.JWTPayloadKey;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class JWTServiceImpl implements JWTService {
    private final ObjectMapper objectMapper;
    private final Map<String, JWSHeader> jwsHeaders;
    private final Map<String, JWSSigner> jwsSigners;
    private final Map<String, JWSVerifier> jwsVerifiers;
    private final Map<String, JWTLifetime> jwtLifetimes;

    @Override
    public String createOauth2ExchangeJwt(UUID userId) {
        try {
            Instant now = Instant.now();
            String keyId = JWTKeyID.OAUTH2;
            AuthToken.Type type = AuthToken.Type.OAUTH2;

            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder();
            claimsBuilder.issueTime(DateUtils.toDate(now));
            claimsBuilder.notBeforeTime(DateUtils.toDate(now));
            claimsBuilder.expirationTime(DateUtils.toDate(now.plusSeconds(jwtLifetimes.get(keyId).seconds())));
            claimsBuilder.subject(userId.toString());
            claimsBuilder.claim(JWTPayloadKey.TYPE, type);
            JWTClaimsSet claimsSet = claimsBuilder.build();

            SignedJWT signedJWT = new SignedJWT(jwsHeaders.get(keyId), claimsSet);
            signedJWT.sign(jwsSigners.get(keyId));
            return signedJWT.serialize();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String createAccessJwt(UUID userId, List<String> permissions) {
        try {
            Instant now = Instant.now();
            String keyId = JWTKeyID.ACCESS;
            AuthToken.Type type = AuthToken.Type.ACCESS_TOKEN;

            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder();
            claimsBuilder.issueTime(DateUtils.toDate(now));
            claimsBuilder.notBeforeTime(DateUtils.toDate(now));
            claimsBuilder.expirationTime(DateUtils.toDate(now.plusSeconds(jwtLifetimes.get(keyId).seconds())));
            claimsBuilder.subject(userId.toString());
            claimsBuilder.claim(JWTPayloadKey.TYPE, type);
            claimsBuilder.claim(JWTPayloadKey.SCOPE, String.join(" ", permissions));
            JWTClaimsSet claimsSet = claimsBuilder.build();

            SignedJWT signedJWT = new SignedJWT(jwsHeaders.get(keyId), claimsSet);
            signedJWT.sign(jwsSigners.get(keyId));
            return signedJWT.serialize();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String createRefreshJwt(UUID userId) {
        try {
            Instant now = Instant.now();
            String keyId = JWTKeyID.REFRESH;
            AuthToken.Type type = AuthToken.Type.REFRESH_TOKEN;

            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder();
            claimsBuilder.issueTime(DateUtils.toDate(now));
            claimsBuilder.notBeforeTime(DateUtils.toDate(now));
            claimsBuilder.expirationTime(DateUtils.toDate(now.plusSeconds(jwtLifetimes.get(keyId).seconds())));
            claimsBuilder.subject(userId.toString());
            claimsBuilder.claim(JWTPayloadKey.TYPE, type);
            JWTClaimsSet claimsSet = claimsBuilder.build();

            SignedJWT signedJWT = new SignedJWT(jwsHeaders.get(keyId), claimsSet);
            signedJWT.sign(jwsSigners.get(keyId));
            return signedJWT.serialize();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String createResetPasswordJwt(UUID userId) {
        try {
            Instant now = Instant.now();
            String keyId = JWTKeyID.PASSWORD_RESET;
            AuthToken.Type type = AuthToken.Type.RESET_PASSWORD;

            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder();
            claimsBuilder.issueTime(DateUtils.toDate(now));
            claimsBuilder.notBeforeTime(DateUtils.toDate(now));
            claimsBuilder.expirationTime(DateUtils.toDate(now.plusSeconds(jwtLifetimes.get(keyId).seconds())));
            claimsBuilder.subject(userId.toString());
            claimsBuilder.claim(JWTPayloadKey.TYPE, type);
            JWTClaimsSet claimsSet = claimsBuilder.build();

            SignedJWT signedJWT = new SignedJWT(jwsHeaders.get(keyId), claimsSet);
            signedJWT.sign(jwsSigners.get(keyId));
            return signedJWT.serialize();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String createActivateAccountJwt(UUID userId) {
        try {
            Instant now = Instant.now();
            String keyId = JWTKeyID.ACCOUNT_ACTIVATION;
            AuthToken.Type type = AuthToken.Type.ACTIVATE_ACCOUNT;

            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder();
            claimsBuilder.issueTime(DateUtils.toDate(now));
            claimsBuilder.notBeforeTime(DateUtils.toDate(now));
            claimsBuilder.expirationTime(DateUtils.toDate(now.plusSeconds(jwtLifetimes.get(keyId).seconds())));
            claimsBuilder.subject(userId.toString());
            claimsBuilder.claim(JWTPayloadKey.TYPE, type);
            JWTClaimsSet claimsSet = claimsBuilder.build();

            SignedJWT signedJWT = new SignedJWT(jwsHeaders.get(keyId), claimsSet);
            signedJWT.sign(jwsSigners.get(keyId));
            return signedJWT.serialize();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public JWTPayload verify(String token) {
        try {
            if (StringUtils.isBlank(token)) return null;
            SignedJWT signedJWT = SignedJWT.parse(token);
            if (!signedJWT.verify(jwsVerifiers.get(signedJWT.getHeader().getKeyID()))) return null;
            return objectMapper.readValue(signedJWT.getPayload().toString(), JWTPayload.class);
        } catch (Exception e) {
            log.error("verify {}", e.toString());
            return null;
        }
    }
}
