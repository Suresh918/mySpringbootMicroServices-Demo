package com.example.mirai.libraries.notification.engine.processor;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.notification.engine.config.InAppConfigurationProperties;
import com.example.mirai.libraries.notification.engine.config.LinkConfigurationProperties;
import com.example.mirai.libraries.notification.engine.dispatcher.EmailDispatcher;
import com.example.mirai.libraries.notification.engine.dispatcher.InAppDispatcher;
import com.example.mirai.libraries.notification.error.model.UnableToFetchDelegates;
import com.example.mirai.libraries.notification.error.model.UnableToFetchInAppSubscription;
import com.example.mirai.libraries.notification.error.model.UnableToFetchRecipientSubscription;
import com.example.mirai.libraries.notification.settings.model.Subscription;
import com.example.mirai.libraries.notification.settings.service.SettingsService;
import com.example.mirai.libraries.notification.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
public abstract class BaseRole {
	public User recipient;

	protected Event event;

	protected String role;

	protected String category;

	protected Long entityId;

	protected Long id;

	LinkConfigurationProperties linkConfigurationProperties;

	InAppConfigurationProperties inAppConfigurationProperties;

	ObjectMapper objectMapper;

	public BaseRole(Event event, String role, String category, Long entityId, Long id) {
		this.linkConfigurationProperties = ApplicationContextHolder.getApplicationContext().getBean(LinkConfigurationProperties.class);
		this.inAppConfigurationProperties = ApplicationContextHolder.getApplicationContext().getBean(InAppConfigurationProperties.class);
		this.objectMapper = ApplicationContextHolder.getApplicationContext().getBean(ObjectMapper.class);
		this.event = event;
		this.role = role;
		this.category = category;
		this.entityId = entityId;
		this.id = id;
	}

	public abstract String getTitle();

	public User getActor() {
		return getEvent().getActor();
	}

	public Long getEntityId() {
		return entityId;
	}

	public Long getId() {
		return id;
	}

	public String getEntityType() {
		if (Objects.isNull(getEvent()))
			return null;
		return getEvent().getEntity();
	}

	public String getEventType() {
		if (Objects.isNull(getEvent()))
			return null;
		return getEvent().getType();
	}

	public Long getEventTimestamp() {
		if (Objects.isNull(getEvent()))
			return null;
		return getEvent().getTimestamp();
	}
	public boolean ignoreRecipientCheck(){
		return false;
	}

	public boolean ignoreSubscriptionCheck(){
		return false;
	}

	public String formatUserName(Map userValue) {
		User user = objectMapper.convertValue(userValue, User.class);
		return user.getFullName() + " (" + user.getAbbreviation() + ")";
	}

	public String formatUserName(User user) {
		return user.getFullName() + " (" + user.getAbbreviation() + ")";
	}

	public String formatDate(String dateValue) {
		TemporalAccessor temporalAccessor = DateTimeFormatter.ofPattern(Constants.DATE_TIME_ZONE_FORMAT).parse(dateValue);
		return LocalDate.from(temporalAccessor).toString();
	}

	private Subscription getRecipientSubscription() {
		User recipient;
		String userId = null;
		try {
			recipient = getRecipient();
			userId = Objects.nonNull(recipient) ? recipient.getUserId() : null;
			Optional<Subscription> optionalSubscription = getSettingsService().getUserSubscription(recipient.getUserId(), getCategory(), getRole());
			if (optionalSubscription.isPresent())
				return optionalSubscription.get();
		} catch (Exception exception) {
			//TODO segregate the exception between transport exception and other
			throw new UnableToFetchRecipientSubscription(exception, "Unable to get " + userId + " subscription", userId, getEvent());
		}
		return null;
	}

	private SettingsService getSettingsService() {
		return (SettingsService) ApplicationContextHolder.getApplicationContext().getBean("settingsService");
	}


	public boolean hasMandatoryProperties() {
		return getRecipient() != null;
	}

	public boolean isApplicable() {
		if(ignoreRecipientCheck()){
			return true;
		}else {
			return !getRecipient().equals(getActor());
		}
	}


	public void notifyRecipient() {
		if (!hasMandatoryProperties())
			return;
		if (!isApplicable())
			return;
		if (ignoreSubscriptionCheck() || getRole().equals("Subscriber") || getRole().equals("Operation") || getRole().equals("LastLoggedInReportRole")) {
			sendEmailNotification();
			return;
		}
		Subscription subscription = getRecipientSubscription();
		if (subscription == null)
			return;

		sendEmailNotifications(subscription);

		sendInAppNotifications(subscription);
	}

	public void sendEmailNotification() {
		new EmailDispatcher(this).dispatch();
	}

	public String getPhotoUrl(String userId) {
		return linkConfigurationProperties.getPhotoUrl().replace("{USER-ID}", userId);
	}

	public String getNotificationsUrl() {
		String baseUrl = getLinkConfigurationProperties().getBaseUrl();
		String notificationsUrl = getLinkConfigurationProperties().getNotifications();
		return baseUrl + notificationsUrl;
	}

	private void sendEmailNotifications(Subscription subscription) {
		String userId = Objects.nonNull(recipient) ? recipient.getUserId() : null;
		Boolean isEmailChannelSubscribed = null;
		String[] delegateEmails = null;
		try {
			isEmailChannelSubscribed = subscription.getEmailChannel().getEnabled();
			List<User> delegates = subscription.getDelegates() != null ? subscription.getDelegates().stream().filter(Objects::nonNull).collect(Collectors.toList()) : new ArrayList<>();
			delegateEmails = delegates.stream().map(delegate -> delegate.getEmail()).filter(Objects::nonNull).collect(Collectors.toList()).toArray(new String[delegates.size()]);
		} catch (Exception exception) {
			//TODO segregate the exception between transport exception and other
			throw new UnableToFetchDelegates(exception, "Unable to get " + userId + " delegates", userId, getEvent());
		}

		if (isEmailChannelSubscribed != null && isEmailChannelSubscribed)
			new EmailDispatcher(this, delegateEmails).dispatch();
	}

	private void sendInAppNotifications(Subscription subscription) {
		Boolean isInAppChannelSubscribed = null;
		String userId = Objects.nonNull(recipient) ? recipient.getUserId() : null;
		try {
			isInAppChannelSubscribed = subscription.getInAppChannel().getEnabled();
		} catch (Exception exception) {
			throw new UnableToFetchInAppSubscription(exception, "Unable to get " + userId + " inapp subscription", userId, getEvent());
		}

		if (isInAppChannelSubscribed != null && isInAppChannelSubscribed && inAppConfigurationProperties.getEnabled())
			(new InAppDispatcher(this)).dispatch();
	}

}
