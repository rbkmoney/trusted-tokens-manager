package com.rbkmoney.trusted.tokens.validator.conditions;

import com.rbkmoney.trusted.tokens.Condition;
import com.rbkmoney.trusted.tokens.InvalidRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class PaymentsConditionsValidator extends ConditionsValidator implements Validator {

    @Override
    public void validateConditions(List<Condition> conditions) throws InvalidRequest {
        Objects.requireNonNull(conditions, "Conditions must be set.");
        for (Condition condition : conditions) {
            validateRequiredFields(condition);
        }
    }
}
