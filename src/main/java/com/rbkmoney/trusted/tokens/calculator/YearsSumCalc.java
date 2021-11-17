package com.rbkmoney.trusted.tokens.calculator;

import com.rbkmoney.trusted.tokens.model.CardTokenData;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class YearsSumCalc {

    public static long getSumYears(Map<Integer, CardTokenData.YearsData> years, Integer yearsOffset) {
        Integer lastYearToCalc = LocalDateTime.now().getYear() - yearsOffset;
        return calculateFullYearsSum(years, lastYearToCalc) + calculateMonthSum(years, lastYearToCalc);
    }

    private static long calculateMonthSum(Map<Integer, CardTokenData.YearsData> years, Integer lastYearToCalc) {
        CardTokenData.YearsData yearsData = years.get(lastYearToCalc);
        if (yearsData == null) {
            return 0;
        }
        return getSumMonths(yearsData.getMonths());
    }

    private static long calculateFullYearsSum(Map<Integer, CardTokenData.YearsData> years, Integer lastYearToCalc) {
        return years.entrySet().stream()
                .filter(year -> year.getKey() > lastYearToCalc)
                .mapToLong(year -> year.getValue().getYearSum())
                .sum();
    }

    private static long getSumMonths(Map<Integer, CardTokenData.MonthsData> months) {
        Integer currentMonth = LocalDateTime.now().getMonthValue();
        return months.entrySet().stream()
                .filter(month -> month.getKey() > currentMonth)
                .mapToLong(month -> month.getValue().getMonthSum())
                .sum();
    }
}
