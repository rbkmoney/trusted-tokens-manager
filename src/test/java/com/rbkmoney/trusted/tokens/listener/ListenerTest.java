package com.rbkmoney.trusted.tokens.listener;

import com.rbkmoney.damsel.fraudbusters.PaymentStatus;
import com.rbkmoney.damsel.fraudbusters.WithdrawalStatus;
import com.rbkmoney.trusted.tokens.TrustedTokensApplication;
import com.rbkmoney.trusted.tokens.converter.RowConverter;
import com.rbkmoney.trusted.tokens.converter.TransactionToCardTokensPaymentInfoConverter;
import com.rbkmoney.trusted.tokens.exception.RiakExecutionException;
import com.rbkmoney.trusted.tokens.repository.CardTokenRepository;
import com.rbkmoney.trusted.tokens.service.PaymentService;
import com.rbkmoney.trusted.tokens.service.WithdrawalService;
import com.rbkmoney.trusted.tokens.updater.CardTokenDataUpdater;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.support.Acknowledgment;

import java.util.Collections;

import static com.rbkmoney.trusted.tokens.utils.TransactionUtils.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest(classes = TrustedTokensApplication.class)
class ListenerTest {

    @MockBean
    CardTokenRepository cardTokenRepository;

    @Autowired
    PaymentService paymentService;

    @Autowired
    WithdrawalService withdrawalService;

    @Autowired
    TransactionToCardTokensPaymentInfoConverter transactionToCardTokensPaymentInfoConverter;

    @Autowired
    PaymentKafkaListener paymentKafkaListener;

    @Autowired
    WithdrawalKafkaListener withdrawalKafkaListener;

    AutoCloseable mocks;

    @Mock
    private Acknowledgment ack;

    @BeforeEach
    public void init() {
        mocks = MockitoAnnotations.openMocks(this);
        Mockito.when(cardTokenRepository.get(EXCEPTION_TOKEN))
                .thenThrow(RiakExecutionException.class);
    }

    @AfterEach
    public void clean() throws Exception {
        doThrow(new RiakExecutionException()).when(cardTokenRepository)
                .get(EXCEPTION_TOKEN);
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

    @Test
    void listenPaymentsWithException() {
        assertThrows(RiakExecutionException.class,
                () -> paymentKafkaListener.listen(createPaymentList(), 0, 0, ack));
        Mockito.verify(ack, Mockito.times(0)).acknowledge();
        Mockito.verify(ack, Mockito.times(1)).nack(1, 500);
    }
}
