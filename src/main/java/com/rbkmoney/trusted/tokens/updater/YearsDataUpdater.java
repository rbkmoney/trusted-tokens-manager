package com.rbkmoney.trusted.tokens.updater;

import com.rbkmoney.trusted.tokens.model.CardToken;
import com.rbkmoney.trusted.tokens.model.CardTokenData;

import java.util.*;

import static com.rbkmoney.trusted.tokens.updater.MothsDataUpdater.updateMonthsData;

public class YearsDataUpdater {

    public static Map<Integer, CardTokenData.YearsData> updateYearsData(
            Map<String, CardTokenData.CurrencyData> currencyMap, CardToken cardToken) {
        String currency = cardToken.getCurrency();
        int year = cardToken.getYear();
        Map<Integer, CardTokenData.YearsData> yearsMap = Optional.of(currencyMap)
                .map(map -> map.get(currency))
                .map(CardTokenData.CurrencyData::getYears)
                .orElse(new HashMap<>());
        yearsMap.keySet().removeIf(key -> key <= year - 3);
        yearsMap.put(year, CardTokenData.YearsData.builder()
                .yearSum(updateYearSum(yearsMap, cardToken))
                .yearCount(updateYearCount(yearsMap, cardToken))
                .months(updateMonthsData(yearsMap, cardToken))
                .build());
        return yearsMap;
    }

    private static long updateYearSum(Map<Integer, CardTokenData.YearsData> yearsMap, CardToken cardToken) {
        long amount = cardToken.getAmount();
        int year = cardToken.getYear();
        return Optional.of(yearsMap)
                .map(map -> map.get(year))
                .map(CardTokenData.YearsData::getYearSum)
                .map(sum -> sum + amount)
                .orElse(amount);
    }

    private static int updateYearCount(Map<Integer, CardTokenData.YearsData> yearsMap, CardToken cardToken) {
        int year = cardToken.getYear();
        return Optional.of(yearsMap)
                .map(map -> map.get(year))
                .map(CardTokenData.YearsData::getYearCount)
                .map(count -> count + 1)
                .orElse(1);
    }

}
