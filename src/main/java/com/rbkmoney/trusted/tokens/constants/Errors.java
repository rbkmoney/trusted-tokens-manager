package com.rbkmoney.trusted.tokens.constants;

import java.util.Collections;
import java.util.List;

public class Errors {
    public static final List<String> INVALID_REQUEST_CONDITIONS = Collections.singletonList(
            "Must be set one of the conditions: payments_conditions or withdrawals_conditions.");
    public static final List<String> ZERO_COUNT = Collections.singletonList(
            "Count must be set and greater than zero.");
    public static final List<String> ZERO_SUM = Collections.singletonList(
            "Sum must be greater than zero.");
    public static final List<String> SUM_IN_WITHDRAWAL = Collections.singletonList(
            "Sum in withdrawal condition must not be set.");
    public static final String YEARS_OFFSET_REQUIRE = "YearsOffset must be set.";
    public static final String CURRENCY_REQUIRE = "CurrencySymbolicCode must be set.";
}
