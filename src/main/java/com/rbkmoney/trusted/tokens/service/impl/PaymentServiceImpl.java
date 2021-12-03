package com.rbkmoney.trusted.tokens.service.impl;

import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.trusted.tokens.converter.TransactionToCardTokensTransactionInfoConverter;
import com.rbkmoney.trusted.tokens.exception.TransactionSavingException;
import com.rbkmoney.trusted.tokens.model.CardTokenData;
import com.rbkmoney.trusted.tokens.model.CardTokensTransactionInfo;
import com.rbkmoney.trusted.tokens.service.CardTokenService;
import com.rbkmoney.trusted.tokens.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final TransactionToCardTokensTransactionInfoConverter transactionInfoConverter;
    private final CardTokenService cardTokenService;

    @Override
    public void saveAll(List<Payment> payments) {
        for (Payment payment : payments) {
            int index = payments.indexOf(payment);
            try {
                String token = payment.getPaymentTool().getBankCard().getToken();
                log.info("Start create row with paymentID {} status {} token {}",
                        payment.getId(),
                        payment.getStatus(),
                        token);
                CardTokenData cardTokenData = cardTokenService.get(token);
                String lastPaymentId = cardTokenData.getLastPaymentId();
                if (isOldCardTokenData(cardTokenData, lastPaymentId)) {
                    erasePaymentData(cardTokenData, token);
                }
                if (Objects.nonNull(lastPaymentId) && lastPaymentId.equals(payment.getId())) {
                    log.info("Payment with id {} already exist", payment.getId());
                    continue;
                }
                CardTokensTransactionInfo info = transactionInfoConverter.convertPayment(payment);
                CardTokenData enrichedCardTokenData = cardTokenService.addPayment(cardTokenData, info);
                cardTokenService.save(enrichedCardTokenData, info.getToken());
            } catch (Exception e) {
                throw new TransactionSavingException(e, index);
            }
        }
    }

    private boolean isOldCardTokenData(CardTokenData cardTokenData, String lastPaymentId) {
        return Objects.isNull(lastPaymentId) && !CollectionUtils.isEmpty(cardTokenData.getPayments());
    }

    private void erasePaymentData(CardTokenData cardTokenData, String token) {
        cardTokenData.setPayments(null);
        cardTokenService.save(cardTokenData, token);
    }


}
