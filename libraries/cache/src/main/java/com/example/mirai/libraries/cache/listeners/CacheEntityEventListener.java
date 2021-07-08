package com.example.mirai.libraries.cache.listeners;

import com.example.mirai.libraries.cache.component.CustomCacheResolver;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.Event;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Data
public class CacheEntityEventListener {

	private final CustomCacheResolver cacheResolver;

	@EventListener(condition = "(#event.type == 'CREATE' || #event.type == 'CREATE-LINK' || #event.type == 'UPDATE' || #event.type == 'MERGE')")
	public void handleCreateUpdateEntityEvents(Event event) {
		String payload = event.getPayload();
		Class payloadClass;
		try {
			payloadClass = Class.forName(payload);
			if (BaseEntityInterface.class.isAssignableFrom(payloadClass)) {
				BaseEntityInterface e = (BaseEntityInterface) event.getData();
				cacheResolver.getCacheManager().getCache(event.getEntity()).put(e.generateObjectId(), e);
			}
		}
		catch (ClassNotFoundException e) {
			log.warn("" + e.getException());
		}
	}

	@EventListener(condition = "#event.type == 'DELETE' || #event.type == 'DELETE-WITHOUT-APPLICATION-EVENT-PUBLICATION'")
	public void handleDeleteEvents(Event event) {
		String payload = event.getPayload();
		Class payloadClass;
		try {
			payloadClass = Class.forName(payload);
			if (BaseEntityInterface.class.isAssignableFrom(payloadClass)) {
				BaseEntityInterface e = (BaseEntityInterface) event.getData();
				cacheResolver.getCacheManager().getCache(event.getEntity()).evict(e.generateObjectId());
			}
		}
		catch (ClassNotFoundException e) {
			log.warn("" + e.getException());
		}
	}
}
