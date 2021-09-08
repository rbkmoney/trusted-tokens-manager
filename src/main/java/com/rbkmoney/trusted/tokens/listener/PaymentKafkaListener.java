package com.rbkmoney.trusted.tokens.listener;

import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.damsel.fraudbusters.PaymentStatus;
import com.rbkmoney.trusted.tokens.converter.TransactionToCardTokenConverter;
import com.rbkmoney.trusted.tokens.repository.TrustedTokenRepository;
import com.rbkmoney.trusted.tokens.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaListener {

    private final PaymentService paymentService;
    private final TransactionToCardTokenConverter transactionToCardTokenConverter;
    private final TrustedTokenRepository trustedTokenRepository;

    @KafkaListener(topics = "${kafka.topic.payment.id}",
            containerFactory = "kafkaPaymentListenerContainerFactory")
    public void listen(List<Payment> payments, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
                       @Header(KafkaHeaders.OFFSET) Long offset) {
        try {
            log.info(
                    "PaymentEventListener listen result size: {} partition: {} offset: {}",
                    payments.size(),
                    partition,
                    offset
            );
            log.debug("PaymentEventListener listen result payments: {}", payments);
            payments.stream()
                    .filter(payment -> PaymentStatus.captured.name().equals(payment.getStatus().name()))
                    .map(transactionToCardTokenConverter::convertPaymentToCardToken)
                    .map(paymentService::updatePaymentCardTokenData)
                    .forEach(trustedTokenRepository::create);
        } catch (Exception e) {
            log.warn("Error when payments listen e: ", e);
            throw e;
        }
    }

}
