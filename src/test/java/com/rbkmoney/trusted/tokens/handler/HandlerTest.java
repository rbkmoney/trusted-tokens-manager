package com.rbkmoney.trusted.tokens.handler;

import com.rbkmoney.trusted.tokens.*;
import com.rbkmoney.trusted.tokens.repository.CardTokenRepository;
import com.rbkmoney.trusted.tokens.repository.ConditionTemplateRepository;
import org.apache.thrift.TException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.rbkmoney.trusted.tokens.utils.CardTokenDataUtils.createCardTokenData;
import static com.rbkmoney.trusted.tokens.utils.ConditionTemplateRequestUtils.*;
import static com.rbkmoney.trusted.tokens.utils.ConditionTemplateUtils.*;
import static com.rbkmoney.trusted.tokens.utils.TransactionUtils.TOKEN;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TrustedTokensApplication.class)
class HandlerTest {

    @MockBean
    ConditionTemplateRepository conditionTemplateRepository;

    @MockBean
    CardTokenRepository cardTokenRepository;

    @Autowired
    TrustedTokensHandler trustedTokensHandler;

    @BeforeEach
    public void init() {
        Mockito.when(cardTokenRepository.get(TOKEN))
                .thenReturn(createCardTokenData());
        Mockito.when(conditionTemplateRepository.get("TemplateNotTrustedPayment"))
                .thenReturn(createTemplateNotTrusted(PAYMENT));
        Mockito.when(conditionTemplateRepository.get("TemplateTrustedPayment"))
                .thenReturn(createTemplateTrusted(PAYMENT));
        Mockito.when(conditionTemplateRepository.get("TemplateTrustedWithSeveralCurrencyPayment"))
                .thenReturn(createTemplateTrustedWithSeveralCurrency(PAYMENT));
        Mockito.when(conditionTemplateRepository.get("TemplateNotTrustedWithdrawal"))
                .thenReturn(createTemplateNotTrusted(WITHDRAWAL));
        Mockito.when(conditionTemplateRepository.get("TemplateTrustedWithdrawal"))
                .thenReturn(createTemplateTrusted(WITHDRAWAL));
        Mockito.when(conditionTemplateRepository.get("TemplateTrustedWithSeveralCurrencyWithdrawal"))
                .thenReturn(createTemplateTrustedWithSeveralCurrency(WITHDRAWAL));
    }

    @Test
    void isTokenTrustedTest() throws TException {
        assertFalse(trustedTokensHandler.isTokenTrusted(
                TOKEN, createTemplateNotTrusted(PAYMENT)));
        assertTrue(trustedTokensHandler.isTokenTrusted(
                TOKEN, createTemplateTrusted(PAYMENT)));
        assertTrue(trustedTokensHandler.isTokenTrusted(
                TOKEN, createTemplateTrustedWithSeveralCurrency(PAYMENT)));
        assertFalse(trustedTokensHandler.isTokenTrusted(
                TOKEN, createTemplateNotTrustedWithSeveralCurrency(PAYMENT)));
        assertFalse(trustedTokensHandler.isTokenTrusted(
                TOKEN, createTemplateNotTrusted(WITHDRAWAL)));
        assertTrue(trustedTokensHandler.isTokenTrusted(
                TOKEN, createTemplateTrusted(WITHDRAWAL)));
        assertTrue(trustedTokensHandler.isTokenTrusted(
                TOKEN, createTemplateTrustedWithSeveralCurrency(WITHDRAWAL)));
        assertFalse(trustedTokensHandler.isTokenTrusted(
                TOKEN, createTemplateNotTrustedWithSeveralCurrency(WITHDRAWAL)));
        assertThrows(InvalidRequest.class,
                () -> trustedTokensHandler.isTokenTrusted(TOKEN,
                        createTemplate(null, null)));
        assertTrue(trustedTokensHandler.isTokenTrusted(TOKEN,
                createTemplateWithWithdrawalAndPayment()));
    }

    @Test
    void isTokenTrustedByConditionTemplateNameTest() throws TException {
        assertFalse(trustedTokensHandler.isTokenTrustedByConditionTemplateName(
                TOKEN, "TemplateNotTrustedPayment"));
        assertTrue(trustedTokensHandler.isTokenTrustedByConditionTemplateName(
                TOKEN, "TemplateTrustedPayment"));
        assertTrue(trustedTokensHandler.isTokenTrustedByConditionTemplateName(
                TOKEN, "TemplateTrustedWithSeveralCurrencyPayment"));
        assertFalse(trustedTokensHandler.isTokenTrustedByConditionTemplateName(
                TOKEN, "TemplateNotTrustedWithdrawal"));
        assertTrue(trustedTokensHandler.isTokenTrustedByConditionTemplateName(
                TOKEN, "TemplateTrustedWithdrawal"));
        assertTrue(trustedTokensHandler.isTokenTrustedByConditionTemplateName(
                TOKEN, "TemplateTrustedWithSeveralCurrencyWithdrawal"));
        assertThrows(ConditionTemplateNotFound.class,
                () -> trustedTokensHandler.isTokenTrustedByConditionTemplateName(
                        TOKEN, "UnknownTemplate"));

    }

    @Test
    void createNewConditionTemplateTest() {
        assertThrows(NullPointerException.class,
                () -> trustedTokensHandler.createNewConditionTemplate(
                        createTemplatePaymentRequestWithNullCurrency()));
        assertThrows(NullPointerException.class,
                () -> trustedTokensHandler.createNewConditionTemplate(
                        createTemplatePaymentRequestWithNullYearsOffset()));
        assertThrows(NullPointerException.class,
                () -> trustedTokensHandler.createNewConditionTemplate(
                        createTemplatePaymentRequestWithNullYearsOffset()));
        assertThrows(InvalidRequest.class,
                () -> trustedTokensHandler.createNewConditionTemplate(
                        createTemplatePaymentRequestWithNullCount()));
        assertThrows(InvalidRequest.class,
                () -> trustedTokensHandler.createNewConditionTemplate(
                        createTemplatePaymentRequestWithNullSum()));
    }

}
