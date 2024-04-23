package com.mawuli.sns.security.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum EmailTemplateName {
    ACTIVATION_ACCOUNT("activation-account")

    ;

    private final String templateName;

    EmailTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
