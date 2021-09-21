package com.rbkmoney.trusted.tokens.config;

import com.rbkmoney.trusted.tokens.initializer.RiakClusterStartupInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

@TestConfiguration
public class MockedStartupInitializers {

    @MockBean
    private RiakClusterStartupInitializer riakClusterStartupInitializer;

}
