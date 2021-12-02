package com.rbkmoney.trusted.tokens.listener;

import com.rbkmoney.damsel.fraudbusters.PaymentStatus;
import com.rbkmoney.damsel.fraudbusters.WithdrawalStatus;
import com.rbkmoney.testcontainers.annotations.KafkaSpringBootTest;
import com.rbkmoney.testcontainers.annotations.kafka.KafkaTestcontainer;
import com.rbkmoney.testcontainers.annotations.kafka.config.KafkaProducer;
import com.rbkmoney.trusted.tokens.config.MockedStartupInitializers;
import com.rbkmoney.trusted.tokens.exception.RiakExecutionException;
import com.rbkmoney.trusted.tokens.repository.CardTokenRepository;
import org.apache.thrift.TBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import static com.rbkmoney.trusted.tokens.utils.TransactionUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@KafkaTestcontainer(
        properties = {
                "kafka.topics.payment.consume.enabled=true",
                "kafka.topics.withdrawal.consume.enabled=true",
                "kafka.state.cache.size=0"},
        topicsKeys = {
                "kafka.topics.payment.id",
                "kafka.topics.withdrawal.id",
                "kafka.topics.payment.dest",
                "kafka.topics.withdrawal.dest"})
@KafkaSpringBootTest
@Import(MockedStartupInitializers.class)
public class ListenerTest {

    @Value("${kafka.topics.payment.id}")
    private String paymentTopicName;
    @Value("${kafka.topics.withdrawal.id}")
    private String withdrawalTopicName;
    @Autowired
    private KafkaProducer<TBase<?, ?>> testThriftKafkaProducer;
    @MockBean
    private CardTokenRepository cardTokenRepository;

    @BeforeEach
    public void init() {
        doThrow(new RiakExecutionException()).when(cardTokenRepository)
                .get(EXCEPTION_TOKEN);
    }

    @Test
    public void listenCapturePayment() {
        testThriftKafkaProducer.send(paymentTopicName, createPayment().setStatus(PaymentStatus.captured));
        testThriftKafkaProducer.send(paymentTopicName, createPayment().setStatus(PaymentStatus.processed));
        verify(cardTokenRepository, timeout(5000).times(1)).create(any());
    }

    @Test
    void listenSucceededWithdrawal() {
        testThriftKafkaProducer.send(withdrawalTopicName, createWithdrawal().setStatus(WithdrawalStatus.succeeded));
        testThriftKafkaProducer.send(withdrawalTopicName, createWithdrawal().setStatus(WithdrawalStatus.pending));
        verify(cardTokenRepository, timeout(5000).times(1)).create(any());
    }
}
