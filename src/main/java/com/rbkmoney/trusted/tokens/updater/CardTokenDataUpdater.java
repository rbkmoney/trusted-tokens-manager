package com.rbkmoney.trusted.tokens.updater;

import com.rbkmoney.trusted.tokens.model.CardToken;
import com.rbkmoney.trusted.tokens.model.CardTokenData;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.rbkmoney.trusted.tokens.updater.YearsDataUpdater.updateYearsData;

@Component
public class CardTokenDataUpdater {

    public Map<String, CardTokenData.CurrencyData> updateCurrencyData(
            CardToken cardToken,
            Map<String, CardTokenData.CurrencyData> currencyMap) {

        currencyMap.put(cardToken.getCurrency(), CardTokenData.CurrencyData.builder()
                .years(updateYearsData(currencyMap, cardToken))
                .build());
        return currencyMap;
    }

}
