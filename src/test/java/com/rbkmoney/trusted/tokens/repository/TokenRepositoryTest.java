package com.rbkmoney.trusted.tokens.repository;

import com.rbkmoney.trusted.tokens.ConditionTemplate;
import com.rbkmoney.trusted.tokens.config.RiakAbstractTestContainer;
import com.rbkmoney.trusted.tokens.converter.CardTokenToRowConverter;
import com.rbkmoney.trusted.tokens.converter.TemplateToRowConverter;
import com.rbkmoney.trusted.tokens.model.CardTokenData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.rbkmoney.trusted.tokens.utils.CardTokenDataUtils.createCardTokenData;
import static com.rbkmoney.trusted.tokens.utils.ConditionTemplateUtils.createTemplateWithWithdrawalAndPayment;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TokenRepositoryTest extends RiakAbstractTestContainer {

    private static final String KEY = "key";

    @Autowired
    private TrustedTokenRepository trustedTokenRepository;

    @Autowired
    private CardTokenToRowConverter cardTokenToRowConverter;

    @Autowired
    private TemplateToRowConverter templateToRowConverter;

    @Test
    public void riakTestCardTokenData() throws InterruptedException {
        sleep(50000);
        CardTokenData emptyCardTokenData = trustedTokenRepository.get(KEY, CardTokenData.class, tokenBucketName);

        assertNull(emptyCardTokenData.getPayments());
        assertNull(emptyCardTokenData.getWithdrawals());

        trustedTokenRepository.create(
                cardTokenToRowConverter.convert(KEY, createCardTokenData()),
                tokenBucketName);
        CardTokenData cardTokenData = trustedTokenRepository.get(KEY, CardTokenData.class, tokenBucketName);

        assertNotNull(cardTokenData.getPayments());
        assertNotNull(cardTokenData.getWithdrawals());
    }

    @Test
    public void riakTestConditionTemplate() throws InterruptedException {
        sleep(50000);
        ConditionTemplate emptyConditionTemplate =
                trustedTokenRepository.get(KEY, ConditionTemplate.class, templateBucketName);

        assertNull(emptyConditionTemplate.getPaymentsConditions());
        assertNull(emptyConditionTemplate.getWithdrawalsConditions());

        trustedTokenRepository.create(
                templateToRowConverter.convert(KEY, createTemplateWithWithdrawalAndPayment()),
                templateBucketName);
        ConditionTemplate conditionTemplate =
                trustedTokenRepository.get(KEY, ConditionTemplate.class, templateBucketName);

        assertNotNull(conditionTemplate.getPaymentsConditions());
        assertNotNull(conditionTemplate.getWithdrawalsConditions());
    }

}
