package com.rbkmoney.trusted.tokens.handler.utis;

import com.rbkmoney.trusted.tokens.Condition;
import com.rbkmoney.trusted.tokens.model.CardTokenData;

import java.util.List;
import java.util.Map;

import static com.rbkmoney.trusted.tokens.handler.utis.CurrencyCheck.isCurrencyTrusted;

public class TrustedCheck {

    public static boolean isTrusted(List<Condition> conditions,
                                    Map<String, CardTokenData.CurrencyData> currencies) {
        boolean trustedCheck = false;
        for (Condition condition : conditions) {
            trustedCheck = isCurrencyTrusted(currencies, condition);
            if (!trustedCheck) {
                break;
            }
        }
        return trustedCheck;
    }
}
