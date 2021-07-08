package com.example.mirai.libraries.core.model;

/**
 * Holds the values of different aggregate types
 * Used to differentiate aggregate while building the aggregate and while reading the aggregate
 * Entity - the aggregate contains the entity data
 * CASE_ACTIONS - the aggregate contains the {@link CasePermissions} data of respective entity
 * CHANGELOG - the aggregate contains the <code>Audit</code> entries of related entity
 *
 * @author ptummala
 * @since 1.0.0
 */
public enum AggregateType {
	ENTITY, CASE_ACTIONS, CHANGELOG
}
