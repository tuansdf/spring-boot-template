package com.example.sbt.common.dto;

import com.example.sbt.module.authtoken.entity.AuthToken;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

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
    private AuthToken.Type type;
    @JsonProperty(JWTPayloadKey.SCOPE)
    private List<String> permissions;

    @JsonIgnore
    private String value;

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public void setPermissions(String permissions) {
        if (StringUtils.isBlank(permissions)) return;
        this.permissions = Arrays.asList(permissions.split(" "));
    }
}
