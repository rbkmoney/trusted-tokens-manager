package com.rbkmoney.trusted.tokens.service;

import com.rbkmoney.trusted.tokens.model.CardTokenData;
import com.rbkmoney.trusted.tokens.model.CardTokensTransactionInfo;

public interface CardTokenService {

    void save(CardTokenData cardTokenData, String token);

    CardTokenData get(String token);

    CardTokenData addWithdrawal(CardTokenData cardTokenData, CardTokensTransactionInfo cardTokensTransactionInfo);

    CardTokenData addPayment(CardTokenData cardTokenData, CardTokensTransactionInfo cardTokensTransactionInfo);


}
