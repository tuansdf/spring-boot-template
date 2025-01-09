package org.tuanna.xcloneserver.modules.authentication.dtos;

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
