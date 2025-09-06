package com.example.sbt.module.configuration.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "configuration", timeToLive = 86400)
public class ConfigurationKV {
    @Id
    private String code;
    private String value;
    private Boolean isEnabled;
    private Boolean isPublic;
}
