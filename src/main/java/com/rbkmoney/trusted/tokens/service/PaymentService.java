package com.rbkmoney.trusted.tokens.service;

import com.rbkmoney.trusted.tokens.converter.CardTokenDataConverter;
import com.rbkmoney.trusted.tokens.converter.RowConverter;
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
public class PaymentService {

    private final CardTokenDataConverter cardTokenDataConverter;
    private final TrustedTokenRepository trustedTokenRepository;
    private final RowConverter rowConverter;
    @Value("${riak.bucket.token}")
    private String bucket;

    public void processPayment(CardToken cardToken) {
        CardTokenData cardTokenData =
                Optional.ofNullable(trustedTokenRepository.get(cardToken.getToken(), CardTokenData.class, bucket))
                        .orElse(new CardTokenData());
        Map<String, CardTokenData.CurrencyData> currencyMap = cardTokenDataConverter.convert(
                cardToken,
                Optional.ofNullable(cardTokenData.getPayments())
                        .orElse(new HashMap<>()));
        cardTokenData.setPayments(currencyMap);
        Row row = rowConverter.convert(cardToken.getToken(), cardTokenData);
        trustedTokenRepository.create(row, bucket);
    }

}
