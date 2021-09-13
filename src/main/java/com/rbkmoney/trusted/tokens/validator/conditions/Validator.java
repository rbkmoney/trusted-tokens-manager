package com.rbkmoney.trusted.tokens.validator.conditions;

import com.rbkmoney.trusted.tokens.Condition;
import com.rbkmoney.trusted.tokens.InvalidRequest;

import java.util.List;

public interface Validator {

    void validateConditions(List<Condition> conditions) throws InvalidRequest;

}
