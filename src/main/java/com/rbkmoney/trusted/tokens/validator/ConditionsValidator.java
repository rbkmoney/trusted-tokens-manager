package com.rbkmoney.trusted.tokens.validator;

import com.rbkmoney.trusted.tokens.Condition;
import com.rbkmoney.trusted.tokens.InvalidRequest;

import java.util.List;
import java.util.Objects;

import static com.rbkmoney.trusted.tokens.constants.ExceptionErrors.*;

public class ConditionsValidator {

    public static void validatePaymentsConditions(List<Condition> conditions) throws InvalidRequest {
        Objects.requireNonNull(conditions, "Conditions must be set.");
        for (Condition condition : conditions) {
            requireSum(condition.getSum());
            validateRequiredFields(condition);
        }
    }

    public static void validateWithdrawalConditions(List<Condition> conditions) throws InvalidRequest {
        Objects.requireNonNull(conditions, "Conditions must be set.");
        for (Condition condition : conditions) {
            requireNonSumInWithDrawal(condition.getSum());
            validateRequiredFields(condition);
        }
    }

    public static void validateRequiredFields(Condition condition) throws InvalidRequest {
        requireCount(condition.getCount());
        Objects.requireNonNull(condition.getYearsOffset(), YEARS_OFFSET_REQUIRE);
        Objects.requireNonNull(condition.getCurrencySymbolicCode(), CURRENCY_REQUIRE);
    }

    public static void requireCount(int count) throws InvalidRequest {
        if (count <= 0) {
            throw new InvalidRequest(COUNT_REQUIRE);
        }
    }

    public static void requireSum(long sum) throws InvalidRequest {
        if (sum <= 0) {
            throw new InvalidRequest(SUM_REQUIRE);
        }
    }

    public static void requireNonSumInWithDrawal(long sum) throws InvalidRequest {
        if (sum > 0) {
            throw new InvalidRequest(INVALID_SUM_IN_WITHDRAWAL);
        }
    }
}
