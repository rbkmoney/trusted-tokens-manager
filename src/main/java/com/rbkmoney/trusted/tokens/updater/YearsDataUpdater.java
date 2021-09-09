package com.rbkmoney.trusted.tokens.updater;

import com.rbkmoney.trusted.tokens.model.CardTokenData;
import com.rbkmoney.trusted.tokens.model.CardTokensPaymentInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class YearsDataUpdater {

    private final MonthsDataUpdater monthsDataUpdater;
    private final int keepDataYears = 3;

    public Map<Integer, CardTokenData.YearsData> updateYearsData(
            Map<String, CardTokenData.CurrencyData> currencyMap, CardTokensPaymentInfo cardTokensPaymentInfo) {
        String currency = cardTokensPaymentInfo.getCurrency();
        int year = cardTokensPaymentInfo.getYear();
        Map<Integer, CardTokenData.YearsData> yearsMap = Optional.of(currencyMap)
                .map(map -> map.get(currency))
                .map(CardTokenData.CurrencyData::getYears)
                .orElse(new HashMap<>());
        yearsMap.keySet().removeIf(key -> key <= year - keepDataYears);
        yearsMap.put(year, CardTokenData.YearsData.builder()
                .yearSum(updateYearSum(yearsMap, cardTokensPaymentInfo))
                .yearCount(updateYearCount(yearsMap, cardTokensPaymentInfo))
                .months(monthsDataUpdater.updateMonthsData(yearsMap, cardTokensPaymentInfo))
                .build());
        return yearsMap;
    }

    private long updateYearSum(Map<Integer, CardTokenData.YearsData> yearsMap,
                               CardTokensPaymentInfo cardTokensPaymentInfo) {
        long amount = cardTokensPaymentInfo.getAmount();
        int year = cardTokensPaymentInfo.getYear();
        return Optional.of(yearsMap)
                .map(map -> map.get(year))
                .map(CardTokenData.YearsData::getYearSum)
                .map(sum -> sum + amount)
                .orElse(amount);
    }

    private int updateYearCount(Map<Integer, CardTokenData.YearsData> yearsMap,
                                CardTokensPaymentInfo cardTokensPaymentInfo) {
        int year = cardTokensPaymentInfo.getYear();
        return Optional.of(yearsMap)
                .map(map -> map.get(year))
                .map(CardTokenData.YearsData::getYearCount)
                .map(count -> count + 1)
                .orElse(1);
    }

}
