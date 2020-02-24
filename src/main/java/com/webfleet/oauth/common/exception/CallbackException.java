package com.webfleet.oauth.common.exception;

public class CallbackException extends RuntimeException {

    public CallbackException(final String message) {
        super(message);
    }

    public CallbackException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
