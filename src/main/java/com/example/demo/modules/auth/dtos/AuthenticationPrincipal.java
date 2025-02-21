package com.example.demo.modules.auth.dtos;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class AuthenticationPrincipal {

    private UUID userId;
    private List<String> permissions;

}
