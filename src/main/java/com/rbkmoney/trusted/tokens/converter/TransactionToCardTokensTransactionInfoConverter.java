package com.rbkmoney.trusted.tokens.converter;

import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.damsel.fraudbusters.Withdrawal;
import com.rbkmoney.trusted.tokens.model.CardTokensTransactionInfo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TransactionToCardTokensTransactionInfoConverter {

    public CardTokensTransactionInfo convertPayment(Payment payment) {
        LocalDateTime localDateTime = LocalDateTime.parse(payment.getEventTime(), DateTimeFormatter.ISO_DATE_TIME);
        return CardTokensTransactionInfo.builder()
                .token(payment.getPaymentTool().getBankCard().getToken())
                .currency(payment.getCost().getCurrency().getSymbolicCode())
                .amount(payment.getCost().getAmount())
                .year(localDateTime.getYear())
                .month(localDateTime.getMonth().getValue())
                .build();
    }

    public CardTokensTransactionInfo convertWithdrawal(Withdrawal withdrawal) {
        LocalDateTime localDateTime = LocalDateTime.parse(withdrawal.getEventTime(), DateTimeFormatter.ISO_DATE_TIME);
        return CardTokensTransactionInfo.builder()
                .token(withdrawal.getDestinationResource().getBankCard().getToken())
                .currency(withdrawal.getCost().getCurrency().getSymbolicCode())
                .year(localDateTime.getYear())
                .month(localDateTime.getMonth().getValue())
                .build();
    }

}
