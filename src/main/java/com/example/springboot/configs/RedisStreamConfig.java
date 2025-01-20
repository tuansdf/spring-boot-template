package com.example.springboot.configs;

import com.example.springboot.constants.RedisKey;
import com.example.springboot.modules.email.dtos.SendEmailStreamRequest;
import com.example.springboot.stream.SendEmailStreamListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.time.Duration;

@RequiredArgsConstructor
@Configuration
public class RedisStreamConfig {

    private final SendEmailStreamListener sendEmailStreamListener;

    @Bean
    public Subscription subscription(RedisConnectionFactory redisConnectionFactory) {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setVirtualThreads(true);
        executor.setConcurrencyLimit(10);
        executor.setThreadNamePrefix(RedisKey.SEND_EMAIL_STREAM.concat("-"));

        var options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                .pollTimeout(Duration.ofSeconds(1))
                .targetType(SendEmailStreamRequest.class)
                .batchSize(10)
                .executor(executor)
                .build();

        var container = StreamMessageListenerContainer.create(redisConnectionFactory, options);

        var subscription = container.receive(StreamOffset.create(RedisKey.SEND_EMAIL_STREAM, ReadOffset.lastConsumed()), sendEmailStreamListener);

        container.start();
        return subscription;
    }

}
