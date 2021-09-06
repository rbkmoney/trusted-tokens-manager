package com.rbkmoney.trusted.tokens.service;

import com.rbkmoney.trusted.tokens.config.RiakAbstractTestIntegration;
import com.rbkmoney.trusted.tokens.model.CardTokenData;
import com.rbkmoney.trusted.tokens.repository.TrustedTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static com.rbkmoney.trusted.tokens.utils.TransactionUtils.TOKEN;
import static com.rbkmoney.trusted.tokens.utils.TransactionUtils.createWithdrawal;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

public class WithdrawalServiceTest extends RiakAbstractTestIntegration {

    @Autowired
    private TrustedTokenRepository trustedTokenRepository;

    @Autowired
    private WithdrawalService withdrawalService;


    @Test
    public void processWithdrawalTest() throws InterruptedException {
        sleep(50000);
        withdrawalService.processWithdrawal(createWithdrawal());
        CardTokenData cardTokenData =
                trustedTokenRepository.get(TOKEN, CardTokenData.class, tokenBucketName);

        assertNotNull(cardTokenData);
        assertNotNull(cardTokenData.getWithdrawals());
        assertNull(cardTokenData.getPayments());
        assertEquals(1, cardTokenData.getWithdrawals().size());
        assertTrue(cardTokenData.getWithdrawals().containsKey("RUB"));
        assertTrue(cardTokenData.getWithdrawals().get("RUB").getYears().containsKey(LocalDateTime.now().getYear()));
        assertEquals(1,
                cardTokenData.getWithdrawals().get("RUB").getYears()
                        .get(LocalDateTime.now().getYear()).getYearCount());
        assertEquals(0,
                cardTokenData.getWithdrawals().get("RUB").getYears()
                        .get(LocalDateTime.now().getYear()).getYearSum());
        assertEquals(1,
                cardTokenData.getWithdrawals().get("RUB").getYears()
                        .get(LocalDateTime.now().getYear())
                        .getMonths().get(LocalDateTime.now().getMonthValue()).getMonthCount());
        assertEquals(0,
                cardTokenData.getWithdrawals().get("RUB").getYears()
                        .get(LocalDateTime.now().getYear())
                        .getMonths().get(LocalDateTime.now().getMonthValue()).getMonthSum());

        withdrawalService.processWithdrawal(createWithdrawal());
        CardTokenData cardTokenDataUpdate =
                trustedTokenRepository.get(TOKEN, CardTokenData.class, tokenBucketName);

        assertNotNull(cardTokenDataUpdate);
        assertNotNull(cardTokenDataUpdate.getWithdrawals());
        assertNull(cardTokenDataUpdate.getPayments());
        assertEquals(1, cardTokenDataUpdate.getWithdrawals().size());
        assertTrue(cardTokenDataUpdate.getWithdrawals().containsKey("RUB"));
        assertTrue(cardTokenDataUpdate.getWithdrawals().get("RUB").getYears().containsKey(LocalDateTime.now().getYear()));
        assertEquals(2,
                cardTokenDataUpdate.getWithdrawals().get("RUB").getYears()
                        .get(LocalDateTime.now().getYear()).getYearCount());
        assertEquals(0,
                cardTokenDataUpdate.getWithdrawals().get("RUB").getYears()
                        .get(LocalDateTime.now().getYear()).getYearSum());
        assertEquals(2,
                cardTokenDataUpdate.getWithdrawals().get("RUB").getYears()
                        .get(LocalDateTime.now().getYear())
                        .getMonths().get(LocalDateTime.now().getMonthValue()).getMonthCount());
        assertEquals(0,
                cardTokenDataUpdate.getWithdrawals().get("RUB").getYears()
                        .get(LocalDateTime.now().getYear())
                        .getMonths().get(LocalDateTime.now().getMonthValue()).getMonthSum());

    }

}
