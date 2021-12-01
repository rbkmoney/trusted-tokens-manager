package com.rbkmoney.trusted.tokens.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardTokensPaymentInfo {

    public String lastPaymentId;
    public String lastWithdrawalId;
    public String token;
    public String currency;
    public long amount;
    public int year;
    public int month;
}
