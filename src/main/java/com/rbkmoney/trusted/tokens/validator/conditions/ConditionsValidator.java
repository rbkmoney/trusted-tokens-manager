package com.rbkmoney.trusted.tokens.validator.conditions;

import com.rbkmoney.trusted.tokens.Condition;
import com.rbkmoney.trusted.tokens.InvalidRequest;

import java.util.Objects;

import static com.rbkmoney.trusted.tokens.constants.ExceptionErrors.*;

public abstract class ConditionsValidator {

    protected void validateRequiredFields(Condition condition) throws InvalidRequest {
        requireCount(condition.getCount());
        Objects.requireNonNull(condition.getYearsOffset(), YEARS_OFFSET_REQUIRE);
        Objects.requireNonNull(condition.getCurrencySymbolicCode(), CURRENCY_REQUIRE);
    }

    protected void requireCount(int count) throws InvalidRequest {
        if (count <= 0) {
            throw new InvalidRequest(COUNT_REQUIRE);
        }
    }

    protected void requireSum(long sum) throws InvalidRequest {
        if (sum <= 0) {
            throw new InvalidRequest(SUM_REQUIRE);
        }
    }

    protected void requireNonSumInWithDrawal(long sum) throws InvalidRequest {
        if (sum > 0) {
            throw new InvalidRequest(INVALID_SUM_IN_WITHDRAWAL);
        }
    }
}
