package com.rbkmoney.trusted.tokens.converter;

import com.rbkmoney.trusted.tokens.TrustedTokensApplication;
import com.rbkmoney.trusted.tokens.model.CardToken;
import com.rbkmoney.trusted.tokens.model.CardTokenData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static com.rbkmoney.trusted.tokens.utils.CardTokenDataUtils.createCardTokenData;
import static com.rbkmoney.trusted.tokens.utils.TransactionUtils.createPayment;
import static com.rbkmoney.trusted.tokens.utils.TransactionUtils.createWithdrawal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(classes = TrustedTokensApplication.class)
class CardTokenDataConverterTest {

    private final int currentYear = LocalDateTime.now().getYear();
    private final int currentMonth = LocalDateTime.now().getMonthValue();

    @Autowired
    private CardTokenDataConverter cardTokenDataConverter;

    @Autowired
    private TransactionToCardTokenConverter transactionToCardTokenConverter;

    private CardTokenData cardTokenData;
    private CardToken paymentCardToken;
    private CardToken withdrawalCardToken;

    @BeforeEach
    public void init() {
        cardTokenData = createCardTokenData();
        paymentCardToken = createPaymentCardToken();
        withdrawalCardToken = createWithdrawalCardToken();
    }

    @Test
    void convertPaymentTest() {
        cardTokenDataConverter.convert(paymentCardToken, cardTokenData.getPayments());
        assertFalse(cardTokenData.getPayments().get("RUB").getYears()
                .containsKey(currentYear - 3));
        assertEquals(79000, cardTokenData.getPayments().get("RUB").getYears()
                .get(currentYear).getYearSum());
        assertEquals(79, cardTokenData.getPayments().get("RUB").getYears()
                .get(currentYear).getYearCount());
        assertEquals((currentMonth + 1) * 1000L, cardTokenData.getPayments().get("RUB").getYears()
                .get(currentYear)
                .getMonths().get(currentMonth).getMonthSum());
        assertEquals(currentMonth + 1, cardTokenData.getPayments().get("RUB").getYears()
                .get(currentYear)
                .getMonths().get(currentMonth).getMonthCount());
    }

    @Test
    void convertWithdrawalTest() {
        cardTokenDataConverter.convert(withdrawalCardToken, cardTokenData.getWithdrawals());
        assertFalse(cardTokenData.getWithdrawals().get("RUB").getYears()
                .containsKey(currentYear - 3));
        assertEquals(79, cardTokenData.getWithdrawals().get("RUB").getYears()
                .get(currentYear).getYearCount());
        assertEquals(currentMonth + 1, cardTokenData.getWithdrawals().get("RUB").getYears()
                .get(currentYear)
                .getMonths().get(currentMonth).getMonthCount());
    }

    private CardToken createPaymentCardToken() {
        return transactionToCardTokenConverter.convertPaymentToCardToken(createPayment());
    }

    private CardToken createWithdrawalCardToken() {
        return transactionToCardTokenConverter.convertWithdrawalToCardToken(createWithdrawal());
    }

}
