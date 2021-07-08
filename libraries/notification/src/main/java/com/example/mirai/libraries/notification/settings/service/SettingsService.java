package com.example.mirai.libraries.notification.settings.service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import com.example.mirai.libraries.core.exception.UnauthorizedException;
import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.notification.engine.config.JmsDestinationConfigurationProperties;
import com.example.mirai.libraries.notification.settings.repository.SettingsRepository;
import com.example.mirai.libraries.websecurity.PrincipalAwareJwtAuthenticationToken;
import com.example.mirai.libraries.notification.core.AuthenticatedContext;
import com.example.mirai.libraries.notification.settings.model.Settings;
import com.example.mirai.libraries.notification.settings.model.Subscription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;

@Slf4j
@AllArgsConstructor
@Service("settingsService")
public class SettingsService {
	private final AuthenticatedContext auditorExtractorImpl;

	private final SettingsRepository settingsRepository;

	private final JmsTemplate jmsTemplate;


	private final JmsDestinationConfigurationProperties jmsDestinationConfigurationProperties;

	private final ObjectMapper objectMapper;

	public Settings createSettings(Settings settings) {
		return settingsRepository.save(settings);
	}

	public Optional<Settings> getSettings(String userId) {
		return settingsRepository.findById(userId);
	}

	public Settings updateSettings(Principal principal,Settings settings) {

		if (!settings.getUserId().equals(auditorExtractorImpl.getUserId()))
			throw new UnauthorizedException();

		Optional<Settings> oldUserSettings = settingsRepository.findById(settings.getUserId());
		//reading the new delegate users to add and old delegate users to remove
		if (oldUserSettings.isPresent() && oldUserSettings.get().getSubscriptions() != null
				&& settings.getSubscriptions() != null) {

			Set<Subscription> newSubscriptionSet =  settings.getSubscriptions();
			Subscription[] newSubscriptionArray = new Subscription[newSubscriptionSet.size()];
			newSubscriptionSet.toArray(newSubscriptionArray);
			List<User> newDelegateUsersList = newSubscriptionArray[0].getDelegates();

			Set<Subscription> oldSubscriptionSet = oldUserSettings.get().getSubscriptions();
			Subscription[] oldSubscriptionArray = new Subscription[oldSubscriptionSet.size()];
			oldSubscriptionSet.toArray(oldSubscriptionArray);
			List<User> oldDelegateUsersList = oldSubscriptionArray[0].getDelegates();
			List<User> delegateAddedList = new ArrayList<User>();
			if(newDelegateUsersList!=null) {
				 delegateAddedList = newDelegateUsersList.stream()
						.filter(newuser -> !oldDelegateUsersList.contains(newuser))
						.collect(Collectors.toList());
			}
			List<User> delegateRemovedList = new ArrayList<User>();;
			if(oldDelegateUsersList!=null) {
				 delegateRemovedList = oldDelegateUsersList.stream()
						.filter(olduser -> !newDelegateUsersList.contains(olduser))
						.collect(Collectors.toList());
			}

			if (delegateAddedList != null && delegateAddedList.size()!=0) {
				//publish delegator user to add
				//Principal principal = null;
				Jwt jwt = ((PrincipalAwareJwtAuthenticationToken) principal).getToken();
				User user = new User();
                user.setUserId(getClaimAsString(jwt, "user_id"));
                user.setFullName(getClaimAsString(jwt, "full_name"));
                user.setEmail(getClaimAsString(jwt, "email"));
				user.setDepartmentName(getClaimAsString(jwt, "department_name"));
				user.setAbbreviation(getClaimAsString(jwt, "abbreviation"));

				Event event  = new Event("DELEGATE-ADDED",
							             "SUCCESS",
										 "com.example.mirai.libraries.notification.settings.model.Settings",
										 "com.example.mirai.libraries.notification.settings.model.Settings",
						                  user,
						                  delegateAddedList,
						                 null,
						                  System.currentTimeMillis());

				createAndSendMessageforDelegateAdded(event);
			}

			if (delegateRemovedList != null && delegateRemovedList.size()!=0 ) {
				//publish delegator user to remove
				//Principal principal = null;
				Jwt jwt = ((PrincipalAwareJwtAuthenticationToken) principal).getToken();
				User user = new User();
				user.setUserId(getClaimAsString(jwt, "user_id"));
				user.setFullName(getClaimAsString(jwt, "full_name"));
				user.setEmail(getClaimAsString(jwt, "email"));
				user.setDepartmentName(getClaimAsString(jwt, "department_name"));
				user.setAbbreviation(getClaimAsString(jwt, "abbreviation"));

				Event event  = new Event("DELEGATE-REMOVED",
						"SUCCESS",
						"com.example.mirai.libraries.notification.settings.model.Settings",
						"com.example.mirai.libraries.notification.settings.model.Settings",
						 user,
						 delegateRemovedList,
						null,
						 System.currentTimeMillis());

				createAndSendMessageforDelegateRemoved(event);
			}

		}
		return settingsRepository.save(settings);
	}

