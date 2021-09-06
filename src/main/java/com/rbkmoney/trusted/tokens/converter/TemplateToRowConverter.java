package com.rbkmoney.trusted.tokens.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.trusted.tokens.ConditionTemplate;
import com.rbkmoney.trusted.tokens.model.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TemplateToRowConverter {

    private final ObjectMapper objectMapper;

    public Row convert(String conditionTemplateName, ConditionTemplate conditionTemplate) {
        Row row = new Row();
        row.setKey(conditionTemplateName);
        row.setValue(initValue(conditionTemplate));
        return row;
    }

    private String initValue(ConditionTemplate conditionTemplate) {
        try {
            return objectMapper.writeValueAsString(conditionTemplate);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
