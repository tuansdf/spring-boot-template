package org.tuanna.xcloneserver.modules.user.dtos;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ChangePasswordRequestDTO {

    private UUID userId;
    private String oldPassword;
    private String newPassword;

}
