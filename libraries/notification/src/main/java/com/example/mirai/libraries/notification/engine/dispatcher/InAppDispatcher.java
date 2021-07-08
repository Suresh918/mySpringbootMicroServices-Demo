package com.example.mirai.libraries.notification.engine.dispatcher;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import com.example.mirai.libraries.notification.inapp.model.InAppNotification;
import com.example.mirai.libraries.notification.inapp.service.InAppNotificationPublisher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InAppDispatcher extends BaseDispatcher {
	public InAppDispatcher(BaseRole baseRole) {
		super(baseRole);
	}

	private InAppNotification generateInAppNotificationInstance() {
		InAppNotification inAppNotification = new InAppNotification();

		inAppNotification.setCategory(baseRole.getCategory());
		inAppNotification.setRole(baseRole.getRole());
		inAppNotification.setEntity(baseRole.getEntityType());
		inAppNotification.setActor(baseRole.getActor());
		inAppNotification.setRecipient(baseRole.getRecipient());
		inAppNotification.setTimestamp(baseRole.getEventTimestamp());
		inAppNotification.setEntityId(baseRole.getEntityId());
		inAppNotification.setTitle(baseRole.getTitle());

		return inAppNotification;
	}

	@Override
	public void dispatch() {
		String title = null;
		try {
			InAppNotificationPublisher inAppNotificationPublisher = ApplicationContextHolder.getApplicationContext().getBean(InAppNotificationPublisher.class);
			InAppNotification inAppNotification = generateInAppNotificationInstance();
			title = inAppNotification.getTitle();
			boolean success = inAppNotificationPublisher.queueInAppNotification(inAppNotification);
			log.info("InApp Notification Message " + title.trim() + " sent to " + inAppNotification.getRecipient().getUserId());
		} catch (Exception e) {
			log.info("InApp Notification Message " + title.trim() + " for " + baseRole.getRecipient().getUserId() + " failed" + " Reason : " + e.getMessage());
		}
	}

}
