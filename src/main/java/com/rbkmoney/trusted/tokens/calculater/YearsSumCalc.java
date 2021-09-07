package com.rbkmoney.trusted.tokens.calculater;

import com.rbkmoney.trusted.tokens.model.CardTokenData;

import java.time.LocalDateTime;
import java.util.Map;

public class YearsSumCalc {

    public static long getSumYears(Map<Integer, CardTokenData.YearsData> years, Integer yearsOffset) {
        Integer lastYearToCalc = LocalDateTime.now().getYear() - yearsOffset;
        return Long.sum(years.entrySet().stream()
                        .filter(year -> year.getKey() > lastYearToCalc)
                        .mapToLong(year -> year.getValue().getYearSum())
                        .sum(),
                getSumMounts(years.get(lastYearToCalc).getMonths()));
    }

    private static long getSumMounts(Map<Integer, CardTokenData.MonthsData> months) {
        Integer currentMonth = LocalDateTime.now().getMonthValue();
        return months.entrySet().stream()
                .filter(month -> month.getKey() > currentMonth)
                .mapToLong(month -> month.getValue().getMonthSum())
                .sum();
    }
}
