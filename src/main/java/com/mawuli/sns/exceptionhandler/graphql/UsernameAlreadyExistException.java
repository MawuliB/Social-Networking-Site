package com.mawuli.sns.exceptionhandler.graphql;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class UsernameAlreadyExistException extends ResponseStatusException {
    public UsernameAlreadyExistException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
