package com.rbkmoney.trusted.tokens.riak;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.core.query.*;
import com.rbkmoney.trusted.tokens.config.RiakAbstractTestIntegration;
import com.rbkmoney.trusted.tokens.config.RiakConfig;
import com.rbkmoney.trusted.tokens.model.CardTokenData;
import com.rbkmoney.trusted.tokens.model.Row;
import com.rbkmoney.trusted.tokens.repository.TrustedTokenRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.ExecutionException;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ContextConfiguration(classes = {TrustedTokenRepository.class, RiakConfig.class})
class RiakTest extends RiakAbstractTestIntegration {

    private static final String VALUE = "{\"squadName\": \"Super hero squad\"}";
    private static final String KEY = "key";

    @Value("${riak.bucket.token}")
    String tokenBucketName;

    @Autowired
    TrustedTokenRepository trustedTokenRepository;

    @Autowired
    RiakClient client;

    @Test
    void riakTest() throws InterruptedException, ExecutionException {
        sleep(10000);

        Row row = new Row();
        row.setKey(KEY);
        row.setValue(VALUE);
        trustedTokenRepository.create(row, tokenBucketName);

        Namespace ns = new Namespace(tokenBucketName);
        Location location = new Location(ns, KEY);
        FetchValue fv = new FetchValue.Builder(location).build();
        FetchValue.Response response = client.execute(fv);
        RiakObject obj = response.getValue(RiakObject.class);

        String result = obj.getValue().toString();
        Assertions.assertEquals(VALUE, result);

        CardTokenData resultGet = trustedTokenRepository.get(KEY, CardTokenData.class, tokenBucketName);
        assertNull(resultGet);
        Assertions.assertEquals(VALUE, null);
    }

}
