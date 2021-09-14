package com.rbkmoney.trusted.tokens;

import com.basho.riak.client.api.RiakClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import javax.annotation.PreDestroy;

@ServletComponentScan
@SpringBootApplication
public class TrustedTokensApplication extends SpringApplication {

    @Autowired
    private RiakClient client;

    public static void main(String[] args) {
        SpringApplication.run(TrustedTokensApplication.class, args);
    }

    @PreDestroy
    public void preDestroy() {
        client.shutdown();
    }
}
