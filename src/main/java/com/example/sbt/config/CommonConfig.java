package com.example.sbt.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@EnableJpaRepositories(
        basePackages = "com.example.sbt.module",
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*KVRepository")
)
public class CommonConfig {
}
