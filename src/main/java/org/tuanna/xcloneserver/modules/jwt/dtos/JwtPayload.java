package org.tuanna.xcloneserver.modules.jwt.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class JwtPayload implements Serializable {

    @JsonProperty("iss")
    private String issuer;
    @JsonProperty("sub")
    private String subject;
    @JsonProperty("aud")
    private List<String> audience;
    @JsonProperty("exp")
    private Instant expiresAt;
    @JsonProperty("nbf")
    private Instant notBefore;
    @JsonProperty("iat")
    private Instant issuedAt;

    @JsonProperty("sid")
    private String subjectId;
    @JsonProperty("tid")
    private String tokenId;

}
