package com.rbkmoney.trusted.tokens.updater;

import com.rbkmoney.trusted.tokens.config.MockedStartupInitializers;
import com.rbkmoney.trusted.tokens.converter.TransactionToCardTokensTransactionInfoConverter;
import com.rbkmoney.trusted.tokens.model.CardTokenData;
import com.rbkmoney.trusted.tokens.model.CardTokensTransactionInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static com.rbkmoney.trusted.tokens.utils.CardTokenDataUtils.createCardTokenData;
import static com.rbkmoney.trusted.tokens.utils.TransactionUtils.createPayment;
import static com.rbkmoney.trusted.tokens.utils.TransactionUtils.createWithdrawal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Import(MockedStartupInitializers.class)
class CardTokensTransactionInfoDataUpdaterTest {

    private final int currentYear = LocalDateTime.now().getYear();
    private final int currentMonth = LocalDateTime.now().getMonthValue();

    @Autowired
    private CardTokenDataUpdater cardTokenDataUpdater;

    @Autowired
    private TransactionToCardTokensTransactionInfoConverter transactionToCardTokensTransactionInfoConverter;

    private CardTokenData cardTokenData;
    private CardTokensTransactionInfo paymentCardTokensTransactionInfo;
    private CardTokensTransactionInfo withdrawalCardTokensTransactionInfo;

    @BeforeEach
    public void init() {
        cardTokenData = createCardTokenData();
        paymentCardTokensTransactionInfo = createPaymentCardToken();
        withdrawalCardTokensTransactionInfo = createWithdrawalCardToken();
    }

    @Test
    void convertPaymentTest() {
        cardTokenDataUpdater.updateCurrencyData(paymentCardTokensTransactionInfo, cardTokenData.getPayments());
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
        cardTokenDataUpdater.updateCurrencyData(withdrawalCardTokensTransactionInfo, cardTokenData.getWithdrawals());
        assertFalse(cardTokenData.getWithdrawals().get("RUB").getYears()
                .containsKey(currentYear - 3));
        assertEquals(79, cardTokenData.getWithdrawals().get("RUB").getYears()
                .get(currentYear).getYearCount());
        assertEquals(currentMonth + 1, cardTokenData.getWithdrawals().get("RUB").getYears()
                .get(currentYear)
                .getMonths().get(currentMonth).getMonthCount());
    }

    private CardTokensTransactionInfo createPaymentCardToken() {
        return transactionToCardTokensTransactionInfoConverter.convertPayment(createPayment());
    }

    private CardTokensTransactionInfo createWithdrawalCardToken() {
        return transactionToCardTokensTransactionInfoConverter.convertWithdrawal(createWithdrawal());
    }

}
