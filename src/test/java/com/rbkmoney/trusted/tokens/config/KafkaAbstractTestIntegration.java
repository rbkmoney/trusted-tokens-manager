package com.rbkmoney.trusted.tokens.config;

import com.rbkmoney.trusted.tokens.TrustedTokensApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@SpringBootTest
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = TrustedTokensApplication.class,
        initializers = KafkaAbstractTestIntegration.Initializer.class)
public abstract class KafkaAbstractTestIntegration {

    private static final String IMAGE_NAME = "confluentinc/cp-kafka";
    private static final String PLATFORM_VERSION = "6.1.2";

    @Container
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse(IMAGE_NAME)
            .withTag(PLATFORM_VERSION))
            .withEmbeddedZookeeper()
            .withStartupTimeout(Duration.ofMinutes(2));


    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues
                    .of("kafka.bootstrap.servers=" + kafka.getBootstrapServers())
                    .applyTo(configurableApplicationContext);
        }
    }

}
