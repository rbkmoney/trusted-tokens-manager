package com.rbkmoney.trusted.tokens.config;

import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.damsel.fraudbusters.Withdrawal;
import com.rbkmoney.trusted.tokens.config.properties.KafkaSslProperties;
import com.rbkmoney.trusted.tokens.serde.PaymentDeserializer;
import com.rbkmoney.trusted.tokens.serde.WithdrawalDeserializer;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(KafkaSslProperties.class)
public class KafkaConfig {

    @Value("${retry-policy.maxAttempts}")
    int maxAttempts;
    @Value("${kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;
    @Value("${kafka.consumer.enable-auto-commit}")
    private boolean enableAutoCommit;
    @Value("${kafka.consumer.group-id}")
    private String groupId;
    @Value("${kafka.client.id}")
    private String clientId;
    @Value("${kafka.consumer.max-poll-records}")
    private int maxPollRecords;
    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;
    @Value("${kafka.topic.payment.concurrency}")
    private int paymentConcurrency;
    @Value("${kafka.topic.withdrawal.concurrency}")
    private int withdrawalConcurrency;

    public Map<String, Object> createDefaultProperties(KafkaSslProperties kafkaSslProperties) {
        final Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        configureSsl(props, kafkaSslProperties);
        return props;
    }

    private void configureSsl(Map<String, Object> props, KafkaSslProperties kafkaSslProperties) {
        if (kafkaSslProperties.isEnabled()) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SSL.name());
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,
                    new File(kafkaSslProperties.getTrustStoreLocation()).getAbsolutePath());
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, kafkaSslProperties.getTrustStorePassword());
            props.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, kafkaSslProperties.getKeyStoreType());
            props.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, kafkaSslProperties.getTrustStoreType());
            props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG,
                    new File(kafkaSslProperties.getKeyStoreLocation()).getAbsolutePath());
            props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, kafkaSslProperties.getKeyStorePassword());
            props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, kafkaSslProperties.getKeyPassword());
        }
    }

    public <T> ConcurrentKafkaListenerContainerFactory<String, T> createFactory(
            Deserializer<T> deserializer,
            Map<String, Object> props,
            int concurrency) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        DefaultKafkaConsumerFactory<String, T> consumerFactory = new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(concurrency);
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Payment> kafkaPaymentListenerContainerFactory(
            KafkaSslProperties kafkaSslProperties) {
        Map<String, Object> prop = createDefaultProperties(kafkaSslProperties);
        prop.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId + "-payment");
        return createFactory(new PaymentDeserializer(), prop, paymentConcurrency);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Withdrawal> kafkaWithdrawalListenerContainerFactory(
            KafkaSslProperties kafkaSslProperties) {
        Map<String, Object> prop = createDefaultProperties(kafkaSslProperties);
        prop.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId + "-withdrawal");
        return createFactory(new WithdrawalDeserializer(), prop, withdrawalConcurrency);
    }

}
