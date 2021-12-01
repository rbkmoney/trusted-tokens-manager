package com.rbkmoney.trusted.tokens.service;

import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.trusted.tokens.converter.TransactionToCardTokensPaymentInfoConverter;
import com.rbkmoney.trusted.tokens.exception.TransactionSavingException;
import com.rbkmoney.trusted.tokens.model.CardTokenData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final TransactionToCardTokensPaymentInfoConverter transactionToCardTokensPaymentInfoConverter;
    private final CardTokenService cardTokenService;

    @Override
    public void save(List<Payment> payments) {
        for (Payment payment : payments) {
            int index = payments.indexOf(payment);
            try {
                log.info("Start create row with paymentID {} status {} token {}",
                        payment.getId(),
                        payment.getStatus(),
                        payment.getPaymentTool().getBankCard().getToken());
                CardTokenData cardTokenData =
                        cardTokenService.get(payment.getPaymentTool().getBankCard().getToken());
                if (cardTokenData.getLastWithdrawalId().equals(payment.getId())) {
                    log.info("Payment with id {} already exist", payment.getId());
                    continue;
                }
                var info = transactionToCardTokensPaymentInfoConverter.convertPaymentToCardToken(payment);
                cardTokenService.addPayment(cardTokenData, info);
                cardTokenService.save(cardTokenData, info.getToken());
            } catch (Exception e) {
                throw new TransactionSavingException(e, index);
            }
        }
    }


}
