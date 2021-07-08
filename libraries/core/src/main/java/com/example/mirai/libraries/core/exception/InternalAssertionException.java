package com.example.mirai.libraries.core.exception;

import lombok.Getter;

/**
 * This is a generic exception used to indicate any kind of technical error while processing.
 *
 * @author ptummala
 * @see RuntimeException
 * @since 1.0.0
 */
@Getter
public class InternalAssertionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String message;

    public InternalAssertionException(String message) {
        this.message = message;
    }
}
