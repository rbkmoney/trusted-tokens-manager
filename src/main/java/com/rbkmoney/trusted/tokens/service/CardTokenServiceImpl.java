package com.rbkmoney.trusted.tokens.service;

import com.rbkmoney.trusted.tokens.converter.RowConverter;
import com.rbkmoney.trusted.tokens.model.CardTokenData;
import com.rbkmoney.trusted.tokens.model.CardTokensPaymentInfo;
import com.rbkmoney.trusted.tokens.model.Row;
import com.rbkmoney.trusted.tokens.repository.CardTokenRepository;
import com.rbkmoney.trusted.tokens.updater.CardTokenDataUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardTokenServiceImpl implements CardTokenService {

    private final CardTokenDataUpdater cardTokenDataUpdater;
    private final RowConverter rowConverter;
    private final CardTokenRepository repository;

    @Override
    public void save(CardTokenData cardTokenData, String token) {
        Row row = rowConverter.convert(token, cardTokenData);
        repository.create(row);
    }

    @Override
    public CardTokenData get(String token) {
        return Optional.ofNullable(repository.get(token))
                .orElse(new CardTokenData());
    }

    @Override
    public void addWithdrawal(CardTokenData cardTokenData, CardTokensPaymentInfo cardTokensPaymentInfo) {
        Map<String, CardTokenData.CurrencyData> currencyDataMap = cardTokenDataUpdater.updateCurrencyData(
                cardTokensPaymentInfo,
                Optional.ofNullable(cardTokenData.getWithdrawals())
                        .orElse(new HashMap<>()));
        cardTokenData.setWithdrawals(currencyDataMap);
        cardTokenData.setLastWithdrawalId(cardTokensPaymentInfo.getLastWithdrawalId());
    }

    @Override
    public void addPayment(CardTokenData cardTokenData, CardTokensPaymentInfo cardTokensPaymentInfo) {
        Map<String, CardTokenData.CurrencyData> currencyMap = cardTokenDataUpdater.updateCurrencyData(
                cardTokensPaymentInfo,
                Optional.ofNullable(cardTokenData.getPayments())
                        .orElse(new HashMap<>()));
        cardTokenData.setPayments(currencyMap);
        cardTokenData.setLastPaymentId(cardTokensPaymentInfo.getLastPaymentId());
    }

}
