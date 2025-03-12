package com.example.demo.config;

import com.example.demo.common.util.JSONUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JSONConfig {

    @Bean
    public ObjectMapper getObjectMapper() {
        return JSONUtils.getObjectMapper();
    }

}
