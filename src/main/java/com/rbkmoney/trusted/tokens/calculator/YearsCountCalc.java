package com.rbkmoney.trusted.tokens.calculator;

import com.rbkmoney.trusted.tokens.model.CardTokenData;

import java.time.LocalDateTime;
import java.util.Map;

public class YearsCountCalc {

    public static int getCountYears(Map<Integer, CardTokenData.YearsData> years, Integer yearsOffset) {
        Integer lastYearToCalc = LocalDateTime.now().getYear() - yearsOffset;
        return calculateCountFullYears(years, lastYearToCalc) + calculateCountMonths(years, lastYearToCalc);
    }

    private static int calculateCountMonths(Map<Integer, CardTokenData.YearsData> years, Integer lastYearToCalc) {
        CardTokenData.YearsData yearsData = years.get(lastYearToCalc);
        if (yearsData == null) {
            return 0;
        }
        return getCountMonths(yearsData.getMonths());
    }

    private static int calculateCountFullYears(Map<Integer, CardTokenData.YearsData> years, Integer lastYearToCalc) {
        return years.entrySet().stream()
                .filter(year -> year.getKey() > lastYearToCalc)
                .mapToInt(year -> year.getValue().getYearCount())
                .sum();
    }

    private static int getCountMonths(Map<Integer, CardTokenData.MonthsData> months) {
        Integer currentMonth = LocalDateTime.now().getMonthValue();
        return months.entrySet().stream()
                .filter(month -> month.getKey() > currentMonth)
                .mapToInt(month -> month.getValue().getMonthCount())
                .sum();
    }
}
