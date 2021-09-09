package com.rbkmoney.trusted.tokens.service;

import com.rbkmoney.trusted.tokens.converter.RowConverter;
import com.rbkmoney.trusted.tokens.model.*;
import com.rbkmoney.trusted.tokens.repository.CardTokenRepository;
import com.rbkmoney.trusted.tokens.updater.CardTokenDataUpdater;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawalService {

    private final CardTokenDataUpdater cardTokenDataUpdater;
    private final CardTokenRepository cardTokenRepository;
    private final RowConverter rowConverter;

    public Row addWithdrawalCardTokenData(CardTokensPaymentInfo cardTokensPaymentInfo) {
        CardTokenData cardTokenData =
                Optional.ofNullable(cardTokenRepository.get(cardTokensPaymentInfo.getToken()))
                        .orElse(new CardTokenData());
        Map<String, CardTokenData.CurrencyData> currencyDataMap = cardTokenDataUpdater.updateCurrencyData(
                cardTokensPaymentInfo,
                Optional.ofNullable(cardTokenData.getWithdrawals())
                        .orElse(new HashMap<>()));
        cardTokenData.setWithdrawals(currencyDataMap);
        return rowConverter.convert(cardTokensPaymentInfo.getToken(), cardTokenData);
    }

}
