package com.rbkmoney.trusted.tokens.handler.impl;

import com.rbkmoney.trusted.tokens.ConditionTemplate;
import com.rbkmoney.trusted.tokens.model.CardTokenData;
import com.rbkmoney.trusted.tokens.repository.TrustedTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.rbkmoney.trusted.tokens.handler.utis.ConditionTrustedResult.isTrusted;


@Component
@RequiredArgsConstructor
@Slf4j
public class WithdrawalTokensHandler implements TrustedTokensCommonHandler {

    private final TrustedTokenRepository trustedTokenRepository;
    @Value("${riak.bucket.token}")
    private String bucket;

    @Override
    public boolean filter(ConditionTemplate conditionTemplate) {
        return conditionTemplate.getWithdrawalsConditions() != null
                && conditionTemplate.getPaymentsConditions() == null;
    }

    @Override
    public boolean handler(String cardToken, ConditionTemplate conditionTemplate) {
        CardTokenData cardTokenData = trustedTokenRepository.get(cardToken, CardTokenData.class, bucket);
        return cardTokenData != null && isTrusted(conditionTemplate.getWithdrawalsConditions().getConditions(),
                cardTokenData.getWithdrawals());
    }
}
