package com.rbkmoney.trusted.tokens.repository;

import com.rbkmoney.trusted.tokens.ConditionTemplate;
import com.rbkmoney.trusted.tokens.config.RiakAbstractTestIntegration;
import com.rbkmoney.trusted.tokens.converter.CardTokenToRowConverter;
import com.rbkmoney.trusted.tokens.converter.TemplateToRowConverter;
import com.rbkmoney.trusted.tokens.model.CardTokenData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.rbkmoney.trusted.tokens.utils.CardTokenDataUtils.createCardTokenData;
import static com.rbkmoney.trusted.tokens.utils.ConditionTemplateUtils.createTemplateWithWithdrawalAndPayment;
import static com.rbkmoney.trusted.tokens.utils.TransactionUtils.TOKEN;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TokenRepositoryTest extends RiakAbstractTestIntegration {

    @Autowired
    private TrustedTokenRepository trustedTokenRepository;

    @Autowired
    private CardTokenToRowConverter cardTokenToRowConverter;

    @Autowired
    private TemplateToRowConverter templateToRowConverter;

    @Test
    public void riakTestCardTokenData() throws InterruptedException {
        sleep(50000);
        CardTokenData emptyCardTokenData = trustedTokenRepository.get(TOKEN, CardTokenData.class, tokenBucketName);

        assertNull(emptyCardTokenData);

        trustedTokenRepository.create(
                cardTokenToRowConverter.convert(TOKEN, createCardTokenData()),
                tokenBucketName);
        CardTokenData cardTokenData = trustedTokenRepository.get(TOKEN, CardTokenData.class, tokenBucketName);

        assertNotNull(cardTokenData.getPayments());
        assertNotNull(cardTokenData.getWithdrawals());
    }

    @Test
    public void riakTestConditionTemplate() throws InterruptedException {
        sleep(50000);
        ConditionTemplate emptyConditionTemplate =
                trustedTokenRepository.get(TOKEN, ConditionTemplate.class, templateBucketName);

        assertNull(emptyConditionTemplate);

        trustedTokenRepository.create(
                templateToRowConverter.convert(TOKEN, createTemplateWithWithdrawalAndPayment()),
                templateBucketName);
        ConditionTemplate conditionTemplate =
                trustedTokenRepository.get(TOKEN, ConditionTemplate.class, templateBucketName);

        assertNotNull(conditionTemplate.getPaymentsConditions());
        assertNotNull(conditionTemplate.getWithdrawalsConditions());
    }

}
