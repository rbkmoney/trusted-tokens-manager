package com.rbkmoney.trusted.tokens.validator;

import com.rbkmoney.trusted.tokens.ConditionTemplate;
import com.rbkmoney.trusted.tokens.InvalidRequest;
import org.springframework.stereotype.Component;

import static com.rbkmoney.trusted.tokens.constants.Errors.INVALID_REQUEST_CONDITIONS;
import static com.rbkmoney.trusted.tokens.validator.ConditionsValidator.validatePaymentsConditions;
import static com.rbkmoney.trusted.tokens.validator.ConditionsValidator.validateWithdrawalConditions;

@Component
public class ConditionTemplateValidator {

    public void validate(ConditionTemplate conditionTemplate) throws InvalidRequest {

        if ((conditionTemplate.isSetPaymentsConditions()
                && conditionTemplate.isSetWithdrawalsConditions())
                || (!conditionTemplate.isSetPaymentsConditions()
                && !conditionTemplate.isSetWithdrawalsConditions())) {
            throw new InvalidRequest(INVALID_REQUEST_CONDITIONS);
        } else if (conditionTemplate.isSetPaymentsConditions()) {
            validatePaymentsConditions(conditionTemplate.getPaymentsConditions().getConditions());
        } else {
            validateWithdrawalConditions(conditionTemplate.getWithdrawalsConditions().getConditions());
        }
    }
}
