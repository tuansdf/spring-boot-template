package org.tuanna.xcloneserver.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tuanna.xcloneserver.utils.JSONUtils;

@Configuration
public class JSONConfig {

    @Bean
    public ObjectMapper getObjectMapper() {
        return JSONUtils.getObjectMapper();
    }

}
