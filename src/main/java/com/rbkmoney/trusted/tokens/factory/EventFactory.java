package com.rbkmoney.trusted.tokens.factory;

import com.rbkmoney.trusted.tokens.constants.StreamType;
import org.apache.kafka.streams.KafkaStreams;

public interface EventFactory {

    StreamType getType();

    KafkaStreams create();
}
