package com.example.mirai.libraries.core.exception;

/**
 * Thrown by the merge operation on an entity.
 * <p>The request for the merge method consists of old value of the field and the new value, that the field needs to be updated with.
 *
 * <p>This exception is thrown when the old values in the request are not matched with the value in the database.
 * <p>Indicating that the user is not working on the latest data or data has been updated by some one else.
 *
 * @author ptummala
 * @see RuntimeException
 * @since 1.0.0
 */
public class ParallelUpdateException extends RuntimeException {
    private static final long serialVersionUID = 1L;
}
