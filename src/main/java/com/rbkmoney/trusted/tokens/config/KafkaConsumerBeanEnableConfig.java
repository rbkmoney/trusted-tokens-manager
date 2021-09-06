package com.rbkmoney.trusted.tokens.config;

import com.rbkmoney.trusted.tokens.listener.PaymentKafkaListener;
import com.rbkmoney.trusted.tokens.listener.WithdrawalKafkaListener;
import com.rbkmoney.trusted.tokens.service.PaymentService;
import com.rbkmoney.trusted.tokens.service.WithdrawalService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
public class KafkaConsumerBeanEnableConfig {

    @Bean
    @ConditionalOnProperty(value = "kafka.topics.payment.enabled", havingValue = "true")
    public PaymentKafkaListener paymentEventsKafkaListener(PaymentService paymentService) {
        return new PaymentKafkaListener(paymentService);
    }

    @Bean
    @ConditionalOnProperty(value = "kafka.topics.withdrawal.enabled", havingValue = "true")
    public WithdrawalKafkaListener withdrawalKafkaListener(WithdrawalService withdrawalService) {
        return new WithdrawalKafkaListener(withdrawalService);
    }

}
