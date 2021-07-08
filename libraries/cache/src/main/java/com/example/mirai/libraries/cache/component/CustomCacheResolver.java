package com.example.mirai.libraries.cache.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.example.mirai.libraries.core.annotation.EntityClass;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

@Component("customCacheResolver")
public class CustomCacheResolver extends SimpleCacheResolver {
	public CustomCacheResolver(CacheManager cacheManager) {
		super(cacheManager);
	}

	protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
		Class serviceClass = context.getTarget().getClass();
		Collection<String> collection = new ArrayList<>();
		EntityClass entityClass = AnnotationUtils.findAnnotation(serviceClass, EntityClass.class);
		String entityName;
		if (entityClass != null) {
			entityName = entityClass.value().getCanonicalName();
		}
		else {
			CacheConfig cacheConfig = AnnotationUtils.getAnnotation(serviceClass, CacheConfig.class);
			if (cacheConfig != null) {
				collection.addAll(Arrays.asList(cacheConfig.cacheNames()));
				return collection;
			}
			entityName = serviceClass.getSimpleName().toLowerCase();
		}
		collection.add(entityName);
		return collection;
	}
}
