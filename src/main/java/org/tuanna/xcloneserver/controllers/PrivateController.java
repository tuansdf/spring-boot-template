package org.tuanna.xcloneserver.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tuanna.xcloneserver.constants.PermissionCode;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/private")
public class PrivateController {

    @GetMapping("/health")
    @Secured({PermissionCode.ADMIN})
    public String check() {
        return "OK";
    }

}
