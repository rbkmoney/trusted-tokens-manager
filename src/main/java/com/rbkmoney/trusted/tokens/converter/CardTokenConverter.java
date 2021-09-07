package com.rbkmoney.trusted.tokens.converter;

import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.damsel.fraudbusters.Withdrawal;
import com.rbkmoney.trusted.tokens.model.CardToken;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CardTokenConverter {

    public CardToken convertPaymentToCardToken(Payment payment) {
        LocalDateTime localDateTime = LocalDateTime.parse(payment.getEventTime());
        return CardToken.builder()
                .currency(payment.getCost().getCurrency().getSymbolicCode())
                .amount(payment.getCost().getAmount())
                .year(localDateTime.getYear())
                .month(localDateTime.getMonth().getValue())
                .build();
    }

    public CardToken convertWithdrawalToCardToken(Withdrawal withdrawal) {
        LocalDateTime localDateTime = LocalDateTime.parse(withdrawal.getEventTime());
        return CardToken.builder()
                .currency(withdrawal.getCost().getCurrency().getSymbolicCode())
                .year(localDateTime.getYear())
                .month(localDateTime.getMonth().getValue())
                .build();
    }

}
