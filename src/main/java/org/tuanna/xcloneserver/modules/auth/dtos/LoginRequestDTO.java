package org.tuanna.xcloneserver.modules.auth.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LoginRequestDTO {

    private String username;
    private String password;

}
