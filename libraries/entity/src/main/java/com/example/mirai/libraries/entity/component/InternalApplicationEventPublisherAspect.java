package com.example.mirai.libraries.entity.component;

import java.util.Date;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.core.service.ServiceInterface;
import com.example.mirai.libraries.entity.ApplicationEventActorExtractorInterface;
import com.example.mirai.libraries.entity.config.InternalApplicationEventConfigurationProperties;
import com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@AllArgsConstructor
public class InternalApplicationEventPublisherAspect {
	private final InternalApplicationEventFactory internalApplicationEventFactory;

	private final InternalApplicationEventConfigurationProperties internalApplicationEventConfigurationProperties;

	private final ApplicationEventActorExtractorInterface applicationEventActorExtractorInterface;

	private final ApplicationEventPublisher applicationEventPublisher;

	@AfterReturning(pointcut = "execution(* com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface.create(..))", returning = "returnValue")
	public void afterCreate(JoinPoint joinPoint, Object returnValue) {
		createAndSendEvent(joinPoint, returnValue, "CREATE");
	}

	@AfterReturning(pointcut = "execution(* com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface.createLinkedEntityWithLinks(..))", returning = "returnValue")
	public void afterCreateLinkedEntityWithLinks(JoinPoint joinPoint, Object returnValue) {
		createAndSendEvent(joinPoint, returnValue, "CREATE-LINK");
	}

	@AfterReturning(pointcut = "execution(* com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface.update(..))", returning = "returnValue")
	public void afterUpdate(JoinPoint joinPoint, Object returnValue) {
		createAndSendEvent(joinPoint, returnValue, "UPDATE");
	}

	@AfterReturning(pointcut = "execution(* com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface.merge(..))", returning = "returnValue")
	public void afterMerge(JoinPoint joinPoint, Object returnValue) {
		createAndSendEvent(joinPoint, returnValue, "MERGE");
	}

	@AfterReturning(pointcut = "execution(* com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface.overwrite(..))", returning = "returnValue")
	public void afterOverwrite(JoinPoint joinPoint, Object returnValue) {
		createAndSendEvent(joinPoint, returnValue, "MERGE");
	}

	@Around("execution(* com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface.delete(..))")
	public void aroundDelete(ProceedingJoinPoint joinPoint) throws Throwable {
		Class declaringType = joinPoint.getTarget().getClass();
		ServiceInterface serviceInterface = (ServiceInterface) ApplicationContextHolder.getApplicationContext().getBean(declaringType);
		if (!EntityServiceDefaultInterface.class.isAssignableFrom(serviceInterface.getClass()))
			throw new InternalAssertionException("Service must implement EntityServiceDefaultInterface");
		EntityServiceDefaultInterface entityServiceDefaultInterface = (EntityServiceDefaultInterface) serviceInterface;
		BaseEntityInterface baseEntityInterface = entityServiceDefaultInterface.getEntityById((Long) joinPoint.getArgs()[0]);
		Event event = createEvent(joinPoint, "DELETE", baseEntityInterface.getClass().getName(), baseEntityInterface, false);
		joinPoint.proceed();
		sendEvent(event);
	}

	@Around("execution(* com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface.deleteWithoutApplicationEventPublish(..))")
	public void aroundDeleteWithoutApplicationEventPublish(ProceedingJoinPoint joinPoint) throws Throwable {
		Class declaringType = joinPoint.getTarget().getClass();
		ServiceInterface serviceInterface = (ServiceInterface) ApplicationContextHolder.getApplicationContext().getBean(declaringType);
		if (!EntityServiceDefaultInterface.class.isAssignableFrom(serviceInterface.getClass()))
			throw new InternalAssertionException("Service must implement EntityServiceDefaultInterface");
		EntityServiceDefaultInterface entityServiceDefaultInterface = (EntityServiceDefaultInterface) serviceInterface;
		BaseEntityInterface baseEntityInterface = entityServiceDefaultInterface.getEntityById((Long) joinPoint.getArgs()[0]);
		Event event = createEvent(joinPoint, "DELETE-WITHOUT-APPLICATION-EVENT-PUBLICATION", baseEntityInterface.getClass().getName(), baseEntityInterface, false);
		joinPoint.proceed();
		sendEvent(event);
	}

	@AfterReturning(pointcut = "execution(* com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface.performCaseAction(..))", returning = "returnValue")
	public void afterPerformCaseAction(JoinPoint joinPoint, Object returnValue) {
		createAndSendEvent(joinPoint, returnValue, "PERFORM-CASE-ACTION");
	}

	private Event createEvent(JoinPoint joinPoint, String eventType, String eventEntity, Object data, boolean isAsync) {
		try {
			return internalApplicationEventFactory.createEvent(joinPoint, eventType, eventEntity, data, getActor(), new Date().getTime());
		}
		catch (Exception e) {
			if (isAsync) {
				log.warn("", e);
				return null;
			}
			else {
				e.printStackTrace();
				throw new InternalAssertionException("Not Able To Send Message as create event failed");
			}
		}
	}

	public void createAndSendEvent(JoinPoint joinPoint, Object returnValue, String type) {
		try {
			User actor = getActor();
			Long timestamp = new Date().getTime();
			if (internalApplicationEventConfigurationProperties.getAsynchronous()) {
				Thread newThread = new Thread(() -> {
					Event event = internalApplicationEventFactory.createEvent(joinPoint, type, returnValue.getClass().getName(), returnValue, actor, timestamp);
					applicationEventPublisher.publishEvent(event);
				});
				newThread.start();
			}
			else {
				Event event = internalApplicationEventFactory.createEvent(joinPoint, type, returnValue.getClass().getName(), returnValue, actor, timestamp);
				applicationEventPublisher.publishEvent(event);
			}
		}
		catch (Exception e) {
			throw new InternalAssertionException("Not Able To Send Message- publishing event failed");
		}
	}

	public void sendEvent(Event event) {
		try {
			if (internalApplicationEventConfigurationProperties.getAsynchronous()) {
				Thread newThread = new Thread(() -> applicationEventPublisher.publishEvent(event));
				newThread.start();
			}
			else {
				applicationEventPublisher.publishEvent(event);
			}
		}
		catch (Exception e) {
			log.warn("", e);
			throw new InternalAssertionException("Not Able To Send Message");
		}
	}

	private User getActor() {
		return applicationEventActorExtractorInterface.getApplicationEventActor();
	}
}
