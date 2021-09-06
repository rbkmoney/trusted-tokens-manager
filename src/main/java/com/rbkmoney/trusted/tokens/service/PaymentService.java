package com.rbkmoney.trusted.tokens.service;

import com.rbkmoney.damsel.fraudbusters.Payment;
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
public class PaymentService {

    private final CardTokenToCardDataConverter cardTokenToCardDataConverter;
    private final TransactionToCardTokenConverter transactionToCardTokenConverter;
    private final TrustedTokenRepository trustedTokenRepository;
    private final CardTokenToRowConverter cardTokenToRowConverter;
    @Value("${riak.bucket.token}")
    private String bucket;

    public void processPayment(Payment payment) {
        String token = payment.getPaymentTool().getBankCard().getToken();
        CardTokenData cardTokenData = trustedTokenRepository.get(token, CardTokenData.class, bucket);
        CardToken cardToken = transactionToCardTokenConverter.convertPaymentToCardToken(payment);
        Map<String, CardTokenData.CurrencyData> currencyMap = Optional.ofNullable(cardTokenData.getPayments())
                .orElse(new HashMap<>());
        cardTokenData.setPayments(cardTokenToCardDataConverter.convert(cardToken, currencyMap));
        Row row = cardTokenToRowConverter.convert(token, cardTokenData);
        trustedTokenRepository.create(row, bucket);
    }

}
