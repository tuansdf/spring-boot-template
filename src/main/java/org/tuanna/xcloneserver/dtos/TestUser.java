package org.tuanna.xcloneserver.dtos;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class TestUser {

    private UUID id;
    private String username;
    private String email;
    private String name;
    private String address;
    private String street;
    private String city;
    private String country;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

}
