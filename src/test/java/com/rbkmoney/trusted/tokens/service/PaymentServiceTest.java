package com.rbkmoney.trusted.tokens.service;

import com.rbkmoney.trusted.tokens.config.RiakAbstractTestIntegration;
import com.rbkmoney.trusted.tokens.model.CardTokenData;
import com.rbkmoney.trusted.tokens.repository.TrustedTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static com.rbkmoney.trusted.tokens.utils.TransactionUtils.TOKEN;
import static com.rbkmoney.trusted.tokens.utils.TransactionUtils.createPayment;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentServiceTest extends RiakAbstractTestIntegration {

    @Autowired
    private TrustedTokenRepository trustedTokenRepository;

    @Autowired
    private PaymentService paymentService;

    @Test
    public void processPaymentTest() throws InterruptedException {
        sleep(50000);
        paymentService.processPayment(createPayment());
        CardTokenData cardTokenData =
                trustedTokenRepository.get(TOKEN, CardTokenData.class, tokenBucketName);

        assertNotNull(cardTokenData);
        assertNotNull(cardTokenData.getPayments());
        assertNull(cardTokenData.getWithdrawals());
        assertEquals(1, cardTokenData.getPayments().size());
        assertTrue(cardTokenData.getPayments().containsKey("RUB"));
        assertTrue(cardTokenData.getPayments().get("RUB").getYears().containsKey(LocalDateTime.now().getYear()));
        assertEquals(1,
                cardTokenData.getPayments().get("RUB").getYears()
                        .get(LocalDateTime.now().getYear()).getYearCount());
        assertEquals(1000,
                cardTokenData.getPayments().get("RUB").getYears()
                        .get(LocalDateTime.now().getYear()).getYearSum());
        assertEquals(1,
                cardTokenData.getPayments().get("RUB").getYears()
                        .get(LocalDateTime.now().getYear())
                        .getMonths().get(LocalDateTime.now().getMonthValue()).getMonthCount());
        assertEquals(1000,
                cardTokenData.getPayments().get("RUB").getYears()
                        .get(LocalDateTime.now().getYear())
                        .getMonths().get(LocalDateTime.now().getMonthValue()).getMonthSum());

        paymentService.processPayment(createPayment());
        CardTokenData cardTokenDataUpdate =
                trustedTokenRepository.get(TOKEN, CardTokenData.class, tokenBucketName);

        assertNotNull(cardTokenDataUpdate);
        assertNotNull(cardTokenDataUpdate.getPayments());
        assertNull(cardTokenDataUpdate.getWithdrawals());
        assertEquals(1, cardTokenDataUpdate.getPayments().size());
        assertTrue(cardTokenDataUpdate.getPayments().containsKey("RUB"));
        assertTrue(cardTokenDataUpdate.getPayments().get("RUB").getYears().containsKey(LocalDateTime.now().getYear()));
        assertEquals(2,
                cardTokenDataUpdate.getPayments().get("RUB").getYears()
                        .get(LocalDateTime.now().getYear()).getYearCount());
        assertEquals(2000,
                cardTokenDataUpdate.getPayments().get("RUB").getYears()
                        .get(LocalDateTime.now().getYear()).getYearSum());
        assertEquals(2,
                cardTokenDataUpdate.getPayments().get("RUB").getYears()
                        .get(LocalDateTime.now().getYear())
                        .getMonths().get(LocalDateTime.now().getMonthValue()).getMonthCount());
        assertEquals(2000,
                cardTokenDataUpdate.getPayments().get("RUB").getYears()
                        .get(LocalDateTime.now().getYear())
                        .getMonths().get(LocalDateTime.now().getMonthValue()).getMonthSum());

    }

}
