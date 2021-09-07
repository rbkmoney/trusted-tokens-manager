package com.rbkmoney.trusted.tokens.handler.utis;

import com.rbkmoney.trusted.tokens.Condition;
import com.rbkmoney.trusted.tokens.model.CardTokenData;

import java.util.List;
import java.util.Map;

import static com.rbkmoney.trusted.tokens.handler.utis.CurrencyDataTrustedResult.isCurrencyDataTrusted;

public class ConditionTrustedResult {

    public static boolean isTrusted(List<Condition> conditions,
                                    Map<String, CardTokenData.CurrencyData> currencies) {
        boolean conditionTrusted = false;
        for (Condition condition : conditions) {
            conditionTrusted = isCurrencyDataTrusted(currencies, condition);
            if (!conditionTrusted) {
                break;
            }
        }
        return conditionTrusted;
    }
}
