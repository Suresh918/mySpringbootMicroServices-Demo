package com.example.mirai.libraries.security.abac.listeners;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.security.abac.component.AbacProcessor;
import com.example.mirai.libraries.security.abac.config.AbacConfigurationProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Data
@ConditionalOnProperty(name = "mirai.libraries.security.abac.enabled", havingValue = "true", matchIfMissing = true)
public class AbacEntityEventListener {
	private final AbacProcessor abacProcessor;

	private final AbacConfigurationProperties abacConfigurationProperties;

	@EventListener(condition = "@securityAbacConfigSpringCacheConfiguration !=null &&  !(@securityAbacConfigSpringCacheConfiguration.getType().equalsIgnoreCase(\"NONE\")) && (#event.type == 'CREATE' || #event.type == 'CREATE-LINK' || " +
			"#event.type == 'DELETE' || #event.type == 'UPDATE' || #event.type == 'MERGE')")
	public void handleEntityEventSuccessful(Event event) {
		String payload = event.getPayload();
		Class payloadClass;
		try {
			payloadClass = Class.forName(payload);
			if (BaseEntityInterface.class.isAssignableFrom(payloadClass)) {
				BaseEntityInterface e = (BaseEntityInterface) event.getData();
				if (abacConfigurationProperties.getCacheAsynchronously()) {
					Thread newThread = new Thread(() -> abacProcessor.refreshUserRolesOfImpactedEntities(e));
					newThread.start();
				}
				else {
					abacProcessor.refreshUserRolesOfImpactedEntities(e);
				}
			}
		}
		catch (ClassNotFoundException e) {
			log.warn("" + e.getException());
		}
	}
}
