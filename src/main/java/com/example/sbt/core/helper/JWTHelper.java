package com.example.sbt.core.helper;

import com.example.sbt.core.constant.ApplicationProperties;
import com.example.sbt.core.dto.JWTPayload;
import com.example.sbt.core.dto.JWTPayloadKey;
import com.example.sbt.shared.util.DateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Component
public class JWTHelper {
    private final ApplicationProperties applicationProperties;
    private final ObjectMapper objectMapper;
    private final JWSHeader jwsHeader;
    private final JWSSigner jwsSigner;
    private final JWSVerifier jwsVerifier;

    public String create(JWTPayload jwtPayload) {
        try {
            Instant now = Instant.now();
            if (jwtPayload == null) {
                jwtPayload = new JWTPayload();
            }
            if (jwtPayload.getIssuedAt() == null) {
                jwtPayload.setIssuedAt(now);
            }
            if (jwtPayload.getNotBefore() == null) {
                jwtPayload.setNotBefore(now);
            }
            if (jwtPayload.getExpiresAt() == null) {
                jwtPayload.setExpiresAt(now.plusSeconds(applicationProperties.getJwtAccessLifetime()));
            }

            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder();
            claimsBuilder.issueTime(DateUtils.toDate(jwtPayload.getIssuedAt()));
            claimsBuilder.notBeforeTime(DateUtils.toDate(jwtPayload.getNotBefore()));
            claimsBuilder.expirationTime(DateUtils.toDate(jwtPayload.getExpiresAt()));
            if (StringUtils.isNotBlank(jwtPayload.getIssuer())) {
                claimsBuilder.issuer(jwtPayload.getIssuer());
            }
            if (StringUtils.isNotBlank(jwtPayload.getSubject())) {
                claimsBuilder.subject(jwtPayload.getSubject());
            }
            if (jwtPayload.getType() != null) {
                claimsBuilder.claim(JWTPayloadKey.TYPE, jwtPayload.getType());
            }
            if (CollectionUtils.isNotEmpty(jwtPayload.getPermissions())) {
                claimsBuilder.claim(JWTPayloadKey.PERMISSIONS, jwtPayload.getPermissions());
            }
            JWTClaimsSet claimsSet = claimsBuilder.build();

            SignedJWT signedJWT = new SignedJWT(jwsHeader, claimsSet);
            signedJWT.sign(jwsSigner);
            return signedJWT.serialize();
        } catch (Exception e) {
            log.error("create ", e);
            return null;
        }
    }

    public JWTPayload verify(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            if (!signedJWT.verify(jwsVerifier)) {
                return null;
            }
            return objectMapper.readValue(signedJWT.getPayload().toString(), JWTPayload.class);
        } catch (Exception e) {
            log.error("verify ", e);
            return null;
        }
    }
}
