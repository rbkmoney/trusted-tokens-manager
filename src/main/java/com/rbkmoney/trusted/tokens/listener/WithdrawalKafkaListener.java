package com.rbkmoney.trusted.tokens.listener;

import com.rbkmoney.damsel.fraudbusters.Withdrawal;
import com.rbkmoney.trusted.tokens.service.WitdrawalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.rbkmoney.trusted.tokens.constants.TransactionStatus.SUCCEEDED;

@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawalKafkaListener {

    private final WitdrawalService witdrawalService;


    @KafkaListener(topics = "${kafka.topic.withdrawal.id}",
            containerFactory = "kafkaWithdrawalListenerContainerFactory")
    public void listen(List<Withdrawal> withdrawals, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
                       @Header(KafkaHeaders.OFFSET) Long offset) {
        try {
            log.info("Listen withdrawals size: {} partition: {} offset: {}", withdrawals.size(), partition, offset);
            withdrawals.stream()
                    .filter(withdrawal -> SUCCEEDED.equals(withdrawal.getStatus().name()))
                    .forEach(witdrawalService::processWithdrawal);

        } catch (Exception e) {
            log.warn("Error when withdrawals listen e: ", e);
            throw e;
        }
    }

}
