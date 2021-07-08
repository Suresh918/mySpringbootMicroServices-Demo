package com.example.mirai.libraries.event.component;

import java.util.*;
import java.util.concurrent.ExecutionException;

import com.example.mirai.libraries.core.annotation.SecureCaseAction;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.core.service.ServiceInterface;
import com.example.mirai.libraries.event.EventBuilderInterface;
import com.example.mirai.libraries.util.ReflectionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

@Data
@Component
@Slf4j
public class EventFactory {
	private final ObjectMapper objectMapper;

	public Event createEvent(JoinPoint joinPoint, String eventType, String eventEntity, Object data,
							 Class eventBuilderClass, Class responseClass, User actor, Long timestamp, Optional<BaseEntityInterface> entityBeforeAction) throws ExecutionException, InterruptedException {
		Object translatedData = null;
		Map changedAttributes = getChangedAttributes(joinPoint, data, eventType, entityBeforeAction);
		if (EventBuilderInterface.class.isAssignableFrom(eventBuilderClass)) {
			EventBuilderInterface eventBuilderInterface = (EventBuilderInterface) ReflectionUtil.createInstance(eventBuilderClass);
			eventBuilderInterface.setResponseClass(responseClass);
			translatedData = eventBuilderInterface.translateResponse(data);
			return new Event(eventType, "SUCCESS", eventEntity, translatedData.getClass().getName(), actor, translatedData, changedAttributes, timestamp);
		}
		return null;
	}

	private Map<String, Map<String, Object>> getChangedAttributes(JoinPoint joinPoint, Object retVal, String eventType, Optional<BaseEntityInterface> entityBeforeAction) {
		Map changedAttributes = null;
		changedAttributes = getChangedAttributesForMerge(joinPoint, eventType, entityBeforeAction);
		if (changedAttributes == null)
			changedAttributes = getChangedAttributesForPerformCaseAction(joinPoint, retVal);
		return changedAttributes;
	}

