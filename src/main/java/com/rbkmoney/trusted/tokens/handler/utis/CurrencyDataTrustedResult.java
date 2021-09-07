package com.rbkmoney.trusted.tokens.handler.utis;

import com.rbkmoney.trusted.tokens.Condition;
import com.rbkmoney.trusted.tokens.model.CardTokenData;

import java.time.LocalDateTime;
import java.util.Map;

public class CurrencyDataTrustedResult {

    public static boolean isCurrencyDataTrusted(Map<String, CardTokenData.CurrencyData> currencies,
                                                Condition condition) {
        Integer yearsOffset = condition.getYearsOffset().getValue();
        for (String currency : currencies.keySet()) {
            if (currency.equals(condition.getCurrencySymbolicCode())) {
                long sum = getSumYears(currencies.get(currency).getYears(), yearsOffset);
                int count = getCountYears(currencies.get(currency).getYears(), yearsOffset);
                if (sum > condition.getSum() && count > condition.getCount()) {
                    return true;
                } else if (sum == 0 && count > condition.getCount()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static long getSumYears(Map<Integer, CardTokenData.YearsData> years, Integer yearsOffset) {
        Integer lastYearToCalc = LocalDateTime.now().getYear() - yearsOffset;
        return Long.sum(years.entrySet().stream()
                        .filter(year -> year.getKey() > lastYearToCalc)
                        .mapToLong(year -> year.getValue().getYearSum())
                        .sum(),
                getSumMounts(years.get(lastYearToCalc).getMonths()));
    }

    private static int getCountYears(Map<Integer, CardTokenData.YearsData> years, Integer yearsOffset) {
        Integer lastYearToCalc = LocalDateTime.now().getYear() - yearsOffset;
        return Integer.sum(years.entrySet().stream()
                        .filter(year -> year.getKey() > lastYearToCalc)
                        .mapToInt(year -> year.getValue().getYearCount())
                        .sum(),
                getCountMounts(years.get(lastYearToCalc).getMonths()));
    }

    private static long getSumMounts(Map<Integer, CardTokenData.MonthsData> months) {
        Integer currentMonth = LocalDateTime.now().getMonthValue();
        return months.entrySet().stream()
                .filter(month -> month.getKey() > currentMonth)
                .mapToLong(month -> month.getValue().getMonthSum())
                .sum();
    }

    private static int getCountMounts(Map<Integer, CardTokenData.MonthsData> months) {
        Integer currentMonth = LocalDateTime.now().getMonthValue();
        return months.entrySet().stream()
                .filter(month -> month.getKey() > currentMonth)
                .mapToInt(month -> month.getValue().getMonthCount())
                .sum();
    }
}
