package com.example.sbt.infrastructure.persistence;

import com.example.sbt.common.constant.EventKey;
import com.example.sbt.features.email.event.SendEmailEventListener;
import com.example.sbt.features.email.event.SendEmailEventRequest;
import com.example.sbt.features.notification.event.SendNotificationEventListener;
import com.example.sbt.features.notification.event.SendNotificationEventRequest;
import com.example.sbt.features.user.event.ExportUserEventListener;
import com.example.sbt.features.user.event.ExportUserEventRequest;
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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class RedisStreamConfig {
    private final RedisConnectionFactory connectionFactory;
    private final StringRedisTemplate redisTemplate;
    private final SendEmailEventListener sendEmailEventListener;
    private final SendNotificationEventListener sendNotificationEventListener;
    private final ExportUserEventListener exportUserEventListener;

    @PostConstruct
    public void setup() throws UnknownHostException {
        String hostname = InetAddress.getLocalHost().getHostName();
        String consumerName = "consumer-" + hostname;

        List<CreateSubscriptionRequest<?>> requests = new ArrayList<>();
        requests.add(new CreateSubscriptionRequest<>(sendEmailEventListener, SendEmailEventRequest.class, EventKey.SEND_EMAIL + "_group", EventKey.SEND_EMAIL));
        requests.add(new CreateSubscriptionRequest<>(sendNotificationEventListener, SendNotificationEventRequest.class, EventKey.SEND_NOTIFICATION + "_group", EventKey.SEND_NOTIFICATION));
        requests.add(new CreateSubscriptionRequest<>(exportUserEventListener, ExportUserEventRequest.class, EventKey.EXPORT_USER + "_group", EventKey.EXPORT_USER));
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
        executor.setThreadNamePrefix(request.streamName() + "-");

        var options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                .pollTimeout(Duration.ofSeconds(5))
                .targetType(request.requestClass())
                .batchSize(10)
                .executor(executor)
                .build();

        var container = StreamMessageListenerContainer.create(connectionFactory, options);

        var subscription = container.receiveAutoAck(Consumer.from(request.groupName(), consumerName),
                StreamOffset.create(request.streamName(), ReadOffset.lastConsumed()), request.streamListener());

        container.start();
        return subscription;
    }

    private record CreateSubscriptionRequest<T>(
            StreamListener<String, ObjectRecord<String, T>> streamListener,
            Class<T> requestClass,
            String groupName,
            String streamName
    ) {
    }
}
