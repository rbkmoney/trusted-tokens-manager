package com.rbkmoney.trusted.tokens.listener;

import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.damsel.fraudbusters.PaymentStatus;
import com.rbkmoney.trusted.tokens.converter.TransactionToCardTokensPaymentInfoConverter;
import com.rbkmoney.trusted.tokens.model.CardTokensPaymentInfo;
import com.rbkmoney.trusted.tokens.model.Row;
import com.rbkmoney.trusted.tokens.repository.CardTokenRepository;
import com.rbkmoney.trusted.tokens.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaListener {

    private final PaymentService paymentService;
    private final TransactionToCardTokensPaymentInfoConverter transactionToCardTokensPaymentInfoConverter;
    private final CardTokenRepository cardTokenRepository;

    @Value("${kafka.acknowledgment.nack.sleep}")
    private long sleep;

    @KafkaListener(topics = "${kafka.topic.payment.id}",
            containerFactory = "kafkaPaymentListenerContainerFactory")
    public void listen(List<Payment> payments, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
                       @Header(KafkaHeaders.OFFSET) Integer offset, Acknowledgment acknowledgment) {
        int index = 0;
        try {
            log.info(
                    "PaymentEventListener listen result size: {} partition: {} offset: {}",
                    payments.size(),
                    partition,
                    offset
            );
            for (Payment payment : payments) {
                index = payments.indexOf(payment);
                if (PaymentStatus.captured == payment.getStatus()) {
                    CardTokensPaymentInfo cardTokensPaymentInfo =
                            transactionToCardTokensPaymentInfoConverter.convertPaymentToCardToken(payment);
                    Row row = paymentService.addPaymentCardTokenData(cardTokensPaymentInfo);
                    cardTokenRepository.create(row);
                }
            }
            log.debug("PaymentEventListener listen result payments: {}", payments);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.warn("Error when payments listen e: ", e);
            acknowledgment.nack(index, sleep);
            throw e;
        }
    }

}
