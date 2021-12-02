package com.rbkmoney.trusted.tokens.service;

import com.rbkmoney.trusted.tokens.model.CardTokenData;
import com.rbkmoney.trusted.tokens.model.CardTokensPaymentInfo;

public interface CardTokenService {

    void save(CardTokenData cardTokenData, String token);

    CardTokenData get(String token);

    CardTokenData addWithdrawal(CardTokenData cardTokenData, CardTokensPaymentInfo cardTokensPaymentInfo);

    CardTokenData addPayment(CardTokenData cardTokenData, CardTokensPaymentInfo cardTokensPaymentInfo);


}
