package org.tuanna.xcloneserver.modules.jwt.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

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

    @JsonProperty("sid")
    private String subjectId;
    @JsonProperty("per")
    private List<String> permissions;
    @JsonProperty("tid")
    private String tokenId;
    @JsonProperty("typ")
    private String type;

}
