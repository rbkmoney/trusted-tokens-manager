package com.rbkmoney.trusted.tokens.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.fraudbusters.Withdrawal;
import com.rbkmoney.trusted.tokens.converter.*;
import com.rbkmoney.trusted.tokens.model.*;
import com.rbkmoney.trusted.tokens.repository.TrustedTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawalService {

    private final CardTokenDataConverter cardTokenDataConverter;
    private final CardTokenConverter cardTokenConverter;
    private final TrustedTokenRepository trustedTokenRepository;
    private final RowConverter rowConverter;
    private final ObjectMapper objectMapper;
    @Value("${riak.bucket.token}")
    private String bucket;

    public void processWithdrawal(Withdrawal withdrawal) {
        String token = withdrawal.getDestinationResource().getBankCard().getToken();
        CardTokenData cardTokenData =
                Optional.ofNullable(trustedTokenRepository.get(token, CardTokenData.class, bucket))
                        .orElse(new CardTokenData());
        CardToken cardToken = cardTokenConverter.convertWithdrawalToCardToken(withdrawal);
        Map<String, CardTokenData.CurrencyData> currencyMap = Optional.ofNullable(cardTokenData.getWithdrawals())
                .orElse(new HashMap<>());
        cardTokenData.setWithdrawals(cardTokenDataConverter.convert(cardToken, currencyMap));
        Row row = rowConverter.convert(token, cardTokenData);
        trustedTokenRepository.create(row, bucket);
    }

}
