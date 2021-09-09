package com.rbkmoney.trusted.tokens.calculator;

import com.rbkmoney.trusted.tokens.Condition;
import com.rbkmoney.trusted.tokens.ConditionTemplate;
import com.rbkmoney.trusted.tokens.model.CardTokenData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.rbkmoney.trusted.tokens.calculator.CurrencyDataTrustedResolver.isCurrencyDataTrusted;

@Component
public class ConditionTrustedResolver {

    public boolean isTrusted(CardTokenData cardTokenData, ConditionTemplate conditionTemplate) {
        return cardTokenData != null
                && (isPaymentConditionTrusted(cardTokenData, conditionTemplate)
                || isWithdrawalConditionTrusted(cardTokenData, conditionTemplate));
    }

    private boolean isPaymentConditionTrusted(CardTokenData cardTokenData, ConditionTemplate conditionTemplate) {
        return conditionTemplate.getPaymentsConditions() != null
                && isConditionsTrusted(conditionTemplate.getPaymentsConditions().getConditions(),
                cardTokenData.getPayments());
    }

    private boolean isWithdrawalConditionTrusted(CardTokenData cardTokenData,
                                                 ConditionTemplate conditionTemplate) {
        return conditionTemplate.getWithdrawalsConditions() != null
                && isConditionsTrusted(conditionTemplate.getWithdrawalsConditions().getConditions(),
                cardTokenData.getWithdrawals());
    }

    private boolean isConditionsTrusted(List<Condition> conditions,
                                        Map<String, CardTokenData.CurrencyData> currencies) {
        return conditions.stream().anyMatch(condition -> isCurrencyDataTrusted(currencies, condition));
    }
}
