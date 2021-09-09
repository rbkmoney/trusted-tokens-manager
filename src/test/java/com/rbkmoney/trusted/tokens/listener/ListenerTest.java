package com.rbkmoney.trusted.tokens.listener;

import com.rbkmoney.damsel.fraudbusters.PaymentStatus;
import com.rbkmoney.damsel.fraudbusters.WithdrawalStatus;
import com.rbkmoney.trusted.tokens.TrustedTokensApplication;
import com.rbkmoney.trusted.tokens.converter.TransactionToCardTokenConverter;
import com.rbkmoney.trusted.tokens.repository.CardTokenRepository;
import com.rbkmoney.trusted.tokens.service.PaymentService;
import com.rbkmoney.trusted.tokens.service.WithdrawalService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.Acknowledgment;

import java.util.Collections;

import static com.rbkmoney.trusted.tokens.utils.TransactionUtils.createPayment;
import static com.rbkmoney.trusted.tokens.utils.TransactionUtils.createWithdrawal;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = TrustedTokensApplication.class)
class ListenerTest {

    @Mock
    CardTokenRepository cardTokenRepository;

    @Mock
    PaymentService paymentService;

    @Mock
    WithdrawalService withdrawalService;
    @Autowired
    TransactionToCardTokenConverter transactionToCardTokenConverter;
    PaymentKafkaListener paymentKafkaListener;
    WithdrawalKafkaListener withdrawalKafkaListener;
    AutoCloseable mocks;
    @Mock
    private Acknowledgment ack;

    @BeforeEach
    public void init() {
        mocks = MockitoAnnotations.openMocks(this);
        paymentKafkaListener = new PaymentKafkaListener(
                paymentService,
                transactionToCardTokenConverter,
                cardTokenRepository);
        withdrawalKafkaListener = new WithdrawalKafkaListener(
                withdrawalService,
                transactionToCardTokenConverter,
                cardTokenRepository);
    }

    @AfterEach
    public void clean() throws Exception {
        mocks.close();
    }

    @Test
    void listenCapturePayment() {
        paymentKafkaListener.listen(Collections.singletonList(
                createPayment().setStatus(PaymentStatus.captured)), 0, 0, ack);
        Mockito.verify(cardTokenRepository, Mockito.times(1)).create(any());
    }

    @Test
    void listenProcessedPayment() {
        paymentKafkaListener.listen(Collections.singletonList(
                createPayment().setStatus(PaymentStatus.processed)), 0, 0, ack);
        Mockito.verify(cardTokenRepository, Mockito.times(0)).create(any());
    }

    @Test
    void listenSucceededWithdrawal() {
        withdrawalKafkaListener.listen(Collections.singletonList(
                createWithdrawal().setStatus(WithdrawalStatus.succeeded)), 0, 0, ack);
        Mockito.verify(cardTokenRepository, Mockito.times(1)).create(any());
    }

    @Test
    void listenPendingWithdrawal() {
        withdrawalKafkaListener.listen(Collections.singletonList(
                createWithdrawal().setStatus(WithdrawalStatus.pending)), 0, 0, ack);
        Mockito.verify(cardTokenRepository, Mockito.times(0)).create(any());
    }
}
