package com.example.sbt.config;

import com.example.sbt.common.constant.EventKey;
import com.example.sbt.common.util.CommonUtils;
import com.example.sbt.event.dto.SendEmailEventRequest;
import com.example.sbt.event.dto.SendNotificationEventRequest;
import com.example.sbt.event.listener.SendEmailEventListener;
import com.example.sbt.event.listener.SendNotificationEventListener;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class RedisStreamConfig {

    private final StringRedisTemplate redisTemplate;
    private final SendEmailEventListener sendEmailEventListener;
    private final SendNotificationEventListener sendNotificationEventListener;
    private final RedisConnectionFactory connectionFactory;

    @PostConstruct
    public void setup() {
        String hostname = CommonUtils.coalesce(System.getenv("HOSTNAME"), System.getenv("COMPUTERNAME"));
        String consumerName = "consumer-" + hostname;

        List<CreateSubscriptionRequest<?>> requests = new ArrayList<>();
        requests.add(new CreateSubscriptionRequest<>(sendEmailEventListener, SendEmailEventRequest.class, EventKey.SEND_EMAIL.concat("_group"), EventKey.SEND_EMAIL));
        requests.add(new CreateSubscriptionRequest<>(sendNotificationEventListener, SendNotificationEventRequest.class, EventKey.SEND_NOTIFICATION.concat("_group"), EventKey.SEND_NOTIFICATION));
        List<Subscription> subscriptions = new ArrayList<>();

        for (CreateSubscriptionRequest<?> request : requests) {
            try {
                redisTemplate.opsForStream().createGroup(request.streamName(), ReadOffset.from("0"), request.groupName());
            } catch (Exception e) {
                log.info("Group {} already exists", request.groupName());
            }

            subscriptions.add(createSubscription(request, connectionFactory, consumerName));
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            subscriptions.forEach(Subscription::cancel);
            log.info("Redis stream consumers stopped");
        }));
    }

    private <T> Subscription createSubscription(CreateSubscriptionRequest<T> request, RedisConnectionFactory connectionFactory, String consumerName) {
        var executor = new SimpleAsyncTaskExecutor();
        executor.setVirtualThreads(true);
        executor.setConcurrencyLimit(10);
        executor.setThreadNamePrefix(request.streamName().concat("-"));

        var options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                .pollTimeout(Duration.ofSeconds(5))
                .targetType(request.request())
                .batchSize(10)
                .executor(executor)
                .build();

        var container = StreamMessageListenerContainer.create(connectionFactory, options);

        var subscription = container.receiveAutoAck(Consumer.from(request.groupName(), consumerName),
                StreamOffset.create(request.streamName(), ReadOffset.lastConsumed()), request.listener());

        container.start();
        return subscription;
    }

    private record CreateSubscriptionRequest<T>(
            StreamListener<String, ObjectRecord<String, T>> listener,
            Class<T> request,
            String groupName,
            String streamName
    ) {
    }

}
