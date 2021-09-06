package com.rbkmoney.trusted.tokens.service;

import com.rbkmoney.trusted.tokens.*;
import com.rbkmoney.trusted.tokens.config.RiakAbstractTestIntegration;
import com.rbkmoney.trusted.tokens.converter.CardTokenToRowConverter;
import com.rbkmoney.trusted.tokens.repository.TrustedTokenRepository;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.rbkmoney.trusted.tokens.utils.CardTokenDataUtils.createCardTokenData;
import static com.rbkmoney.trusted.tokens.utils.ConditionTemplateRequestUtils.*;
import static com.rbkmoney.trusted.tokens.utils.TransactionUtils.TOKEN;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

public class TemplateServiceTest extends RiakAbstractTestIntegration {

    @Autowired
    private TrustedTokenRepository trustedTokenRepository;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private CardTokenToRowConverter cardTokenToRowConverter;

    @Test
    public void createTemplateTest() throws InterruptedException, TException {
        sleep(50000);

        ConditionTemplate emptyConditionTemplate =
                trustedTokenRepository.get(CONDITION_NAME, ConditionTemplate.class, templateBucketName);

        assertNull(emptyConditionTemplate.getPaymentsConditions());
        assertNull(emptyConditionTemplate.getWithdrawalsConditions());

        templateService.createTemplate(createTemplateRequestWithTwoConditions());

        assertThrows(ConditionTemplateAlreadyExists.class,
                () -> templateService.createTemplate(createTemplateRequestWithTwoConditions()));

        ConditionTemplate conditionTemplateUp =
                trustedTokenRepository.get(CONDITION_NAME, ConditionTemplate.class, templateBucketName);

        assertNotNull(conditionTemplateUp.getPaymentsConditions());
        assertNotNull(conditionTemplateUp.getWithdrawalsConditions());
    }

    @Test
    public void isTrusted() throws InterruptedException, TException {
        sleep(50000);
        assertThrows(ConditionTemplateNotFound.class, () -> templateService.isTrusted(TOKEN, CONDITION_NAME));

        templateService.createTemplate(createTrueTemplateRequest());
        trustedTokenRepository.create(
                cardTokenToRowConverter.convert(TOKEN, createCardTokenData()), tokenBucketName);
        assertTrue(templateService.isTrusted(TOKEN, CONDITION_NAME));
        assertTrue(templateService.isTrusted(TOKEN, CONDITION_NAME));

    }
}
