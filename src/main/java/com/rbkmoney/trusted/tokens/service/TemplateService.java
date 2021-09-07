package com.rbkmoney.trusted.tokens.service;

import com.rbkmoney.trusted.tokens.*;
import com.rbkmoney.trusted.tokens.converter.RowConverter;
import com.rbkmoney.trusted.tokens.handler.impl.TrustedTokensCommonHandler;
import com.rbkmoney.trusted.tokens.model.Row;
import com.rbkmoney.trusted.tokens.repository.TrustedTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateService {

    private final TrustedTokenRepository trustedTokenRepository;
    private final RowConverter rowConverter;
    private final List<TrustedTokensCommonHandler> handlers;
    @Value("${riak.bucket.template}")
    private String bucket;

    public void createTemplate(ConditionTemplateRequest conditionTemplateRequest)
            throws ConditionTemplateAlreadyExists {
        ConditionTemplate conditionTemplate = getConditionTemplate(conditionTemplateRequest.getName());
        if (conditionTemplate != null) {
            throw new ConditionTemplateAlreadyExists();
        }
        Row row = rowConverter.convert(conditionTemplateRequest.getName(),
                conditionTemplateRequest.getTemplate());
        trustedTokenRepository.create(row, bucket);
    }

    public boolean isTrustedTokenByTemplateName(String cardToken, String conditionTemplateName) throws TException {
        ConditionTemplate conditionTemplate = getConditionTemplate(conditionTemplateName);
        if (conditionTemplate == null) {
            throw new ConditionTemplateNotFound();
        }
        return handlers.stream()
                .filter(handler -> handler.filter(conditionTemplate))
                .findFirst()
                .orElseThrow(ConditionTemplateNotFound::new)
                .handler(cardToken, conditionTemplate);
    }

    private ConditionTemplate getConditionTemplate(String conditionTemplateName) {
        return trustedTokenRepository.get(conditionTemplateName, ConditionTemplate.class, bucket);
    }

}
