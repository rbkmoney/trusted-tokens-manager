package com.rbkmoney.trusted.tokens.updater;

import com.rbkmoney.trusted.tokens.model.CardTokenData;
import com.rbkmoney.trusted.tokens.model.CardTokensPaymentInfo;

import java.util.*;

import static com.rbkmoney.trusted.tokens.updater.MonthsDataUpdater.updateMonthsData;

public class YearsDataUpdater {

    public static Map<Integer, CardTokenData.YearsData> updateYearsData(
            Map<String, CardTokenData.CurrencyData> currencyMap, CardTokensPaymentInfo cardTokensPaymentInfo) {
        String currency = cardTokensPaymentInfo.getCurrency();
        int year = cardTokensPaymentInfo.getYear();
        Map<Integer, CardTokenData.YearsData> yearsMap = Optional.of(currencyMap)
                .map(map -> map.get(currency))
                .map(CardTokenData.CurrencyData::getYears)
                .orElse(new HashMap<>());
        yearsMap.keySet().removeIf(key -> key <= year - 3);
        yearsMap.put(year, CardTokenData.YearsData.builder()
                .yearSum(updateYearSum(yearsMap, cardTokensPaymentInfo))
                .yearCount(updateYearCount(yearsMap, cardTokensPaymentInfo))
                .months(updateMonthsData(yearsMap, cardTokensPaymentInfo))
                .build());
        return yearsMap;
    }

    private static long updateYearSum(Map<Integer, CardTokenData.YearsData> yearsMap,
                                      CardTokensPaymentInfo cardTokensPaymentInfo) {
        long amount = cardTokensPaymentInfo.getAmount();
        int year = cardTokensPaymentInfo.getYear();
        return Optional.of(yearsMap)
                .map(map -> map.get(year))
                .map(CardTokenData.YearsData::getYearSum)
                .map(sum -> sum + amount)
                .orElse(amount);
    }

    private static int updateYearCount(Map<Integer, CardTokenData.YearsData> yearsMap,
                                       CardTokensPaymentInfo cardTokensPaymentInfo) {
        int year = cardTokensPaymentInfo.getYear();
        return Optional.of(yearsMap)
                .map(map -> map.get(year))
                .map(CardTokenData.YearsData::getYearCount)
                .map(count -> count + 1)
                .orElse(1);
    }

}
