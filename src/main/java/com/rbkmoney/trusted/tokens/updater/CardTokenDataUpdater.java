package com.rbkmoney.trusted.tokens.updater;

import com.rbkmoney.trusted.tokens.model.CardTokenData;
import com.rbkmoney.trusted.tokens.model.CardTokensPaymentInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CardTokenDataUpdater {

    private final YearsDataUpdater yearsDataUpdater;

    public Map<String, CardTokenData.CurrencyData> updateCurrencyData(
            CardTokensPaymentInfo cardTokensPaymentInfo,
            Map<String, CardTokenData.CurrencyData> currencyMap) {

        currencyMap.put(cardTokensPaymentInfo.getCurrency(), CardTokenData.CurrencyData.builder()
                .years(yearsDataUpdater.updateYearsData(currencyMap, cardTokensPaymentInfo))
                .build());
        return currencyMap;
    }

}
