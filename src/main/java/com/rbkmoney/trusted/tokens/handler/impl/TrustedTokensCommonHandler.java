package com.rbkmoney.trusted.tokens.handler.impl;

import com.rbkmoney.trusted.tokens.ConditionTemplate;

public interface TrustedTokensCommonHandler {

    boolean filter(ConditionTemplate conditionTemplate);

    boolean handler(String cardToken, ConditionTemplate conditionTemplate);
}
