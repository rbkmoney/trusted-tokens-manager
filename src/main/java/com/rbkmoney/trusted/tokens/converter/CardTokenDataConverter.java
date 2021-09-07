package com.rbkmoney.trusted.tokens.converter;

import com.rbkmoney.trusted.tokens.model.CardToken;
import com.rbkmoney.trusted.tokens.model.CardTokenData;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CardTokenDataConverter {

    public Map<String, CardTokenData.CurrencyData> convert(
            CardToken cardToken,
            Map<String, CardTokenData.CurrencyData> currencyMap) {

        currencyMap.put(cardToken.getCurrency(), CardTokenData.CurrencyData.builder()
                .years(getYears(currencyMap, cardToken))
                .build());
        return currencyMap;
    }

    private Map<Integer, CardTokenData.YearsData> getYears(
            Map<String, CardTokenData.CurrencyData> currencyMap, CardToken cardToken) {
        String currency = cardToken.getCurrency();
        int year = cardToken.getYear();
        Map<Integer, CardTokenData.YearsData> yearsMap = Optional.of(currencyMap)
                .map(map -> map.get(currency))
                .map(CardTokenData.CurrencyData::getYears)
                .orElse(new HashMap<>());
        yearsMap.keySet().removeIf(key -> key <= year - 3);

        yearsMap.put(year, CardTokenData.YearsData.builder()
                .yearSum(getYearSum(yearsMap, cardToken))
                .yearCount(getYearCount(yearsMap, cardToken))
                .months(getMonths(yearsMap, cardToken))
                .build());
        return yearsMap;
    }

    private Map<Integer, CardTokenData.MonthsData> getMonths(
            Map<Integer, CardTokenData.YearsData> yearsMap, CardToken cardToken) {
        int year = cardToken.getYear();
        int month = cardToken.getMonth();
        Map<Integer, CardTokenData.MonthsData> monthMap = Optional.of(yearsMap)
                .map(map -> map.get(year))
                .map(CardTokenData.YearsData::getMonths)
                .orElse(new HashMap<>());

        monthMap.put(month, CardTokenData.MonthsData.builder()
                .monthSum(getMonthSum(monthMap, cardToken))
                .monthCount(getMonthCount(monthMap, cardToken))
                .build());
        return monthMap;
    }

    private long getYearSum(Map<Integer, CardTokenData.YearsData> yearsMap, CardToken cardToken) {
        long amount = cardToken.getAmount();
        int year = cardToken.getYear();
        return Optional.of(yearsMap)
                .map(map -> map.get(year))
                .map(CardTokenData.YearsData::getYearSum)
                .map(sum -> sum + amount)
                .orElse(amount);
    }

    private int getYearCount(Map<Integer, CardTokenData.YearsData> yearsMap, CardToken cardToken) {
        int year = cardToken.getYear();
        return Optional.of(yearsMap)
                .map(map -> map.get(year))
                .map(CardTokenData.YearsData::getYearCount)
                .map(count -> count + 1)
                .orElse(1);
    }

    private long getMonthSum(Map<Integer, CardTokenData.MonthsData> monthMap, CardToken cardToken) {
        long amount = cardToken.getAmount();
        int month = cardToken.getMonth();
        return Optional.of(monthMap)
                .map(map -> map.get(month))
                .map(CardTokenData.MonthsData::getMonthSum)
                .map(sum -> sum + amount)
                .orElse(amount);
    }

    private int getMonthCount(Map<Integer, CardTokenData.MonthsData> monthMap, CardToken cardToken) {
        int month = cardToken.getMonth();
        return Optional.of(monthMap)
                .map(map -> map.get(month))
                .map(CardTokenData.MonthsData::getMonthCount)
                .map(count -> count + 1)
                .orElse(1);
    }

}
