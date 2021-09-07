package com.rbkmoney.trusted.tokens.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.trusted.tokens.ConditionTemplate;
import com.rbkmoney.trusted.tokens.model.CardTokenData;
import com.rbkmoney.trusted.tokens.model.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class RowConverter {

    private final ObjectMapper objectMapper;

    public Row convert(String conditionTemplateName, ConditionTemplate conditionTemplate) {
        try {
            Row row = new Row();
            row.setKey(conditionTemplateName);
            row.setValue(objectMapper.writeValueAsString(conditionTemplate));
            return row;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Row convert(String cardToken, CardTokenData cardTokenData) {
        try {
            Row row = new Row();
            row.setKey(cardToken);
            row.setValue(objectMapper.writeValueAsString(cardTokenData));
            return row;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
