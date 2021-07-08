package com.example.mirai.libraries.core.exception;

import lombok.Getter;

/**
 * This is a Default exception, can be thrown when the specific exception type is not found.
 *
 * @author ptummala
 * @see RuntimeException
 * @since 1.0.0
 */
@Getter
public class DefaultException extends RuntimeException {
    private final String message;

    public DefaultException(String exceptionMessage) {
        this.message = exceptionMessage;
    }
}
