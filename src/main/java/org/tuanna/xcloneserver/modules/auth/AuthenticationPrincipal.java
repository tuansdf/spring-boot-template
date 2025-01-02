package org.tuanna.xcloneserver.modules.auth;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class AuthenticationPrincipal {

    private String userId;
    private String tokenId;
    private List<String> permissions;

}
