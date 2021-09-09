package com.rbkmoney.trusted.tokens.updater;

import com.rbkmoney.trusted.tokens.model.CardTokenData;
import com.rbkmoney.trusted.tokens.model.CardTokensPaymentInfo;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.rbkmoney.trusted.tokens.updater.YearsDataUpdater.updateYearsData;

@Component
public class CardTokenDataUpdater {

    public Map<String, CardTokenData.CurrencyData> updateCurrencyData(
            CardTokensPaymentInfo cardTokensPaymentInfo,
            Map<String, CardTokenData.CurrencyData> currencyMap) {

        currencyMap.put(cardTokensPaymentInfo.getCurrency(), CardTokenData.CurrencyData.builder()
                .years(updateYearsData(currencyMap, cardTokensPaymentInfo))
                .build());
        return currencyMap;
    }

}
