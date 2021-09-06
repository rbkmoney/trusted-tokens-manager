package com.rbkmoney.trusted.tokens.validator;

import com.rbkmoney.trusted.tokens.Condition;
import com.rbkmoney.trusted.tokens.InvalidRequest;

import java.util.List;
import java.util.Objects;

import static com.rbkmoney.trusted.tokens.constants.Errors.*;

public class ConditionsValidator {

    public static void validatePaymentsConditions(List<Condition> conditions) throws InvalidRequest {
        Objects.requireNonNull(conditions, "Conditions must be set.");
        for (Condition condition : conditions) {
            requireSumNonZero(condition.getSum());
            validateRequiredFields(condition);
        }
    }

    public static void validateWithdrawalConditions(List<Condition> conditions) throws InvalidRequest {
        Objects.requireNonNull(conditions, "Conditions must be set.");
        for (Condition condition : conditions) {
            requireZero(condition.getSum());
            validateRequiredFields(condition);
        }
    }

    public static void validateRequiredFields(Condition condition) throws InvalidRequest {
        requireCountNonZero(condition.getCount());
        Objects.requireNonNull(condition.getYearsOffset(), YEARS_OFFSET_REQUIRE);
        Objects.requireNonNull(condition.getCurrencySymbolicCode(), CURRENCY_REQUIRE);
    }

    public static void requireCountNonZero(int count) throws InvalidRequest {
        if (count <= 0) {
            throw new InvalidRequest(ZERO_COUNT);
        }
    }

    public static void requireSumNonZero(long sum) throws InvalidRequest {
        if (sum <= 0) {
            throw new InvalidRequest(ZERO_SUM);
        }
    }

    public static void requireZero(long sum) throws InvalidRequest {
        if (sum > 0) {
            throw new InvalidRequest(SUM_IN_WITHDRAWAL);
        }
    }
}
