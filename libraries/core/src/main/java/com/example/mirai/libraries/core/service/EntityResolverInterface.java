package com.example.mirai.libraries.core.service;

import com.example.mirai.libraries.core.model.StatusInterface;

/**
 * Interface to be implemented by EntityResolver classes
 *
 * <p>It exposes methods to return the related service of entity, and the aggregate classes of entity etc.,
 *
 * @author ptummala
 * @since 1.0.0
 */
public interface EntityResolverInterface {
	ServiceInterface getService(Class entityClass);

	ServiceInterface getService(String type);

	Class<? extends ServiceInterface> getEntityClass(Class classAnnotatedWithEntityClass);

	StatusInterface[] getEntityStatuses(Class entityClass);

	Class getCaseStatusAggregateClass(String link);

	Class getAggregateClass(String link);
}
