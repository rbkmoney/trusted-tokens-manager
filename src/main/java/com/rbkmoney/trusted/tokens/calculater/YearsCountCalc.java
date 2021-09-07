package com.rbkmoney.trusted.tokens.calculater;

import com.rbkmoney.trusted.tokens.model.CardTokenData;

import java.time.LocalDateTime;
import java.util.Map;

public class YearsCountCalc {

    public static int getCountYears(Map<Integer, CardTokenData.YearsData> years, Integer yearsOffset) {
        Integer lastYearToCalc = LocalDateTime.now().getYear() - yearsOffset;
        return Integer.sum(years.entrySet().stream()
                        .filter(year -> year.getKey() > lastYearToCalc)
                        .mapToInt(year -> year.getValue().getYearCount())
                        .sum(),
                getCountMounts(years.get(lastYearToCalc).getMonths()));
    }

    private static int getCountMounts(Map<Integer, CardTokenData.MonthsData> months) {
        Integer currentMonth = LocalDateTime.now().getMonthValue();
        return months.entrySet().stream()
                .filter(month -> month.getKey() > currentMonth)
                .mapToInt(month -> month.getValue().getMonthCount())
                .sum();
    }
}
