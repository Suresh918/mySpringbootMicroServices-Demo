package com.example.mirai.libraries.core.exception;

import lombok.Getter;

/**
 * Thrown when the user is not authorized to perform an operation
 *
 * @author ptummala
 * @see RuntimeException
 * @since 1.0.0
 */
@Getter
public class UnauthorizedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
}
