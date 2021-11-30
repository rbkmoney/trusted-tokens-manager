package com.rbkmoney.trusted.tokens.serde;

import com.rbkmoney.damsel.fraudbusters.Withdrawal;
import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import com.rbkmoney.trusted.tokens.serde.deserializer.WithdrawalDeserializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class WithdrawalSerde implements Serde<Withdrawal> {

    @Override
    public Serializer<Withdrawal> serializer() {
        return new ThriftSerializer<>();
    }

    @Override
    public Deserializer<Withdrawal> deserializer() {
        return new WithdrawalDeserializer();
    }
}
