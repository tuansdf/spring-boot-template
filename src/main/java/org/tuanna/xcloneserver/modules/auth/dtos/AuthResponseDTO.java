package org.tuanna.xcloneserver.modules.auth.dtos;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class AuthResponseDTO {

    private String accessToken;
    private String refreshToken;
    private UUID userId;
    private String username;
    private String email;
    private String name;

}
