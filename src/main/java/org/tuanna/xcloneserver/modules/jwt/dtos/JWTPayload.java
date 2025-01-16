package org.tuanna.xcloneserver.modules.jwt.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.tuanna.xcloneserver.constants.JWTPayloadKey;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JWTPayload {

    @JsonProperty(JWTPayloadKey.ISSUER)
    private String issuer;
    @JsonProperty(JWTPayloadKey.SUBJECT)
    private String subject;
    @JsonProperty(JWTPayloadKey.ISSUED_AT)
    private Instant issuedAt;
    @JsonProperty(JWTPayloadKey.NOT_BEFORE)
    private Instant notBefore;
    @JsonProperty(JWTPayloadKey.EXPIRES_AT)
    private Instant expiresAt;

    @JsonProperty(JWTPayloadKey.TYPE)
    private Integer type;
    @JsonProperty(JWTPayloadKey.TOKEN_ID)
    private String tokenId;

    @JsonProperty(JWTPayloadKey.PERMISSIONS)
    private List<Integer> permissions;

    @JsonIgnore
    private String value;

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        if (issuer != null) result.put(JWTPayloadKey.ISSUER, issuer);
        if (subject != null) result.put(JWTPayloadKey.SUBJECT, subject);
        if (issuedAt != null) result.put(JWTPayloadKey.ISSUED_AT, issuedAt);
        if (notBefore != null) result.put(JWTPayloadKey.NOT_BEFORE, notBefore);
        if (expiresAt != null) result.put(JWTPayloadKey.EXPIRES_AT, expiresAt);
        if (type != null) result.put(JWTPayloadKey.TYPE, type);
        if (tokenId != null) result.put(JWTPayloadKey.TOKEN_ID, tokenId);
        if (permissions != null) result.put(JWTPayloadKey.PERMISSIONS, permissions);
        return result;
    }

}
