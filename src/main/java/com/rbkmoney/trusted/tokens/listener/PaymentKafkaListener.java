package com.rbkmoney.trusted.tokens.listener;

import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.damsel.fraudbusters.PaymentStatus;
import com.rbkmoney.kafka.common.util.LogUtil;
import com.rbkmoney.trusted.tokens.converter.TransactionToCardTokensPaymentInfoConverter;
import com.rbkmoney.trusted.tokens.model.Row;
import com.rbkmoney.trusted.tokens.repository.CardTokenRepository;
import com.rbkmoney.trusted.tokens.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaListener {

    private final PaymentService paymentService;
    private final TransactionToCardTokensPaymentInfoConverter transactionToCardTokensPaymentInfoConverter;
    private final CardTokenRepository cardTokenRepository;

    @Value("${kafka.consumer.throttling-timeout-ms}")
    private int throttlingTimeout;

    @KafkaListener(
            autoStartup = "${kafka.topics.payment.consume.enabled}",
            topics = "${kafka.topics.payment.id}",
            containerFactory = "paymentListenerContainerFactory")
    public void listen(
            List<ConsumerRecord<String, Payment>> batch,
            Acknowledgment ack) {
        int index = 0;
        try {
            log.info("PaymentKafkaListener listen offsets, size={}, {}",
                    batch.size(), toSummaryPaymentString(batch));
            List<Payment> payments = batch.stream()
                    .map(ConsumerRecord::value)
                    .collect(Collectors.toList());
            for (Payment payment : payments) {
                index = payments.indexOf(payment);
                if (PaymentStatus.captured == payment.getStatus()
                        && payment.getPaymentTool().isSetBankCard()) {
                    var info = transactionToCardTokensPaymentInfoConverter.convertPaymentToCardToken(payment);
                    Row row = paymentService.addPaymentCardTokenData(info);
                    cardTokenRepository.create(row);
                }
            }
            ack.acknowledge();
            log.info("PaymentKafkaListener Records have been committed, size={}, {}",
                    batch.size(), toSummaryPaymentString(batch));
        } catch (Exception ex) {
            log.error("Error when PaymentKafkaListener listen ex,", ex);
            ack.nack(index, throttlingTimeout);
            throw ex;
        }
    }

    public static <K> String toSummaryPaymentString(List<ConsumerRecord<K, Payment>> records) {
        String valueKeysString = records.stream().map(ConsumerRecord::value)
                .map((value) -> String.format("'%s'", value.getId()))
                .collect(Collectors.joining(", "));
        return String.format("%s, values={%s}", LogUtil.toSummaryString(records), valueKeysString);
    }
}
