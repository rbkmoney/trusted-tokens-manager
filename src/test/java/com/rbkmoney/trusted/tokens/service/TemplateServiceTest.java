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
    public void templateServiceTest() throws InterruptedException, TException {
        sleep(50000);

        ConditionTemplate emptyConditionTemplate =
                trustedTokenRepository.get(CONDITION_NAME, ConditionTemplate.class, templateBucketName);

        assertNull(emptyConditionTemplate);
        assertThrows(ConditionTemplateNotFound.class, () ->
                templateService.isTrustedTokenByTemplateName(TOKEN, CONDITION_NAME));

        templateService.createTemplate(createTrueTemplateRequest());
        trustedTokenRepository.create(
                cardTokenToRowConverter.convert(TOKEN, createCardTokenData()), tokenBucketName);

        assertTrue(templateService.isTrustedTokenByTemplateName(TOKEN, CONDITION_NAME));
        assertTrue(templateService.isTrustedTokenByTemplateName(TOKEN, CONDITION_NAME));
        assertThrows(ConditionTemplateAlreadyExists.class,
                () -> templateService.createTemplate(createTemplateRequestWithTwoConditions()));

    }
}
