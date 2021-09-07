package com.rbkmoney.trusted.tokens.handler;

import com.rbkmoney.trusted.tokens.*;
import com.rbkmoney.trusted.tokens.model.CardTokenData;
import com.rbkmoney.trusted.tokens.repository.TrustedTokenRepository;
import org.apache.thrift.TException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    TrustedTokenRepository trustedTokenRepository;

    @Autowired
    TrustedTokensHandler trustedTokensHandler;

    @Value("${riak.bucket.token}")
    private String tokenBucketName;

    @Value("${riak.bucket.template}")
    private String templateBucketName;

    @BeforeEach
    public void init() {
        Mockito.when(trustedTokenRepository.get(TOKEN, CardTokenData.class, tokenBucketName))
                .thenReturn(createCardTokenData());
        Mockito.when(trustedTokenRepository.get(
                "TemplateNotTrustedPayment",
                ConditionTemplate.class,
                templateBucketName)).thenReturn(createTemplateNotTrusted(PAYMENT));
        Mockito.when(trustedTokenRepository.get(
                "TemplateTrustedPayment",
                ConditionTemplate.class,
                templateBucketName)).thenReturn(createTemplateTrusted(PAYMENT));
        Mockito.when(trustedTokenRepository.get(
                "TemplateTrustedWithSeveralCurrencyPayment",
                ConditionTemplate.class,
                templateBucketName)).thenReturn(createTemplateTrustedWithSeveralCurrency(PAYMENT));
        Mockito.when(trustedTokenRepository.get(
                "TemplateNotTrustedWithdrawal",
                ConditionTemplate.class,
                templateBucketName)).thenReturn(createTemplateNotTrusted(WITHDRAWAL));
        Mockito.when(trustedTokenRepository.get(
                "TemplateTrustedWithdrawal",
                ConditionTemplate.class,
                templateBucketName)).thenReturn(createTemplateTrusted(WITHDRAWAL));
        Mockito.when(trustedTokenRepository.get(
                "TemplateTrustedWithSeveralCurrencyWithdrawal",
                ConditionTemplate.class,
                templateBucketName)).thenReturn(createTemplateTrustedWithSeveralCurrency(WITHDRAWAL));
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
        assertThrows(InvalidRequest.class,
                () -> trustedTokensHandler.isTokenTrusted(TOKEN,
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
        assertThrows(InvalidRequest.class,
                () -> trustedTokensHandler.createNewConditionTemplate(createTemplateRequestWithTwoConditions()));
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
