package com.rbkmoney.trusted.tokens;

import com.basho.riak.client.api.RiakClient;
import com.rbkmoney.trusted.tokens.initializer.EventSinkStreamsPool;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import javax.annotation.PreDestroy;

@ServletComponentScan
@SpringBootApplication
@RequiredArgsConstructor
public class TrustedTokensApplication extends SpringApplication {

    private final RiakClient client;
    private final EventSinkStreamsPool eventSinkStreamsPool;

    public static void main(String[] args) {
        SpringApplication.run(TrustedTokensApplication.class, args);
    }

    @PreDestroy
    public void preDestroy() {
        eventSinkStreamsPool.cleanAll();
        client.shutdown()
        ;
    }
}
