package com.example.mirai.libraries.notification.email.listener;


import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.notification.email.model.Email;
import com.example.mirai.libraries.notification.engine.config.MailConfigurationProperties;
import com.example.mirai.libraries.notification.error.NotificationErrorService;
import com.example.mirai.libraries.notification.error.model.IrrecoverableNotificationException;
import com.example.mirai.libraries.notification.error.model.MessageToMimeMessageConversionFailure;
import com.example.mirai.libraries.notification.error.model.RecoverableNotificationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Objects;

@Component
@Slf4j
public class EmailSender {
	private final MailConfigurationProperties mailConfigurationProperties;
	private final JavaMailSender javaMailSender;
	private final NotificationErrorService notificationErrorService;

	@Autowired
	public EmailSender(MailConfigurationProperties mailConfigurationProperties, JavaMailSender javaMailSender,
					   NotificationErrorService notificationErrorService) {
		this.mailConfigurationProperties = mailConfigurationProperties;
		this.javaMailSender = javaMailSender;
		this.notificationErrorService = notificationErrorService;
	}

	@JmsListener(destination = "com.example.mirai.notificationservice.email")
	public void processMessage(final Message message) throws Throwable {
		try {
			MimeMessage mimeMessage = convertMessageToMimeMessage(message);
			if (mimeMessage != null)
				sendEmail(mimeMessage);
			message.acknowledge();
		} catch (IrrecoverableNotificationException irrecoverableNotificationException) {
			handleIrrecoverableException(message, irrecoverableNotificationException);
			message.acknowledge();
		} catch (RecoverableNotificationException recoverableNotificationException) {
			handleRecoverableException(recoverableNotificationException);
		}
	}

	protected void handleRecoverableException(RecoverableNotificationException recoverableNotificationException) {
		String description = recoverableNotificationException.getDescription();
		log.error("Message not removed form queue: " + description + " : " + recoverableNotificationException.getMessage());
	}

	protected void handleIrrecoverableException(Message message, IrrecoverableNotificationException irrecoverableNotificationException) {
		log.error("Message removed form queue: " + irrecoverableNotificationException.getDescription() + " : " + irrecoverableNotificationException.getMessage());
		notificationErrorService.recordException(message, irrecoverableNotificationException, irrecoverableNotificationException.getDescription());
	}

	private Event getEventThatResultedInEmail(Message jmsMessage) throws JMSException, JsonProcessingException {
		//get message body from jms message
		String jsonString = jmsMessage.getBody(String.class);

		//create email object from message body
		Email email = getObjectMapper().readValue(jsonString, Email.class);

		return email.getEvent();
	}


	public MimeMessage convertMessageToMimeMessage(Message jmsMessage) {
		try {
			//get message body from jms message
			String jsonString = jmsMessage.getBody(String.class);

			//create email object from message body
			Email email = getObjectMapper().readValue(jsonString, Email.class);

					//create mime message
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(mailConfigurationProperties.getFrom());
			helper.setTo(email.getTo());
			//TODO validate email and raise internal assertion error
			String[] cc = email.getCc();
			if (Objects.nonNull(cc))
				cc = Arrays.stream(cc).filter(s -> (Objects.nonNull(s) && s.length() > 0)).toArray(String[]::new);
			else
				cc = new String[]{};
			helper.setCc(cc);
			//TODO validate email and raise internal assertion error

			helper.setSubject(email.getSubject());
			helper.setText(email.getContent(), true);

			return message;
		} catch (Exception exception) {
			Event event = null;
			try {
				event = getEventThatResultedInEmail(jmsMessage);
			} catch (Exception exception1) {
				log.warn("Unable to extract event for logging.");
			}
			throw new MessageToMimeMessageConversionFailure(exception,
					"Could not convert JMS Message to Mime Message: ", event);

		}
	}

	@Retryable(value = Exception.class, maxAttemptsExpression = "${retry.email.maxAttempts}",
			backoff = @Backoff(delayExpression = "${retry.email.delay}",
					multiplierExpression = "${retry.email.multiplier}",
					maxDelayExpression = "${retry.email.maxDelay}"))
	public void sendEmail(MimeMessage mimeMessage) throws Throwable {

		try {
			javaMailSender.send(mimeMessage);
			log.info("Email Notification Message " + mimeMessage.getSubject().trim() + " sent to:" + Arrays.toString(mimeMessage.getAllRecipients()));
		} catch (Exception e) {
			log.info("Email Notification Message " + mimeMessage.getSubject().trim() + " for :" + Arrays.toString(mimeMessage.getAllRecipients()) + " failed" + " Reason : " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	ObjectMapper getObjectMapper() { return ApplicationContextHolder.getApplicationContext().getBean(ObjectMapper.class);}
}
