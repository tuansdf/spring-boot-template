package org.tuanna.xcloneserver.modules.authentication.dtos;

import lombok.*;

import java.util.List;
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
    private List<String> permissions;

}
