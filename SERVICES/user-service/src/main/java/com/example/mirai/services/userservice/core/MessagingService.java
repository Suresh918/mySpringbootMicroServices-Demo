package com.example.mirai.services.userservice.core;

import javax.jms.JMSException;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.services.userservice.exception.ProfileCreationFailedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessagingService {
	private final ObjectMapper objectMapper;

	private final JmsTemplate jmsTemplate;

	public MessagingService(ObjectMapper objectMapper, JmsTemplate jmsTemplate) {
		this.objectMapper = objectMapper;
		this.jmsTemplate = jmsTemplate;
	}

	public void createAndSendMessage(Event event, String destination) {
		String messageAsString = "";
		try {
			messageAsString = objectMapper.writeValueAsString(event);
		}
		catch (JsonProcessingException jsonProcessingException) {
			throw new ProfileCreationFailedException("Unable to send message");
		}
		jmsTemplate.convertAndSend(destination,
				messageAsString, message -> {
					try {
						message.setStringProperty("type", event.getType());
						message.setStringProperty("status", event.getStatus());
						message.setStringProperty("entity", event.getEntity());
						message.setStringProperty("payload", event.getPayload());
						message.setLongProperty("timestamp", event.getTimestamp());
						message.setBooleanProperty("JMS_TIBCO_PRESERVE_UNDELIVERED", true);
						log.info("message " + message);
						return message;
					}
					catch (JMSException jmsException) {
						throw new ProfileCreationFailedException("Unable to send message");
					}
				});
	}
}
