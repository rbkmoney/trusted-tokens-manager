package com.rbkmoney.trusted.tokens.listener;

import com.rbkmoney.damsel.fraudbusters.Withdrawal;
import com.rbkmoney.damsel.fraudbusters.WithdrawalStatus;
import com.rbkmoney.trusted.tokens.converter.TransactionToCardTokenConverter;
import com.rbkmoney.trusted.tokens.repository.CardTokenRepository;
import com.rbkmoney.trusted.tokens.service.WithdrawalService;
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
public class WithdrawalKafkaListener {

    private final WithdrawalService withdrawalService;
    private final TransactionToCardTokenConverter transactionToCardTokenConverter;
    private final CardTokenRepository cardTokenRepository;

    @Value("${kafka.acknowledgment.nack.sleep}")
    private long sleep;

    @KafkaListener(topics = "${kafka.topic.withdrawal.id}",
            containerFactory = "kafkaWithdrawalListenerContainerFactory")
    public void listen(List<Withdrawal> withdrawals, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
                       @Header(KafkaHeaders.OFFSET) Long offset, Acknowledgment acknowledgment) {
        try {
            log.info("Listen withdrawals size: {} partition: {} offset: {}", withdrawals.size(), partition, offset);
            withdrawals.stream()
                    .filter(withdrawal -> WithdrawalStatus.succeeded == withdrawal.getStatus())
                    .map(transactionToCardTokenConverter::convertWithdrawalToCardToken)
                    .map(withdrawalService::addWithdrawalCardTokenData)
                    .forEach(cardTokenRepository::create);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.warn("Error when withdrawals listen e: ", e);
            acknowledgment.nack(sleep);
            throw e;
        }
    }

}
