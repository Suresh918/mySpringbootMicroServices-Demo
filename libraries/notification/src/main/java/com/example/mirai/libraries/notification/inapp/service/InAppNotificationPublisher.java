package com.example.mirai.libraries.notification.inapp.service;

import com.example.mirai.libraries.notification.engine.config.JmsDestinationConfigurationProperties;
import com.example.mirai.libraries.notification.inapp.model.InAppNotification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

@Component
@Slf4j
public class InAppNotificationPublisher {
	private final ObjectMapper objectMapper;
	private final JmsTemplate jmsTemplate;
	private final JmsDestinationConfigurationProperties jmsDestinationConfigurationProperties;

	@Autowired
	public InAppNotificationPublisher(ObjectMapper objectMapper, JmsTemplate jmsTemplate, JmsDestinationConfigurationProperties jmsDestinationConfigurationProperties) {
		this.objectMapper = objectMapper;
		this.jmsTemplate = jmsTemplate;
		this.jmsDestinationConfigurationProperties = jmsDestinationConfigurationProperties;
	}

	public boolean queueInAppNotification(InAppNotification inAppNotification) {
		try {
			createAndQueueMessage(inAppNotification);
			return true;
		} catch (Exception exception) {
			return false;
		}
	}


	public void createAndQueueMessage(InAppNotification inAppNotification) {
		String messageAsString = "";
		try {
			messageAsString = objectMapper.writeValueAsString(inAppNotification);
		} catch (JsonProcessingException jspe) {
			throw new RuntimeException("Unable to send message");
		}

		String destination = jmsDestinationConfigurationProperties.getInAppDestination();
		jmsTemplate.convertAndSend(destination,
				messageAsString, new MessagePostProcessor() {
					@Override
					public javax.jms.Message
					postProcessMessage(javax.jms.Message message) throws JMSException {
						message.setStringProperty("type", "IN-APP-NOTIFICATION");
						message.setStringProperty("status", "SUCCESS");
						message.setLongProperty("timestamp", System.currentTimeMillis());
						return message;
					}
				});
	}
}
