package com.rbkmoney.trusted.tokens.service;

import com.rbkmoney.damsel.fraudbusters.Withdrawal;
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
public class WithdrawalServiceImpl implements WithdrawalService {

    private final TransactionToCardTokensPaymentInfoConverter transactionToCardTokensPaymentInfoConverter;
    private final CardTokenService cardTokenService;

    @Override
    public void save(List<Withdrawal> withdrawals) {
        for (Withdrawal withdrawal : withdrawals) {
            int index = withdrawals.indexOf(withdrawal);
            try {
                log.info("WithdrawalService start create row with withdrawalID {} status {} token {}",
                        withdrawal.getId(),
                        withdrawal.getStatus(),
                        withdrawal.getDestinationResource().getBankCard().getToken());
                CardTokenData cardTokenData =
                        cardTokenService.get(withdrawal.getDestinationResource().getBankCard().getToken());
                if (cardTokenData.getLastWithdrawalId().equals(withdrawal.getId())) {
                    log.info("WithdrawalService withdrawal with id {} already exist", withdrawal.getId());
                    continue;
                }
                var info = transactionToCardTokensPaymentInfoConverter.convertWithdrawalToCardToken(withdrawal);
                cardTokenService.addWithdrawal(cardTokenData, info);
                cardTokenService.save(cardTokenData, info.getToken());
            } catch (Exception e) {
                throw new TransactionSavingException(e, index);
            }
        }
    }


}
