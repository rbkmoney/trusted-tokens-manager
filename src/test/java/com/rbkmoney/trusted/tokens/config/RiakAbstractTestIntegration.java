package com.rbkmoney.trusted.tokens.config;

import com.rbkmoney.trusted.tokens.TrustedTokensApplication;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;


@ContextConfiguration(classes = TrustedTokensApplication.class,
        initializers = RiakAbstractTestIntegration.Initializer.class)
@Testcontainers
public abstract class RiakAbstractTestIntegration extends KafkaAbstractTestIntegration {

    private static final String IMAGE_NAME = "basho/riak-kv";

    @Container
    public static GenericContainer riak = new GenericContainer(IMAGE_NAME)
            .withExposedPorts(8098, 8087)
            .withPrivilegedMode(true)
            .waitingFor(new WaitAllStrategy()
                    .withStartupTimeout(Duration.ofMinutes(2)));

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues
                    .of("riak.port=" + riak.getMappedPort(8087))
                    .applyTo(configurableApplicationContext);
        }
    }

}
