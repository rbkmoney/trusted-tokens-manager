package com.rbkmoney.trusted.tokens.serde;

import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import com.rbkmoney.trusted.tokens.serde.deserializer.PaymentDeserializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class PaymentSerde implements Serde<Payment> {

    @Override
    public Serializer<Payment> serializer() {
        return new ThriftSerializer<>();
    }

    @Override
    public Deserializer<Payment> deserializer() {
        return new PaymentDeserializer();
    }
}
