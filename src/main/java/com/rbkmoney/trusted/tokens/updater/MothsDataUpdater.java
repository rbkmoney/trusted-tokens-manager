package com.rbkmoney.trusted.tokens.updater;

import com.rbkmoney.trusted.tokens.model.CardToken;
import com.rbkmoney.trusted.tokens.model.CardTokenData;

import java.util.*;

public class MothsDataUpdater {

    public static Map<Integer, CardTokenData.MonthsData> updateMonthsData(
            Map<Integer, CardTokenData.YearsData> yearsMap, CardToken cardToken) {
        int year = cardToken.getYear();
        int month = cardToken.getMonth();
        Map<Integer, CardTokenData.MonthsData> monthMap = Optional.of(yearsMap)
                .map(map -> map.get(year))
                .map(CardTokenData.YearsData::getMonths)
                .orElse(new HashMap<>());
        monthMap.put(month, CardTokenData.MonthsData.builder()
                .monthSum(updateMonthSum(monthMap, cardToken))
                .monthCount(updateMonthCount(monthMap, cardToken))
                .build());
        return monthMap;
    }

    private static long updateMonthSum(Map<Integer, CardTokenData.MonthsData> monthMap, CardToken cardToken) {
        long amount = cardToken.getAmount();
        int month = cardToken.getMonth();
        return Optional.of(monthMap)
                .map(map -> map.get(month))
                .map(CardTokenData.MonthsData::getMonthSum)
                .map(sum -> sum + amount)
                .orElse(amount);
    }

    private static int updateMonthCount(Map<Integer, CardTokenData.MonthsData> monthMap, CardToken cardToken) {
        int month = cardToken.getMonth();
        return Optional.of(monthMap)
                .map(map -> map.get(month))
                .map(CardTokenData.MonthsData::getMonthCount)
                .map(count -> count + 1)
                .orElse(1);
    }

}
