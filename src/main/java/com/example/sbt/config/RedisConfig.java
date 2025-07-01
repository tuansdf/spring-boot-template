package com.example.sbt.config;

import com.example.sbt.core.constant.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@RequiredArgsConstructor
@Configuration
@EnableRedisRepositories(
        basePackages = "com.example.sbt.module",
        includeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*KVRepository")
)

public class RedisConfig {

    private final ApplicationProperties applicationProperties;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(applicationProperties.getRedisHost());
        configuration.setPort(applicationProperties.getRedisPort());
        if (StringUtils.isNotBlank(applicationProperties.getRedisUsername())) {
            configuration.setUsername(applicationProperties.getRedisUsername());
        }
        if (StringUtils.isNotBlank(applicationProperties.getRedisPassword())) {
            configuration.setPassword(applicationProperties.getRedisPassword());
        }
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

}
