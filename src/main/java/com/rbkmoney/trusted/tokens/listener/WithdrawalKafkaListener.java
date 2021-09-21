package com.rbkmoney.trusted.tokens.listener;

import com.rbkmoney.damsel.fraudbusters.Withdrawal;
import com.rbkmoney.damsel.fraudbusters.WithdrawalStatus;
import com.rbkmoney.kafka.common.util.LogUtil;
import com.rbkmoney.trusted.tokens.converter.TransactionToCardTokensPaymentInfoConverter;
import com.rbkmoney.trusted.tokens.model.Row;
import com.rbkmoney.trusted.tokens.repository.CardTokenRepository;
import com.rbkmoney.trusted.tokens.service.WithdrawalService;
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
public class WithdrawalKafkaListener {

    private final WithdrawalService withdrawalService;
    private final TransactionToCardTokensPaymentInfoConverter transactionToCardTokensPaymentInfoConverter;
    private final CardTokenRepository cardTokenRepository;

    @Value("${kafka.consumer.throttling-timeout-ms}")
    private int throttlingTimeout;

    @KafkaListener(
            autoStartup = "${kafka.topics.withdrawal.consume.enabled}",
            topics = "${kafka.topics.withdrawal.id}",
            containerFactory = "withdrawalListenerContainerFactory")
    public void listen(
            List<ConsumerRecord<String, Withdrawal>> batch,
            Acknowledgment ack) throws InterruptedException {
        log.info("WithdrawalKafkaListener listen offsets, size={}, {}",
                batch.size(), toSummaryWithdrawalString(batch));
        List<Withdrawal> withdrawals = batch.stream()
                .map(ConsumerRecord::value)
                .collect(Collectors.toList());
        try {
            handleMessages(withdrawals);
        } catch (Exception e) {
            log.error("Error when WithdrawalKafkaListener listen e: ", e);
            Thread.sleep(throttlingTimeout);
            throw e;
        }
        ack.acknowledge();
        log.info("WithdrawalKafkaListener Records have been committed, size={}, {}",
                batch.size(), toSummaryWithdrawalString(batch));
    }

    private void handleMessages(List<Withdrawal> withdrawals) {
        for (Withdrawal withdrawal : withdrawals) {
            if (WithdrawalStatus.succeeded == withdrawal.getStatus()
                    && withdrawal.getDestinationResource().isSetBankCard()) {
                var info = transactionToCardTokensPaymentInfoConverter.convertWithdrawalToCardToken(withdrawal);
                Row row = withdrawalService.addWithdrawalCardTokenData(info);
                cardTokenRepository.create(row);
            }
        }
    }

    public static <K> String toSummaryWithdrawalString(List<ConsumerRecord<K, Withdrawal>> records) {
        String valueKeysString = records.stream().map(ConsumerRecord::value)
                .map((value) -> String.format("'%s'", value.getId()))
                .collect(Collectors.joining(", "));
        return String.format("%s, values={%s}", LogUtil.toSummaryString(records), valueKeysString);
    }
}
