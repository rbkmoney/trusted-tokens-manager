package com.rbkmoney.trusted.tokens.service;

import com.rbkmoney.trusted.tokens.converter.RowConverter;
import com.rbkmoney.trusted.tokens.model.*;
import com.rbkmoney.trusted.tokens.repository.TrustedTokenRepository;
import com.rbkmoney.trusted.tokens.updater.CardTokenDataUpdater;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawalService {

    private final CardTokenDataUpdater cardTokenDataUpdater;
    private final TrustedTokenRepository trustedTokenRepository;
    private final RowConverter rowConverter;
    @Value("${riak.bucket.token}")
    private String bucket;

    public Row updateWithdrawalCardTokenData(CardToken cardToken) {
        CardTokenData cardTokenData =
                Optional.ofNullable(trustedTokenRepository.get(cardToken.getToken(), CardTokenData.class, bucket))
                        .orElse(new CardTokenData());
        Map<String, CardTokenData.CurrencyData> currencyDataMap = cardTokenDataUpdater.updateCurrencyData(
                cardToken,
                Optional.ofNullable(cardTokenData.getWithdrawals())
                        .orElse(new HashMap<>()));
        cardTokenData.setWithdrawals(currencyDataMap);
        return rowConverter.convert(cardToken.getToken(), cardTokenData, bucket);
    }

}
