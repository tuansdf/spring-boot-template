package com.example.springboot.configs;

import com.example.springboot.constants.Env;
import com.example.springboot.constants.StreamKey;
import com.example.springboot.modules.email.dtos.ExecuteSendEmailStreamRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.time.Duration;

@RequiredArgsConstructor
@Configuration
public class RedisConfig {

    private final Env env;

    private final StreamListener<String, ObjectRecord<String, ExecuteSendEmailStreamRequest>> executeSendEmailListener;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(env.getRedisHost(), env.getRedisPort()));
    }

    @Bean
    public Subscription subscription(RedisConnectionFactory redisConnectionFactory) {
        var containerOptions = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder().pollTimeout(Duration.ofMillis(100)).targetType(ExecuteSendEmailStreamRequest.class).build();

        var container = StreamMessageListenerContainer.create(redisConnectionFactory,
                containerOptions);

        var subscription = container.receive(StreamOffset.create(StreamKey.EXECUTE_SEND_EMAIL_STREAM, ReadOffset.lastConsumed()), executeSendEmailListener);
        container.start();
        return subscription;
    }

}
