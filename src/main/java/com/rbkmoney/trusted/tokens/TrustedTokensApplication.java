package com.rbkmoney.trusted.tokens;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class TrustedTokensApplication extends SpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrustedTokensApplication.class, args);
    }
}
