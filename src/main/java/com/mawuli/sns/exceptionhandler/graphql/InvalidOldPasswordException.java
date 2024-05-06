package com.mawuli.sns.exceptionhandler.graphql;

import org.springframework.http.HttpStatus;

public class InvalidOldPasswordException extends RuntimeException {
    public InvalidOldPasswordException(HttpStatus status, String message) {
        super(String.format("Status: %s, Reason: %s", status, message));
    }
}
