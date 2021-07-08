package com.example.mirai.libraries.entity.service;

import java.util.Objects;

import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.StatusInterface;
import com.example.mirai.libraries.core.service.EntityResolverInterface;

import org.springframework.core.annotation.AnnotationUtils;

public interface EntityResolverDefaultInterface extends EntityResolverInterface {
	Class<? extends BaseEntityInterface> getEntityClass(String type);

	Class<? extends BaseEntityInterface> getEntityClass(String parent, String child);

	Class<? extends AggregateInterface> getAggregateClass(String parent, String child);

	StatusInterface[] getEntityStatuses(String entityType);

	StatusInterface[] getEntityStatuses(Class entityClass);

	default Class<? extends EntityServiceDefaultInterface> getEntityClass(Class classAnnotatedWithEntityClass) {
		EntityClass entityClassAnnotation = AnnotationUtils.findAnnotation(classAnnotatedWithEntityClass, EntityClass.class);
		return Objects.requireNonNull(entityClassAnnotation).value();
	}

	default Class<? extends EntityServiceDefaultInterface> getServiceClass(String type) {
		Class<? extends BaseEntityInterface> entityClass = getEntityClass(type);
		ServiceClass serviceClassAnnotation = AnnotationUtils.findAnnotation(entityClass, ServiceClass.class);
		return Objects.requireNonNull(serviceClassAnnotation).value();
	}

	default Class<? extends EntityServiceDefaultInterface> getServiceClass(Class classAnnotatedWithServiceClass) {
		ServiceClass serviceClassAnnotation = AnnotationUtils.findAnnotation(classAnnotatedWithServiceClass, ServiceClass.class);
		return Objects.requireNonNull(serviceClassAnnotation).value();
	}

	default EntityServiceDefaultInterface getService(String type) {
		Class<? extends EntityServiceDefaultInterface> serviceClass = getServiceClass(type);
		return ApplicationContextHolder.getApplicationContext().getBean(serviceClass);
	}

	default EntityServiceDefaultInterface getService(Class entityClass) {
		Class<? extends EntityServiceDefaultInterface> serviceClass = getServiceClass(entityClass);
		return ApplicationContextHolder.getApplicationContext().getBean(serviceClass);
	}
}
