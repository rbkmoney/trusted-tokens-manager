package com.rbkmoney.trusted.tokens.repository;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.cap.Quorum;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.*;
import com.basho.riak.client.core.util.BinaryValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.trusted.tokens.exception.RiakExecutionException;
import com.rbkmoney.trusted.tokens.model.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrustedTokenRepository {

    private static final String TEXT_PLAIN = "application/json";
    private final RiakClient client;
    private final ObjectMapper objectMapper;

    public <T> T get(String key, Class<T> clazz, String bucket) {
        try {
            log.info("TokenRepository get bucket: {} key: {}", bucket, key);
            Location quoteObjectLocation = createLocation(bucket, key);
            FetchValue fetch = new FetchValue.Builder(quoteObjectLocation)
                    .withOption(FetchValue.Option.R, Quorum.quorumQuorum())
                    .build();
            FetchValue.Response response = client.execute(fetch);
            RiakObject obj = response.getValue(RiakObject.class);
            return obj != null && obj.getValue() != null
                    ? objectMapper.readValue(obj.getValue().toString(), clazz)
                    : clazz.getDeclaredConstructor().newInstance();
        } catch (InterruptedException e) {
            log.error("InterruptedException in TokenRepository when get e: ", e);
            Thread.currentThread().interrupt();
            throw new RiakExecutionException(e);
        } catch (Exception e) {
            log.error("Exception in TokenRepository when get e: ", e);
            throw new RiakExecutionException(e);
        }
    }

    public void create(Row row, String bucket) {
        try {
            log.debug("Repository create in bucket: {} row: {}", bucket, row);
            RiakObject quoteObject = new RiakObject()
                    .setContentType(TEXT_PLAIN)
                    .setValue(BinaryValue.create(row.getValue()));
            Location quoteObjectLocation = createLocation(bucket, row.getKey());
            StoreValue storeOp = new StoreValue.Builder(quoteObject)
                    .withOption(StoreValue.Option.W, Quorum.oneQuorum())
                    .withLocation(quoteObjectLocation)
                    .build();
            client.execute(storeOp);
        } catch (InterruptedException e) {
            log.error("InterruptedException in Repository when create e: ", e);
            Thread.currentThread().interrupt();
            throw new RiakExecutionException(e);
        } catch (Exception e) {
            log.error("Exception in Repository when create e: ", e);
            throw new RiakExecutionException();
        }
    }


    private Location createLocation(String bucketName, String key) {
        Namespace quotesBucket = new Namespace(bucketName);
        return new Location(quotesBucket, key);
    }
}
