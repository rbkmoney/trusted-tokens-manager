package com.rbkmoney.trusted.tokens.calculator;

import com.rbkmoney.trusted.tokens.Condition;
import com.rbkmoney.trusted.tokens.model.CardTokenData;

import java.util.List;
import java.util.Map;

import static com.rbkmoney.trusted.tokens.calculator.CurrencyDataTrustedResolver.isCurrencyDataTrusted;

public class ConditionTrustedResolver {

    public static boolean isTrusted(List<Condition> conditions, Map<String, CardTokenData.CurrencyData> currencies) {
        return conditions.stream().anyMatch(condition -> isCurrencyDataTrusted(currencies, condition));
    }
}
