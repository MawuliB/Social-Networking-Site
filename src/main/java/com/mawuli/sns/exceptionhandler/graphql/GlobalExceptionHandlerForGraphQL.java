package com.mawuli.sns.exceptionhandler.graphql;

import graphql.GraphQLError;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class GlobalExceptionHandlerForGraphQL {

    @GraphQlExceptionHandler(EntityNotFoundException.class)
    public GraphQLError handleEntityNotFoundException(EntityNotFoundException exception) {
        return GraphQLError
                .newError()
                .message(exception.getMessage())
                .build();
    }

    @GraphQlExceptionHandler(InvalidDataAccessApiUsageException.class)
    public GraphQLError handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException exception) {
        return GraphQLError
                .newError()
                .message(exception.getLocalizedMessage())
                .build();
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointerException(NullPointerException exception) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body("Null pointer exception occurred");
    }
}
