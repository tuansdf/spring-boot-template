package com.example.sbt.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWTPayload {
    @JsonProperty(JWTPayloadKey.ISSUED_AT)
    private Instant issuedAt;
    @JsonProperty(JWTPayloadKey.NOT_BEFORE)
    private Instant notBefore;
    @JsonProperty(JWTPayloadKey.EXPIRES_AT)
    private Instant expiresAt;
    @JsonProperty(JWTPayloadKey.SUBJECT)
    private String subject;
    @JsonProperty(JWTPayloadKey.ISSUER)
    private String issuer;

    @JsonProperty(JWTPayloadKey.TYPE)
    private Integer type;
    @JsonProperty(JWTPayloadKey.PERMISSIONS)
    private List<Integer> permissions;

    @JsonIgnore
    private String value;

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        if (issuedAt != null) result.put(JWTPayloadKey.ISSUED_AT, issuedAt);
        if (notBefore != null) result.put(JWTPayloadKey.NOT_BEFORE, notBefore);
        if (expiresAt != null) result.put(JWTPayloadKey.EXPIRES_AT, expiresAt);
        if (subject != null) result.put(JWTPayloadKey.SUBJECT, subject);
        if (issuer != null) result.put(JWTPayloadKey.ISSUER, issuer);
        if (type != null) result.put(JWTPayloadKey.TYPE, type);
        if (permissions != null) result.put(JWTPayloadKey.PERMISSIONS, permissions);
        return result;
    }
}
