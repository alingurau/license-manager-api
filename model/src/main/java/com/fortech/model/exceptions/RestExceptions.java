package com.fortech.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public final class RestExceptions extends Throwable {

    @ResponseStatus(value=HttpStatus.FORBIDDEN)
    public static final class Forbidden extends RuntimeException {

        public Forbidden() {
            super();
        }

        public Forbidden(String message) {
            super(message);
        }

    }

    @ResponseStatus(value=HttpStatus.UNAUTHORIZED)
    public static final class Unauthorized extends RuntimeException {

        public Unauthorized() {
            super();
        }

        public Unauthorized(String message) {
            super(message);
        }

    }

    @ResponseStatus(value=HttpStatus.NOT_FOUND)
    public static final class EntityNotFoundException extends RuntimeException {

        public EntityNotFoundException() {
            super();
        }

        public EntityNotFoundException(String message) {
            super(message);
        }

    }

    @ResponseStatus(value=HttpStatus.CONFLICT)
    public static final class EntityExistsException extends RuntimeException {

        public EntityExistsException() {
            super();
        }

        public EntityExistsException(String message) {
            super(message);
        }

    }

    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    public static final class BadRequest extends RuntimeException {

        public BadRequest() {
            super();
        }

        public BadRequest(String message) {
            super(message);
        }

    }

    @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
    public static final class OperationFailed extends RuntimeException {

        public OperationFailed() {
            super();
        }

        public OperationFailed(String message) {
            super(message);
        }

    }
}
