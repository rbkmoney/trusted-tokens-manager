package com.rbkmoney.trusted.tokens.calculator;

import com.rbkmoney.trusted.tokens.Condition;
import com.rbkmoney.trusted.tokens.model.CardTokenData;

import java.util.Map;

import static com.rbkmoney.trusted.tokens.calculator.YearsCountCalc.getCountYears;
import static com.rbkmoney.trusted.tokens.calculator.YearsSumCalc.getSumYears;

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
}
