package com.rbkmoney.trusted.tokens.serde.deserializer;


import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentDeserializer extends AbstractThriftDeserializer<Payment> {

    @SneakyThrows
    @Override
    public Payment deserialize(String topic, byte[] data) {
        try {
            return deserialize(data, new Payment());
        } catch (Exception e) {
            log.warn("Error when PaymentDeserializer deserialize e: ", e);
            throw e;
        }
    }

}
