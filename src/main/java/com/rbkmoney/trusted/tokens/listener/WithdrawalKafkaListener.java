package com.rbkmoney.trusted.tokens.listener;

import com.rbkmoney.damsel.fraudbusters.Withdrawal;
import com.rbkmoney.damsel.fraudbusters.WithdrawalStatus;
import com.rbkmoney.trusted.tokens.converter.TransactionToCardTokensPaymentInfoConverter;
import com.rbkmoney.trusted.tokens.model.CardTokensPaymentInfo;
import com.rbkmoney.trusted.tokens.model.Row;
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
    private final TransactionToCardTokensPaymentInfoConverter transactionToCardTokensPaymentInfoConverter;
    private final CardTokenRepository cardTokenRepository;

    @Value("${kafka.acknowledgment.nack.sleep}")
    private long sleep;

    @KafkaListener(topics = "${kafka.topic.withdrawal.id}",
            containerFactory = "kafkaWithdrawalListenerContainerFactory")
    public void listen(List<Withdrawal> withdrawals, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
                       @Header(KafkaHeaders.OFFSET) Integer offset, Acknowledgment acknowledgment) {
        int index = 0;
        try {
            log.info("Listen withdrawals size: {} partition: {} offset: {}", withdrawals.size(), partition, offset);
            for (Withdrawal withdrawal : withdrawals) {
                index = withdrawals.indexOf(withdrawal);
                if (WithdrawalStatus.succeeded == withdrawal.getStatus()) {
                    CardTokensPaymentInfo cardTokensPaymentInfo =
                            transactionToCardTokensPaymentInfoConverter.convertWithdrawalToCardToken(withdrawal);
                    Row row = withdrawalService.addWithdrawalCardTokenData(cardTokensPaymentInfo);
                    cardTokenRepository.create(row);
                }
            }
        } catch (Exception e) {
            log.warn("Error when withdrawals listen e: ", e);
            acknowledgment.nack(index, sleep);
            throw e;
        }
    }

}
