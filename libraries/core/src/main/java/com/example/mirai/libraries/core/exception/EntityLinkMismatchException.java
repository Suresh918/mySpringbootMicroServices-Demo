package com.example.mirai.libraries.core.exception;

/**
 * In the aggregate structures, {@link com.example.mirai.libraries.core.annotation.LinkTo} annotation is used to provide information on the relationship with the current field.
 *
 * <p>While processing the aggregates, if it is not possible to resolve the relationship of current field(entity) with the values provided, this exception is thrown.
 *
 * @author ptummala
 * @see RuntimeException
 * @since 1.0.0
 */
public class EntityLinkMismatchException extends RuntimeException {
    private static final long serialVersionUID = 1L;
}
