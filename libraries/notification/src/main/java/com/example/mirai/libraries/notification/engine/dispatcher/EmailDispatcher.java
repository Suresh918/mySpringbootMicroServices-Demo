package com.example.mirai.libraries.notification.engine.dispatcher;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.notification.email.model.Email;
import com.example.mirai.libraries.notification.email.service.EmailQueuer;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import com.example.mirai.libraries.notification.error.model.EventToEmailConversionFailure;
import com.example.mirai.libraries.notification.error.model.TransportFailure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class EmailDispatcher extends BaseDispatcher {
	Map<String, Object> templateModel;

	String[] delegateEmails = new String[]{};

	public EmailDispatcher(BaseRole baseRole, String[] delegateEmails) {
		super(baseRole);
		templateModel = new HashMap<>();
		templateModel.put("baseRole", baseRole);
		this.delegateEmails = delegateEmails;
	}

	public EmailDispatcher(BaseRole baseRole) {
		super(baseRole);
		templateModel = new HashMap<>();
		templateModel.put("baseRole", baseRole);
	}

	private String generateEmailBody() {
		Context thymeleafContext = new Context();
		thymeleafContext.setVariables(templateModel);
		return getTemplateEngine().process(baseRole.getCategory() + File.separator + baseRole.getRole() + ".email.body.html", thymeleafContext);
	}

	private Event getEventData() {
		return baseRole.getEvent();
	}

	private String generateEmailSubject() {
		Context thymeleafContext = new Context();
		thymeleafContext.setVariables(templateModel);
		return getTemplateEngine().process(baseRole.getCategory() + File.separator + baseRole.getRole() + ".email.subject.txt", thymeleafContext);
	}

	private SpringTemplateEngine getTemplateEngine() {
		return (SpringTemplateEngine) ApplicationContextHolder.getApplicationContext().getBean("springTemplateEngine");
	}

	@Override
	public void dispatch() {
		Email email = createEmail();
		if(email != null)
			queueEmail(email);
	}


	public Email createEmail() {
		try {
			String emailBody = generateEmailBody();
			String emailSubject = generateEmailSubject();
			return new Email(baseRole.getRecipient().getEmail(), emailSubject, emailBody, delegateEmails, getEventData());
		} catch (Exception exception) {
			throw new EventToEmailConversionFailure(exception, "Could not convert Event to Email object",
					baseRole.getRecipient().getUserId(), getEventData());
		}
	}

	@Retryable( value = TransportFailure.class, maxAttemptsExpression = "${retry.email.maxAttempts}",
			backoff = @Backoff(delayExpression = "${retry.email.delay}",
					multiplierExpression = "${retry.email.multiplier}",
					maxDelayExpression = "${retry.email.maxDelay}"))
	public void queueEmail(Email email) {
		EmailQueuer emailQueuer = ApplicationContextHolder.getApplicationContext().getBean(EmailQueuer.class);
		emailQueuer.queueEmail(email);
		log.info("Email Notification Message " + email.getSubject().trim() + " queued for to:" + email.getTo() + " cc:" + Arrays.toString(email.getCc()));
	}

}
