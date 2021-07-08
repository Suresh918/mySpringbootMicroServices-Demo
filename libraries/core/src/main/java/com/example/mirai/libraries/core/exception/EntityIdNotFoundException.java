package com.example.mirai.libraries.core.exception;

import lombok.Getter;

/**
 * Thrown by the services when the entity related to given entity id is not found.
 *
 * @author ptummala
 * @see RuntimeException
 * @since 1.0.0
 */
@Getter
public class EntityIdNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String entity;

    public EntityIdNotFoundException(String entityName) {
        this.entity = entityName;
    }

    public EntityIdNotFoundException() {
        entity = null;
    }
}
