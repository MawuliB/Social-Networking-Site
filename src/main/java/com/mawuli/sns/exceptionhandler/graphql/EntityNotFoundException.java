package com.mawuli.sns.exceptionhandler.graphql;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
