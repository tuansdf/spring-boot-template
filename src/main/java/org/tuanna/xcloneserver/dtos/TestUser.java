package org.tuanna.xcloneserver.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class TestUser {

    private Integer id;
    private String user;
    private String email;
    private String name;

}
