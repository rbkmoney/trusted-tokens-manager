package com.rbkmoney.trusted.tokens.handler;

import com.rbkmoney.trusted.tokens.*;
import com.rbkmoney.trusted.tokens.model.CardTokenData;
import com.rbkmoney.trusted.tokens.repository.CardTokenRepository;
import com.rbkmoney.trusted.tokens.service.TemplateService;
import com.rbkmoney.trusted.tokens.validator.ConditionTemplateValidator;
import lombok.RequiredArgsConstructor;
import org.apache.thrift.TException;
import org.springframework.stereotype.Component;

import static com.rbkmoney.trusted.tokens.calculator.ConditionTrustedResolver.isTrusted;


@Component
@RequiredArgsConstructor
public class TrustedTokensHandler implements TrustedTokensSrv.Iface {

    private final TemplateService templateService;
    private final ConditionTemplateValidator conditionTemplateValidator;
    private final CardTokenRepository cardTokenRepository;

    @Override
    public boolean isTokenTrusted(String cardToken, ConditionTemplate conditionTemplate)
            throws TException {
        conditionTemplateValidator.validate(conditionTemplate);
        CardTokenData cardTokenData = cardTokenRepository.get(cardToken);
        return isTokenTrusted(cardTokenData, conditionTemplate);
    }

    @Override
    public boolean isTokenTrustedByConditionTemplateName(String cardToken, String conditionTemplateName)
            throws TException {
        ConditionTemplate conditionTemplate = templateService.getConditionTemplate(conditionTemplateName);
        CardTokenData cardTokenData = cardTokenRepository.get(cardToken);
        return isTokenTrusted(cardTokenData, conditionTemplate);
    }

    @Override
    public void createNewConditionTemplate(ConditionTemplateRequest conditionTemplateRequest)
            throws TException {
        conditionTemplateValidator.validate(conditionTemplateRequest.getTemplate());
        templateService.createTemplate(conditionTemplateRequest);
    }

    private boolean isTokenTrusted(CardTokenData cardTokenData, ConditionTemplate conditionTemplate) {
        return cardTokenData != null
                && (isPaymentConditionTrusted(cardTokenData, conditionTemplate)
                || isWithdrawalConditionTrusted(cardTokenData, conditionTemplate));
    }

    private boolean isPaymentConditionTrusted(CardTokenData cardTokenData, ConditionTemplate conditionTemplate) {
        return conditionTemplate.getPaymentsConditions() != null
                && isTrusted(conditionTemplate.getPaymentsConditions().getConditions(),
                cardTokenData.getPayments());
    }

    private boolean isWithdrawalConditionTrusted(CardTokenData cardTokenData, ConditionTemplate conditionTemplate) {
        return conditionTemplate.getWithdrawalsConditions() != null
                && isTrusted(conditionTemplate.getWithdrawalsConditions().getConditions(),
                cardTokenData.getWithdrawals());
    }
}
