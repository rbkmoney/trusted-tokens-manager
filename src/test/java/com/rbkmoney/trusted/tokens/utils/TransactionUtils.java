package com.rbkmoney.trusted.tokens.utils;

import com.rbkmoney.damsel.domain.BankCard;
import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.domain.CurrencyRef;
import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.testcontainers.annotations.util.ValuesGenerator;

import java.time.LocalDateTime;

import static com.rbkmoney.testcontainers.annotations.util.RandomBeans.randomThrift;

public class TransactionUtils {

    public static Payment createPayment() {
        return new Payment()
                .setId(ValuesGenerator.generateId())
                .setEventTime(String.valueOf(LocalDateTime.now()))
                .setReferenceInfo(ReferenceInfo.merchant_info(randomThrift(MerchantInfo.class)))
                .setPaymentTool(PaymentTool.bank_card(randomThrift(BankCard.class)))
                .setCost(new Cash()
                        .setCurrency(new CurrencyRef().setSymbolicCode("RUB"))
                        .setAmount(1000))
                .setProviderInfo(randomThrift(ProviderInfo.class))
                .setStatus(PaymentStatus.pending)
                .setClientInfo(randomThrift(ClientInfo.class));
    }

    public static Withdrawal createWithdrawal() {
        return new Withdrawal()
                .setId(ValuesGenerator.generateId())
                .setEventTime(String.valueOf(LocalDateTime.now()))
                .setDestinationResource(Resource.bank_card(randomThrift(BankCard.class)))
                .setCost(new Cash()
                        .setCurrency(new CurrencyRef().setSymbolicCode("RUB"))
                        .setAmount(1000))
                .setStatus(WithdrawalStatus.pending)
                .setAccount(randomThrift(Account.class));
    }

}
