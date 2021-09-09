package com.rbkmoney.trusted.tokens.repository;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.cap.Quorum;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.*;
import com.basho.riak.client.core.util.BinaryValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.trusted.tokens.exception.RiakExecutionException;
import com.rbkmoney.trusted.tokens.model.CardTokenData;
import com.rbkmoney.trusted.tokens.model.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardTokenRepository {

    private static final String APPLICATION_JSON = "application/json";
    private final RiakClient client;
    private final ObjectMapper objectMapper;

    @Value("${riak.bucket.token}")
    private String bucket;

    public CardTokenData get(String key) {
        try {
            log.info("CardTokenRepository get bucket: {} key: {}", bucket, key);
            Location quoteObjectLocation = createLocation(bucket, key);
            FetchValue fetch = new FetchValue.Builder(quoteObjectLocation)
                    .withOption(FetchValue.Option.R, Quorum.quorumQuorum())
                    .build();
            FetchValue.Response response = client.execute(fetch);
            RiakObject obj = response.getValue(RiakObject.class);
            return obj != null && obj.getValue() != null
                    ? objectMapper.readValue(obj.getValue().toString(), CardTokenData.class)
                    : null;
        } catch (InterruptedException e) {
            log.error("InterruptedException in CardTokenRepository when get e: ", e);
            Thread.currentThread().interrupt();
            throw new RiakExecutionException(e);
        } catch (Exception e) {
            log.error("Exception in CardTokenRepository when get e: ", e);
            throw new RiakExecutionException(e);
        }
    }

    public void create(Row row) {
        try {
            log.debug("CardTokenRepository create in row: {}", row);
            RiakObject quoteObject = new RiakObject()
                    .setContentType(APPLICATION_JSON)
                    .setValue(BinaryValue.create(row.getValue()));
            Location quoteObjectLocation = createLocation(bucket, row.getKey());
            StoreValue storeOp = new StoreValue.Builder(quoteObject)
                    .withOption(StoreValue.Option.W, Quorum.oneQuorum())
                    .withLocation(quoteObjectLocation)
                    .build();
            client.execute(storeOp);
        } catch (InterruptedException e) {
            log.error("InterruptedException in CardTokenRepository when create e: ", e);
            Thread.currentThread().interrupt();
            throw new RiakExecutionException(e);
        } catch (Exception e) {
            log.error("Exception in CardTokenRepository when create e: ", e);
            throw new RiakExecutionException();
        }
    }


    private Location createLocation(String bucketName, String key) {
        Namespace quotesBucket = new Namespace(bucketName);
        return new Location(quotesBucket, key);
    }
}
