package com.example.sbt.common.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/testing")
public class PublicController {

    @GetMapping(value = "/health", produces = MediaType.TEXT_PLAIN_VALUE)
    public String check() {
        return "OK";
    }

}
