package org.tuanna.xcloneserver.modules.user.dtos;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserDTO {

    private UUID id;
    private String username;
    private String email;
    private String password;
    private String name;
    private String status;
    private UUID createdBy;
    private UUID updatedBy;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

}
