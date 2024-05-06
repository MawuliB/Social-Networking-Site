package com.mawuli.sns.exceptionhandler.graphql;

import graphql.GraphQLError;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

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

    @GraphQlExceptionHandler(GeneralGraphQLExceptions.class)
    public GraphQLError handleGeneralGraphQLExceptions(GeneralGraphQLExceptions exception) {
        return GraphQLError
                .newError()
                .message(exception.getLocalizedMessage())
                .build();
    }

    @GraphQlExceptionHandler(UsernameAlreadyExistException.class)
    public GraphQLError handleUsernameAlreadyExistException(UsernameAlreadyExistException exception) {
        return GraphQLError
                .newError()
                .message(exception.getLocalizedMessage())
                .build();
    }

    @GraphQlExceptionHandler(InvalidOldPasswordException.class)
    public GraphQLError handleInvalidOldPasswordException(InvalidOldPasswordException exception) {
        return GraphQLError
                .newError()
                .message(exception.getLocalizedMessage())
                .build();
    }

    @GraphQlExceptionHandler(InvalidDataAccessApiUsageException.class)
    public GraphQLError handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException exception) {
        return GraphQLError
                .newError()
                .message(exception.getLocalizedMessage())
                .build();
    }

    @GraphQlExceptionHandler(NullPointerException.class)
    public GraphQLError handleNullPointerException(NullPointerException exception) {
        return GraphQLError
                .newError()
                .message(exception.getLocalizedMessage())
                .build();
    }
}
