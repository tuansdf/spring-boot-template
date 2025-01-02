package org.tuanna.xcloneserver.modules.jwt.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import lombok.*;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
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
public class JWTPayload implements Serializable {

    @JsonProperty("iss")
    private String issuer;
    @JsonProperty("sub")
    private String subject;
    @JsonProperty("iat")
    private Instant issuedAt;
    @JsonProperty("nbf")
    private Instant notBefore;
    @JsonProperty("exp")
    private Instant expiresAt;

    @JsonProperty("typ")
    private String type;
    @JsonProperty("tid")
    private String tokenId;

    @JsonProperty("sid")
    private String subjectId;
    @JsonProperty("per")
    private List<String> permissions;

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        if (!Strings.isNullOrEmpty(issuer)) result.put("iss", issuer);
        if (!Strings.isNullOrEmpty(subject)) result.put("sub", subject);
        if (issuedAt != null) result.put("iat", issuedAt);
        if (notBefore != null) result.put("nbf", notBefore);
        if (expiresAt != null) result.put("exp", expiresAt);
        if (!Strings.isNullOrEmpty(type)) result.put("typ", type);
        if (!Strings.isNullOrEmpty(tokenId)) result.put("tid", tokenId);
        if (!Strings.isNullOrEmpty(subjectId)) result.put("sid", subjectId);
        if (!CollectionUtils.isEmpty(permissions)) result.put("per", permissions);
        return result;
    }

}
