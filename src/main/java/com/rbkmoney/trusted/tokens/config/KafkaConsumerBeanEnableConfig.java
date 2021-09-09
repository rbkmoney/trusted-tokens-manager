package com.rbkmoney.trusted.tokens.config;

import com.rbkmoney.trusted.tokens.converter.TransactionToCardTokenConverter;
import com.rbkmoney.trusted.tokens.listener.PaymentKafkaListener;
import com.rbkmoney.trusted.tokens.listener.WithdrawalKafkaListener;
import com.rbkmoney.trusted.tokens.repository.CardTokenRepository;
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
    public PaymentKafkaListener paymentEventsKafkaListener(
            PaymentService paymentService,
            TransactionToCardTokenConverter transactionToCardTokenConverter,
            CardTokenRepository cardTokenRepository) {
        return new PaymentKafkaListener(paymentService, transactionToCardTokenConverter, cardTokenRepository);
    }

    @Bean
    @ConditionalOnProperty(value = "kafka.topics.withdrawal.enabled", havingValue = "true")
    public WithdrawalKafkaListener withdrawalKafkaListener(
            WithdrawalService withdrawalService,
            TransactionToCardTokenConverter transactionToCardTokenConverter,
            CardTokenRepository cardTokenRepository) {
        return new WithdrawalKafkaListener(withdrawalService, transactionToCardTokenConverter, cardTokenRepository);
    }

}
