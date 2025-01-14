package org.tuanna.xcloneserver.modules.jwt.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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
    private Integer type;
    @JsonProperty("tid")
    private String tokenId;

    @JsonProperty("sid")
    private String subjectId;
    @JsonProperty("per")
    private List<Integer> permissions;

    @JsonIgnore
    private String value;

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        if (issuer != null) result.put("iss", issuer);
        if (subject != null) result.put("sub", subject);
        if (issuedAt != null) result.put("iat", issuedAt);
        if (notBefore != null) result.put("nbf", notBefore);
        if (expiresAt != null) result.put("exp", expiresAt);
        if (type != null) result.put("typ", type);
        if (tokenId != null) result.put("tid", tokenId);
        if (subjectId != null) result.put("sid", subjectId);
        if (permissions != null) result.put("per", permissions);
        return result;
    }

}
