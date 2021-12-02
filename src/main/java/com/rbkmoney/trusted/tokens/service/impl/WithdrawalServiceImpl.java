package com.rbkmoney.trusted.tokens.service.impl;

import com.rbkmoney.damsel.fraudbusters.Withdrawal;
import com.rbkmoney.trusted.tokens.converter.TransactionToCardTokensPaymentInfoConverter;
import com.rbkmoney.trusted.tokens.exception.TransactionSavingException;
import com.rbkmoney.trusted.tokens.model.CardTokenData;
import com.rbkmoney.trusted.tokens.service.CardTokenService;
import com.rbkmoney.trusted.tokens.service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawalServiceImpl implements WithdrawalService {

    private final TransactionToCardTokensPaymentInfoConverter transactionToCardTokensPaymentInfoConverter;
    private final CardTokenService cardTokenService;

    @Override
    public void save(List<Withdrawal> withdrawals) {
        for (Withdrawal withdrawal : withdrawals) {
            int index = withdrawals.indexOf(withdrawal);
            try {
                String token = withdrawal.getDestinationResource().getBankCard().getToken();
                log.info("WithdrawalService start create row with withdrawalID {} status {} token {}",
                        withdrawal.getId(),
                        withdrawal.getStatus(),
                        token);
                CardTokenData cardTokenData = cardTokenService.get(token);
                String lastWithdrawalId = cardTokenData.getLastWithdrawalId();
                if (Objects.isNull(lastWithdrawalId)) {
                    eraseWithdrawalData(cardTokenData, token);
                }
                if (lastWithdrawalId.equals(withdrawal.getId())) {
                    log.info("WithdrawalService withdrawal with id {} already exist", withdrawal.getId());
                    continue;
                }
                var info = transactionToCardTokensPaymentInfoConverter.convertWithdrawalToCardToken(withdrawal);
                CardTokenData enrichedCardTokenData = cardTokenService.addWithdrawal(cardTokenData, info);
                cardTokenService.save(enrichedCardTokenData, info.getToken());
            } catch (Exception e) {
                throw new TransactionSavingException(e, index);
            }
        }
    }

    private void eraseWithdrawalData(CardTokenData cardTokenData, String token) {
        cardTokenData.setWithdrawals(null);
        cardTokenService.save(cardTokenData, token);
    }
}
