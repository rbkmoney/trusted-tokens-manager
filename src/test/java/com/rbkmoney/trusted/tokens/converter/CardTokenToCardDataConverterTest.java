package com.rbkmoney.trusted.tokens.converter;

import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.domain.CurrencyRef;
import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.damsel.fraudbusters.Withdrawal;
import com.rbkmoney.trusted.tokens.TrustedTokensApplication;
import com.rbkmoney.trusted.tokens.model.CardToken;
import com.rbkmoney.trusted.tokens.model.CardTokenData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static com.rbkmoney.trusted.tokens.utils.CardTokenDataUtils.createCardTokenData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(classes = TrustedTokensApplication.class)
class CardTokenToCardDataConverterTest {

    private final int currentYear = LocalDateTime.now().getYear();
    private final int currentMonth = LocalDateTime.now().getMonthValue();
    @Autowired
    CardTokenToCardDataConverter cardTokenToCardDataConverter;
    @Autowired
    TransactionToCardTokenConverter transactionToCardTokenConverter;
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
        cardTokenToCardDataConverter.convert(paymentCardToken, cardTokenData.getPayments());
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
        cardTokenToCardDataConverter.convert(withdrawalCardToken, cardTokenData.getWithdrawals());
        assertFalse(cardTokenData.getWithdrawals().get("RUB").getYears()
                .containsKey(currentYear - 3));
        assertEquals(79, cardTokenData.getWithdrawals().get("RUB").getYears()
                .get(currentYear).getYearCount());
        assertEquals(currentMonth + 1, cardTokenData.getWithdrawals().get("RUB").getYears()
                .get(currentYear)
                .getMonths().get(currentMonth).getMonthCount());
    }

    private CardToken createPaymentCardToken() {
        Payment payment = new Payment();
        Cash cash = new Cash();
        cash.setCurrency(new CurrencyRef().setSymbolicCode("RUB"));
        cash.setAmount(1000);
        payment.setCost(cash);
        payment.setEventTime(String.valueOf(LocalDateTime.now()));
        return transactionToCardTokenConverter.convertPaymentToCardToken(payment);
    }

    private CardToken createWithdrawalCardToken() {
        Withdrawal withdrawal = new Withdrawal();
        Cash cash = new Cash();
        cash.setCurrency(new CurrencyRef().setSymbolicCode("RUB"));
        withdrawal.setCost(cash);
        withdrawal.setEventTime(String.valueOf(LocalDateTime.now()));
        return transactionToCardTokenConverter.convertWithdrawalToCardToken(withdrawal);
    }

}
