package com.rbkmoney.trusted.tokens.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.trusted.tokens.model.CardTokenData;
import com.rbkmoney.trusted.tokens.model.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CardTokenToRowConverter {

    private final ObjectMapper objectMapper;

    public Row convert(String cardToken, CardTokenData cardTokenData) {
        Row row = new Row();
        row.setKey(cardToken);
        row.setValue(initValue(cardTokenData));
        System.out.println(row);
        return row;
    }

    private String initValue(CardTokenData cardTokenData) {
        try {
            return objectMapper.writeValueAsString(cardTokenData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
