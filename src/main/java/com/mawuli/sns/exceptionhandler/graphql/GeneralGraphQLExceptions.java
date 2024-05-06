package com.mawuli.sns.exceptionhandler.graphql;

import org.springframework.web.server.ResponseStatusException;

public class GeneralGraphQLExceptions extends RuntimeException {
    public GeneralGraphQLExceptions(String message) {
        super(message);
    }
}
