package com.rbkmoney.trusted.tokens.service;

import com.rbkmoney.damsel.fraudbusters.Payment;

import java.util.List;

public interface PaymentService {

    void saveAll(List<Payment> payments);

}
