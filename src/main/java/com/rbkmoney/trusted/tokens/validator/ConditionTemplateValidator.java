package com.rbkmoney.trusted.tokens.validator;

import com.rbkmoney.trusted.tokens.ConditionTemplate;
import com.rbkmoney.trusted.tokens.InvalidRequest;
import com.rbkmoney.trusted.tokens.validator.conditions.PaymentsConditionsValidator;
import com.rbkmoney.trusted.tokens.validator.conditions.WithdrawalConditionsValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.rbkmoney.trusted.tokens.constants.ExceptionErrors.INVALID_REQUEST_CONDITIONS;

@Component
@RequiredArgsConstructor
public class ConditionTemplateValidator {

    private final PaymentsConditionsValidator paymentsConditionsValidator;
    private final WithdrawalConditionsValidator withdrawalConditionsValidator;

    public void validate(ConditionTemplate conditionTemplate) throws InvalidRequest {

        if (!conditionTemplate.isSetPaymentsConditions() && !conditionTemplate.isSetWithdrawalsConditions()) {
            throw new InvalidRequest(INVALID_REQUEST_CONDITIONS);
        } else if (conditionTemplate.isSetPaymentsConditions()) {
            paymentsConditionsValidator.validateConditions(
                    conditionTemplate.getPaymentsConditions().getConditions());
        } else {
            withdrawalConditionsValidator.validateConditions(
                    conditionTemplate.getWithdrawalsConditions().getConditions());
        }
    }
}