	private void createAndSendMessageforDelegateAdded( Event event) {
		String messageAsString = "";
		try {
			messageAsString = objectMapper.writeValueAsString(event);
		} catch (JsonProcessingException jspe) {
			// throw new RuntimeException("Unable to send message");
			throw new RuntimeException("Unable to send message");
		}

		String destination = jmsDestinationConfigurationProperties.getSettingsDestination();
		jmsTemplate.convertAndSend(destination,
				messageAsString, new MessagePostProcessor() {
					@Override
					public javax.jms.Message
					postProcessMessage(javax.jms.Message message) throws JMSException {
						message.setStringProperty("type", event.getType());
						message.setStringProperty("status", event.getStatus());
						message.setStringProperty("entity", event.getEntity());
						message.setStringProperty("payload", event.getPayload());
						message.setLongProperty("timestamp", System.currentTimeMillis());
						log.info("message " + message);
						return message;
					}
				});
	}

	private void createAndSendMessageforDelegateRemoved( Event event ) {
		String messageAsString = "";
		try {
			messageAsString = objectMapper.writeValueAsString(event);
		} catch (JsonProcessingException jspe) {
			// throw new RuntimeException("Unable to send message");
			throw new RuntimeException("Unable to send message");
		}

		String destination = jmsDestinationConfigurationProperties.getSettingsDestination();
		jmsTemplate.convertAndSend(destination,
				messageAsString, new MessagePostProcessor() {
					@Override
					public javax.jms.Message
					postProcessMessage(javax.jms.Message message) throws JMSException {
						message.setStringProperty("type", event.getType());
						message.setStringProperty("status", event.getStatus());
						message.setStringProperty("entity", event.getEntity());
						message.setStringProperty("payload", event.getPayload());
						message.setLongProperty("timestamp", System.currentTimeMillis());
						log.info("message " + message);
						return message;
					}
				});
	}

	private String getClaimAsString(Jwt jwt, String claim) {
		if (jwt == null)
			return null;
		List<String> claims = jwt.getClaimAsStringList(claim);
		if (claims == null)
			return null;
		// return (String) claims.get(0);
		return claims.get(0);
	}

	public void deleteSettings(String userId) {
		if (!userId.equals(auditorExtractorImpl.getUserId()))
			throw new UnauthorizedException();
		settingsRepository.deleteById(userId);
	}

	public Optional<Subscription> getUserSubscription(String userId, String event, String role) {
		Optional<Settings> optionalUserSettings = settingsRepository.findById(userId);
		if (!optionalUserSettings.isPresent())
			return Optional.empty();
		Settings userSettings = optionalUserSettings.get();
		return userSettings.getSubscriptions().stream()
				.filter(subscription -> subscription.getEvent().equals(event) &&
						subscription.getRole().equals(role)).findFirst();
	}

	public Set<Subscription> getUserSubscriptions(String userId, String event, String role) {
		Optional<Settings> optionalUserSettings = settingsRepository.findById(userId);
		if (optionalUserSettings.isEmpty())
			return null;
		Settings userSettings = optionalUserSettings.get();
		return userSettings.getSubscriptions().stream()
				.filter(subscription -> subscription.getEvent().equals(event) &&
						subscription.getRole().equals(role)).collect(Collectors.toSet());
	}
}
