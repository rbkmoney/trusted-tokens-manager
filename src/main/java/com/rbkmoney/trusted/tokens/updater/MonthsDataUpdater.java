package com.rbkmoney.trusted.tokens.updater;

import com.rbkmoney.trusted.tokens.model.CardTokenData;
import com.rbkmoney.trusted.tokens.model.CardTokensPaymentInfo;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MonthsDataUpdater {

    public Map<Integer, CardTokenData.MonthsData> updateMonthsData(
            Map<Integer, CardTokenData.YearsData> yearsMap, CardTokensPaymentInfo cardTokensPaymentInfo) {
        int year = cardTokensPaymentInfo.getYear();
        int month = cardTokensPaymentInfo.getMonth();
        Map<Integer, CardTokenData.MonthsData> monthMap = Optional.of(yearsMap)
                .map(map -> map.get(year))
                .map(CardTokenData.YearsData::getMonths)
                .orElse(new HashMap<>());
        monthMap.put(month, CardTokenData.MonthsData.builder()
                .monthSum(updateMonthSum(monthMap, cardTokensPaymentInfo))
                .monthCount(updateMonthCount(monthMap, cardTokensPaymentInfo))
                .build());
        return monthMap;
    }

    private long updateMonthSum(Map<Integer, CardTokenData.MonthsData> monthMap,
                                CardTokensPaymentInfo cardTokensPaymentInfo) {
        long amount = cardTokensPaymentInfo.getAmount();
        int month = cardTokensPaymentInfo.getMonth();
        return Optional.of(monthMap)
                .map(map -> map.get(month))
                .map(CardTokenData.MonthsData::getMonthSum)
                .map(sum -> sum + amount)
                .orElse(amount);
    }

    private int updateMonthCount(Map<Integer, CardTokenData.MonthsData> monthMap,
                                 CardTokensPaymentInfo cardTokensPaymentInfo) {
        int month = cardTokensPaymentInfo.getMonth();
        return Optional.of(monthMap)
                .map(map -> map.get(month))
                .map(CardTokenData.MonthsData::getMonthCount)
                .map(count -> count + 1)
                .orElse(1);
    }

}
