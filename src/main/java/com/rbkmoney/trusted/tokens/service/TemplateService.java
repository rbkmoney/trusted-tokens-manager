package com.rbkmoney.trusted.tokens.service;

import com.rbkmoney.trusted.tokens.*;
import com.rbkmoney.trusted.tokens.converter.RowConverter;
import com.rbkmoney.trusted.tokens.model.Row;
import com.rbkmoney.trusted.tokens.repository.ConditionTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateService {

    private final ConditionTemplateRepository conditionTemplateRepository;
    private final RowConverter rowConverter;

    public void createTemplate(ConditionTemplateRequest conditionTemplateRequest)
            throws ConditionTemplateAlreadyExists {
        ConditionTemplate conditionTemplate = conditionTemplateRepository.get(conditionTemplateRequest.getName());
        if (conditionTemplate != null) {
            throw new ConditionTemplateAlreadyExists();
        }
        Row row = rowConverter.convert(conditionTemplateRequest.getName(),
                conditionTemplateRequest.getTemplate());
        conditionTemplateRepository.create(row);
    }

    public ConditionTemplate getConditionTemplate(String conditionTemplateName) throws TException {
        ConditionTemplate conditionTemplate = conditionTemplateRepository.get(conditionTemplateName);
        if (conditionTemplate == null) {
            throw new ConditionTemplateNotFound();
        }
        return conditionTemplate;
    }

}
