package com.rbkmoney.trusted.tokens.serde;

import com.rbkmoney.machinegun.eventsink.SinkEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.util.Map;

@Slf4j
public class SinkEventDeserializer implements Deserializer<SinkEvent> {

    ThreadLocal<TDeserializer> deserializerThreadLocal =
            ThreadLocal.withInitial(() -> new TDeserializer(new TBinaryProtocol.Factory()));

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public SinkEvent deserialize(String topic, byte[] data) {
        SinkEvent sinkEvent = new SinkEvent();
        try {
            deserializerThreadLocal.get().deserialize(sinkEvent, data);
        } catch (Exception e) {
            log.error("Error when deserialize machine event data: {} ", data, e);
        }
        return sinkEvent;
    }

    @Override
    public void close() {

    }

}