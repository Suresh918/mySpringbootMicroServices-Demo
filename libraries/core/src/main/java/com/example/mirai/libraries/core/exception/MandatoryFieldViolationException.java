package com.example.mirai.libraries.core.exception;

import lombok.Getter;

/**
 * Thrown by the services when the mandatory fields are not field in order to perform a specific operation.
 *
 * @author ptummala
 * @see RuntimeException
 * @since 1.0.0
 */
@Getter
public class MandatoryFieldViolationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String entity;

    public MandatoryFieldViolationException(String entityName) {
        this.entity = entityName;
    }
}
