package com.example.matcher.userservice.configuration.kafka;

import com.example.matcher.userservice.exception.ResourceNotFoundException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

//    @Value("${spring.kafka.dlq.topic}")
    private final String dlqTopic = "delete_profile.DLT";
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerConfiguration.class);

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, StringDeserializer.class);
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);           // Фиксировать сдвиг только после успешной обработки сообщения
        configProps.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");   // Чтение только подтверждённых сообщений
        configProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);   // Как часто потребитель подтверждает свою активность для kafka
        configProps.put(ConsumerConfig. MAX_POLL_INTERVAL_MS_CONFIG, 40000);  // Максимальное время которое потребитель может не отправлять poll запрос
        configProps.put(ConsumerConfig. SESSION_TIMEOUT_MS_CONFIG, 45000);  // Максимальное время молчания после которого потребитель считается мертвым
        configProps.put("reconnect.backoff.ms", 1000); // Время ожидания перед переподключением
        configProps.put("reconnect.backoff.max.ms", 10000); // Максимальное время между попытками
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                deadLetterPublishingRecoverer(kafkaTemplate()),
                new FixedBackOff(1000L, 3) // Задержка 1 секунда, максимум 3 попытки
        );
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class, NullPointerException.class);
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            if (deliveryAttempt > 0) {
                logger.warn("Retry attempt {} for record {} with exception {}",
                        deliveryAttempt, record.value(), ex.getMessage());
            }
        });
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<String, Object> kafkaTemplate) {
        return new DeadLetterPublishingRecoverer(kafkaTemplate, (record, exception) -> {
            logger.error("Critical error occurred. Sending to DLQ: {}", record.value(), exception);
            return new org.apache.kafka.common.TopicPartition(dlqTopic, record.partition());
        });
    }
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
}

