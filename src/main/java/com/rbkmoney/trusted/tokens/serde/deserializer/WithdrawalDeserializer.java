package com.rbkmoney.trusted.tokens.serde.deserializer;


import com.rbkmoney.damsel.fraudbusters.Withdrawal;
import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WithdrawalDeserializer extends AbstractThriftDeserializer<Withdrawal> {

    @SneakyThrows
    @Override
    public Withdrawal deserialize(String topic, byte[] data) {
        try {
            return deserialize(data, new Withdrawal());
        } catch (Exception e) {
            log.warn("Error when WithdrawalDeserializer deserialize e: ", e);
            throw e;
        }
    }

}
