package com.example.mirai.libraries.event.aspect;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import javax.jms.JMSException;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.core.service.ServiceInterface;
import com.example.mirai.libraries.event.EventActorExtractorInterface;
import com.example.mirai.libraries.event.EventBuilderInterface;
import com.example.mirai.libraries.event.annotation.PublishResponse;
import com.example.mirai.libraries.event.component.EventFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@AllArgsConstructor
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${mirai.libraries.jms.url:}')")
public class ResponsePublisherAspect {
	private final EventFactory eventFactory;

	private final JmsTemplate jmsTemplate;

	private final ObjectMapper objectMapper;

	@Around("@annotation(publishResponse)")
	public Object afterOperationPublishResponse(ProceedingJoinPoint point, PublishResponse publishResponse) throws Throwable {
		Signature signature = point.getSignature();
		Class declaringType = signature.getDeclaringType();
		ServiceInterface baseServiceInterface = (ServiceInterface) ApplicationContextHolder.getApplicationContext().getBean(declaringType);

		if (ServiceInterface.class.isAssignableFrom(declaringType) && signature.getName().equals("delete")) {
			BaseEntityInterface baseEntityInterface;
			if (point.getArgs()[0] instanceof BaseEntityInterface) {
				baseEntityInterface = (BaseEntityInterface) point.getArgs()[0];
			}
			else {
				baseEntityInterface = baseServiceInterface.getEntityById((Long) point.getArgs()[0]);
			}

			String eventEntity = publishResponse.eventEntity();
			if (eventEntity.equals("[]"))
				eventEntity = baseEntityInterface.getClass().getName();
			Event event = createEvent(point, baseEntityInterface, publishResponse.isAsync(), publishResponse.eventBuilder(),
					publishResponse.responseClass(), publishResponse.eventType(), eventEntity, Optional.empty());
			Object retVal = point.proceed();
			sendEvent(event, publishResponse.destination(), publishResponse.isAsync());
			return retVal;
		}
		else {
			BaseEntityInterface entityBeforeAction = null;
			if (point.getArgs().length > 0 && point.getArgs()[0] instanceof BaseEntityInterface && Objects.nonNull(((BaseEntityInterface) point.getArgs()[0]).getId())) {
				entityBeforeAction = baseServiceInterface.getEntityById(((BaseEntityInterface) point.getArgs()[0]).getId());
			}
			Object retVal = point.proceed();
			if (Objects.nonNull(retVal)) {
				String eventEntity = publishResponse.eventEntity();
				if (eventEntity.equals("[]"))
					eventEntity = retVal.getClass().getName();
				// if list of entities are returned
				if (!(retVal instanceof BaseEntityInterface) && Collection.class.isAssignableFrom(retVal.getClass())) {
					Collection collectionOfEntities = (Collection) retVal;
					for (Object entity : collectionOfEntities) {
						eventEntity = entity.getClass().getName();
						Event event = createEvent(point, entity, publishResponse.isAsync(), publishResponse.eventBuilder(),
								publishResponse.responseClass(), publishResponse.eventType(), eventEntity, Optional.empty());
						sendEvent(event, publishResponse.destination(), publishResponse.isAsync());
					}
				}
				else {
					Optional<BaseEntityInterface> optionalEntityBeforeAction = Objects.nonNull(entityBeforeAction) ? Optional.of(entityBeforeAction) : Optional.empty();
					Event event = createEvent(point, retVal, publishResponse.isAsync(), publishResponse.eventBuilder(),
							publishResponse.responseClass(), publishResponse.eventType(), eventEntity, optionalEntityBeforeAction);
					sendEvent(event, publishResponse.destination(), publishResponse.isAsync());
				}
			}
			return retVal;
		}
	}

	private User getActor() {
		EventActorExtractorInterface eventActorExtractor = ApplicationContextHolder.getApplicationContext().getBean(EventActorExtractorInterface.class);
		if (eventActorExtractor == null)
			return null;
		return eventActorExtractor.getEventActor();
	}

	private Event createEvent(JoinPoint joinPoint, Object retVal, boolean isAsync,
			Class<EventBuilderInterface> eventBuilderClass, Class responseClass, String eventType, String eventEntity, Optional<BaseEntityInterface> entityBeforeAction) {
		try {
			return eventFactory.createEvent(joinPoint, eventType, eventEntity, retVal,
					eventBuilderClass, responseClass, getActor(), new Date().getTime(), entityBeforeAction);
		}
		catch (Exception e) {
			if (isAsync) {
				log.warn("", e);
				return null;
			}
			else {
				e.printStackTrace();
				log.info("not able to create message " + e.getMessage());
				throw new InternalAssertionException("Not Able To create Message");
			}
		}
	}

	private void sendEvent(Event event, String destination, boolean isAsync) {
		try {
			if (isAsync) {
				Thread newThread = new Thread(() -> {
					try {
						if (event != null) {
							createAndSendMessage(destination, event);
						}
					}
					catch (JsonProcessingException e) {
						log.warn("", e);
						throw new InternalAssertionException("Not Able To Convert Message");
					}
				});
				newThread.start();
			}
			else if (event != null) {
				createAndSendMessage(destination, event);
			}
			else {
				throw new InternalAssertionException("Not Able To Send Message as event is invalid");
			}
		}
		catch (Exception e) {
			log.warn("", e);
			throw new InternalAssertionException("Not Able To Send Message- send event failed");
		}
	}

	private void createAndSendMessage(String destination, Event event) throws JsonProcessingException {
		String messageAsString = objectMapper.writeValueAsString(event);
		jmsTemplate.convertAndSend(destination, messageAsString, new MessagePostProcessor() {
			@Override
			public javax.jms.Message
			postProcessMessage(javax.jms.Message message) throws JMSException {
				message.setStringProperty("type", event.getType());
				message.setStringProperty("status", event.getStatus());
				message.setStringProperty("entity", event.getEntity());
				message.setStringProperty("payload", event.getPayload());
				message.setLongProperty("timestamp", event.getTimestamp());
				message.setBooleanProperty("JMS_TIBCO_PRESERVE_UNDELIVERED", true);
				log.info("Notification message " + message);
				return message;
			}
		});
	}
}
