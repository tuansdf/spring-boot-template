package com.example.demo.configs;

import com.example.demo.utils.JSONUtils;
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