	private Map<String, Map<String, Object>> getChangedAttributesForMerge(JoinPoint joinPoint, String eventType, Optional<BaseEntityInterface> entityBeforeAction) {
		Signature signature = joinPoint.getSignature();
		Class declaringType = signature.getDeclaringType();
		Map<String, Map<String, Object>> changedAttributes = null;
		String oldValueKey = "oldValue";
		String newValueKey = "newValue";
		//method is a member of ServiceInterface and is merge
		Boolean isMergeEvent = Objects.equals(eventType.toLowerCase(), "merge") && joinPoint.getArgs().length == 2 && Objects.nonNull(joinPoint.getArgs()[0])
				&& joinPoint.getArgs()[0] instanceof BaseEntityInterface && Objects.nonNull(joinPoint.getArgs()[1]) && joinPoint.getArgs()[1] instanceof BaseEntityInterface;
		if (ServiceInterface.class.isAssignableFrom(declaringType) && isMergeEvent) {
			Object[] parameters = joinPoint.getArgs();
			changedAttributes = new HashMap<>();

			BaseEntityInterface newInst = (BaseEntityInterface) parameters[0];
			BaseEntityInterface oldInst = (BaseEntityInterface) parameters[1];
			List<String> oldInsChangedAttributeNames = (List<String>) parameters[2];
			List<String> newInsChangedAttributeNames = (List<String>) parameters[3];
			Map<String, Object> oldInstMap = objectMapper.convertValue(oldInst, Map.class);
			Map<String, Object> newInstMap = objectMapper.convertValue(newInst, Map.class);

			List<String> fieldNamesPresentInOld = new ArrayList<>();
			for (String fieldName : oldInsChangedAttributeNames) {
				Object oldValue = oldInstMap.get(fieldName);
				Object newValue = newInstMap.get(fieldName);
				Map valuesMap = new HashMap<>();
				valuesMap.put(oldValueKey, oldValue);
				valuesMap.put(newValueKey, newValue);
				changedAttributes.put(fieldName, valuesMap);
				fieldNamesPresentInOld.add(fieldName);
			}

			for (String fieldName : newInsChangedAttributeNames) {
				if (fieldNamesPresentInOld.contains(fieldName))
					continue;
				Map valuesMap = new HashMap<>();
				Object newValue = newInstMap.get(fieldName);
				valuesMap.put(oldValueKey, null);
				valuesMap.put(newValueKey, newValue);
				changedAttributes.put(fieldName, valuesMap);
			}
		}
		log.info("signature name " + signature.getName() + " " + joinPoint.getArgs().length);
		//method is a member of BaseServiceInterface and is update

		Boolean isUpdateEvent = Objects.equals(eventType.toLowerCase(), "update") && joinPoint.getArgs().length == 2 && Objects.nonNull(joinPoint.getArgs()[0])
				&& joinPoint.getArgs()[0] instanceof BaseEntityInterface && Objects.nonNull(joinPoint.getArgs()[1]) && joinPoint.getArgs()[1] instanceof HashMap;
		if (ServiceInterface.class.isAssignableFrom(declaringType) && isUpdateEvent) {
			Object[] parameters = joinPoint.getArgs();
			changedAttributes = new HashMap<>();

			BaseEntityInterface newInst = (BaseEntityInterface) parameters[0];
			BaseEntityInterface oldInst = null;
			if (entityBeforeAction.isPresent())
				oldInst = entityBeforeAction.get();
			else
				oldInst = ( (ServiceInterface)joinPoint.getTarget()).getEntityById(newInst.getId());

			List<String> newInsChangedAttributeNames = objectMapper.convertValue(((HashMap)parameters[1]).keySet(), List.class);
			Map<String, Object> oldInstMap = objectMapper.convertValue(oldInst, Map.class);
			Map<String, Object> newInstMap = objectMapper.convertValue(newInst, Map.class);

			for (String fieldName : newInsChangedAttributeNames) {
				if (newInstMap.containsKey(fieldName)) {
					Object oldValue = oldInstMap.get(fieldName);
					Object newValue = newInstMap.get(fieldName);
					log.info("oldValue " + (Objects.nonNull(oldValue) ? oldValue.toString() : oldValue) + " changed attrs" + changedAttributes.size());
					if (!Objects.deepEquals(oldValue,newValue)) {
						Map valuesMap = new HashMap<>();
						valuesMap.put(oldValueKey, oldValue);
						valuesMap.put(newValueKey, newValue);
						changedAttributes.put(fieldName, valuesMap);
					}
				}
			}
		}
		return changedAttributes;
	}

	//TODO change to send merged attributes
	private Map<String, Map<String, Object>> getChangedAttributesForPerformCaseAction(JoinPoint joinPoint, Object returnValue) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Class declaringType = signature.getDeclaringType();
		Map<String, Map<String, Object>> changedAttributes = null;
		String statusKey = "status";
		Boolean doesMethodPerformCaseAction = false;
		SecureCaseAction secureCaseAction = AnnotationUtils.findAnnotation(signature.getMethod(), SecureCaseAction.class);
		if (Objects.nonNull(secureCaseAction)) {
			doesMethodPerformCaseAction = Objects.nonNull(secureCaseAction.value());
		}
		//method is a member of ServiceInterface and is perform case action
		if (ServiceInterface.class.isAssignableFrom(declaringType) && BaseEntityInterface.class.isAssignableFrom(returnValue.getClass()) && doesMethodPerformCaseAction) {
			BaseEntityInterface entity = (BaseEntityInterface) returnValue;
			changedAttributes = new HashMap<>();
			Map<String, Object> map = objectMapper.convertValue(entity, Map.class);

			if (Objects.isNull(map.get(statusKey)))
				return null;

			Map valuesMap = new HashMap<>();
			valuesMap.put("oldValue", "UNKNOWN");
			valuesMap.put("newValue", map.get(statusKey));
			changedAttributes.put(statusKey, valuesMap);

			return Objects.isNull(map.get(statusKey)) ? null : changedAttributes;
		}
		return null;
	}
}
