package com.rbkmoney.trusted.tokens.service;

import com.rbkmoney.damsel.fraudbusters.Withdrawal;

import java.util.List;

public interface WithdrawalService {

    void save(List<Withdrawal> withdrawals);

}
