package org.tuanna.xcloneserver.dtos;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class JwtPayload {
    private String issuer;
    private String subject;
    private List<String> audience;
    private Instant expiresAt;
    private Instant notBefore;
    private Instant issuedAt;
    private Map<String, Object> claims;
    private Map<String, Object> user;
}
