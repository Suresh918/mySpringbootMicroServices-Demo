package com.example.mirai.libraries.notification.email.service;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.notification.email.model.Email;
import com.example.mirai.libraries.notification.engine.config.MailConfigurationProperties;
import com.example.mirai.libraries.notification.error.model.EmailToMessageConversionFailure;
import com.example.mirai.libraries.notification.error.model.TransportFailure;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;

@Service
@Slf4j
public class EmailQueuer {
	private final ObjectMapper objectMapper;
	private final MailConfigurationProperties mailConfigurationProperties;

	@Autowired
	public EmailQueuer(MailConfigurationProperties mailConfigurationProperties,
					   ObjectMapper objectMapper, JmsTemplate jmsTemplate) {
		this.mailConfigurationProperties = mailConfigurationProperties;
		this.objectMapper = objectMapper;
	}

	public void queueEmail(Email email) {
		createAndQueueMessage(email);
	}


	public void createAndQueueMessage(Email email) {
		JmsTemplate pointToPointJmsTemplate = (JmsTemplate) ApplicationContextHolder.getApplicationContext().getBean("pointToPointJmsTemplate");
		String messageAsString = "";
		try {
			messageAsString = objectMapper.writeValueAsString(email);
		} catch (Exception exception) {
			throw new EmailToMessageConversionFailure(exception, "Unable to convert Email to JMS Message Body for queuing",
					email.getTo(), email.getEvent());
		}

		try {
			pointToPointJmsTemplate.convertAndSend("com.example.mirai.notificationservice.email",
					messageAsString, new MessagePostProcessor() {
						@Override
						public javax.jms.Message
						postProcessMessage(javax.jms.Message message) throws JMSException {
							message.setStringProperty("type", "EMAIL-NOTIFICATION");
							message.setStringProperty("status", "SUCCESS");
							message.setLongProperty("timestamp", System.currentTimeMillis());
							return message;
						}
					});
		} catch (Exception exception) {
			throw new TransportFailure(exception, "Unable to queue Email", email.getTo());
		}
	}

	public boolean getEmailEnabled() {
		return mailConfigurationProperties.getEnabled();
	}
}
