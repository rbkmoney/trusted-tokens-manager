package com.rbkmoney.trusted.tokens.handler;

import com.rbkmoney.trusted.tokens.*;
import com.rbkmoney.trusted.tokens.handler.impl.TrustedTokensCommonHandler;
import com.rbkmoney.trusted.tokens.service.TemplateService;
import com.rbkmoney.trusted.tokens.validator.ConditionTemplateValidator;
import lombok.RequiredArgsConstructor;
import org.apache.thrift.TException;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.rbkmoney.trusted.tokens.constants.Errors.INVALID_REQUEST_CONDITIONS;


@Component
@RequiredArgsConstructor
public class TrustedTokensHandler implements TrustedTokensSrv.Iface {

    private final List<TrustedTokensCommonHandler> handlers;
    private final TemplateService templateService;
    private final ConditionTemplateValidator conditionTemplateValidator;

    @Override
    public boolean isTokenTrusted(String cardToken, ConditionTemplate conditionTemplate)
            throws TException {
        conditionTemplateValidator.validate(conditionTemplate);
        return handlers.stream()
                .filter(handler -> handler.filter(conditionTemplate))
                .findFirst()
                .orElseThrow(() -> new InvalidRequest(INVALID_REQUEST_CONDITIONS))
                .handler(cardToken, conditionTemplate);
    }

    @Override
    public boolean isTokenTrustedByConditionTemplateName(String cardToken, String conditionTemplateName)
            throws TException {
        return templateService.isTrustedTokenByTemplateName(cardToken, conditionTemplateName);
    }

    @Override
    public void createNewConditionTemplate(ConditionTemplateRequest conditionTemplateRequest)
            throws TException {
        conditionTemplateValidator.validate(conditionTemplateRequest.getTemplate());
        templateService.createTemplate(conditionTemplateRequest);
    }
}
