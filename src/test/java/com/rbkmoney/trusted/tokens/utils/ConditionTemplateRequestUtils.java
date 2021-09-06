package com.rbkmoney.trusted.tokens.utils;

import com.rbkmoney.trusted.tokens.*;

import java.util.Collections;
import java.util.List;

import static com.rbkmoney.trusted.tokens.utils.ConditionTemplateUtils.PAYMENT;
import static com.rbkmoney.trusted.tokens.utils.ConditionTemplateUtils.WITHDRAWAL;

public class ConditionTemplateRequestUtils {

    public static final String CONDITION_NAME = "condition";

    public static ConditionTemplateRequest createTemplateRequestWithTwoConditions() {
        return new ConditionTemplateRequest()
                .setName(CONDITION_NAME)
                .setTemplate(new ConditionTemplate()
                        .setPaymentsConditions(new PaymentsConditions()
                                .setConditions(createConditions(PAYMENT)))
                        .setWithdrawalsConditions(new WithdrawalsConditions()
                                .setConditions(createConditions(WITHDRAWAL))));
    }

    public static ConditionTemplateRequest createTrueTemplateRequest() {
        return new ConditionTemplateRequest()
                .setName(CONDITION_NAME)
                .setTemplate(new ConditionTemplate()
                        .setPaymentsConditions(new PaymentsConditions()
                                .setConditions(createConditions(PAYMENT))));
    }

    public static List<Condition> createConditions(String type) {
        return Collections.singletonList(new Condition()
                .setCurrencySymbolicCode("RUB")
                .setYearsOffset(YearsOffset.current_with_two_last_years)
                .setCount(2)
                .setSum(PAYMENT.equals(type) ? 2000 : 0));
    }

    public static ConditionTemplateRequest createFalseTemplateRequest() {
        return new ConditionTemplateRequest()
                .setName(CONDITION_NAME)
                .setTemplate(new ConditionTemplate()
                        .setPaymentsConditions(new PaymentsConditions()
                                .setConditions(createFalseConditions(PAYMENT))));
    }

    public static List<Condition> createFalseConditions(String type) {
        return Collections.singletonList(new Condition()
                .setCurrencySymbolicCode("RUB")
                .setYearsOffset(YearsOffset.current_with_two_last_years)
                .setCount(80)
                .setSum(PAYMENT.equals(type) ? 80000 : 0));
    }

    public static ConditionTemplateRequest createTemplatePaymentRequestWithNullYearsOffset() {
        return new ConditionTemplateRequest()
                .setName(CONDITION_NAME)
                .setTemplate(new ConditionTemplate()
                        .setPaymentsConditions(new PaymentsConditions()
                                .setConditions(createConditionsWithNullYearsOffset())));
    }

    public static List<Condition> createConditionsWithNullYearsOffset() {
        return Collections.singletonList(new Condition()
                .setCurrencySymbolicCode("RUB")
                .setCount(2)
                .setSum(2000));
    }

    public static ConditionTemplateRequest createTemplatePaymentRequestWithNullCurrency() {
        return new ConditionTemplateRequest()
                .setName(CONDITION_NAME)
                .setTemplate(new ConditionTemplate()
                        .setPaymentsConditions(new PaymentsConditions()
                                .setConditions(createConditionsWithNullCurrency())));
    }

    public static List<Condition> createConditionsWithNullCurrency() {
        return Collections.singletonList(new Condition()
                .setYearsOffset(YearsOffset.current_with_two_last_years)
                .setCount(2)
                .setSum(2000));
    }

    public static ConditionTemplateRequest createTemplatePaymentRequestWithNullSum() {
        return new ConditionTemplateRequest()
                .setName(CONDITION_NAME)
                .setTemplate(new ConditionTemplate()
                        .setPaymentsConditions(new PaymentsConditions()
                                .setConditions(createConditionsWithNullSum())));
    }

    public static List<Condition> createConditionsWithNullSum() {
        return Collections.singletonList(new Condition()
                .setCurrencySymbolicCode("RUB")
                .setYearsOffset(YearsOffset.current_with_two_last_years)
                .setCount(2));
    }

    public static ConditionTemplateRequest createTemplatePaymentRequestWithNullCount() {
        return new ConditionTemplateRequest()
                .setName(CONDITION_NAME)
                .setTemplate(new ConditionTemplate()
                        .setPaymentsConditions(new PaymentsConditions()
                                .setConditions(createConditionsWithNullCount())));
    }

    public static List<Condition> createConditionsWithNullCount() {
        return Collections.singletonList(new Condition()
                .setCurrencySymbolicCode("RUB")
                .setYearsOffset(YearsOffset.current_with_two_last_years)
                .setSum(2000));
    }
}
