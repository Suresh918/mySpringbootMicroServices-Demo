package com.example.mirai.libraries.entity.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.example.mirai.libraries.core.annotation.SecureCaseAction;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.core.service.ServiceInterface;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

@Data
@Component
public class InternalApplicationEventFactory {
	private final ObjectMapper objectMapper;

	public Event createEvent(JoinPoint joinPoint, String eventType, String eventEntity, Object data, User actor, Long timestamp) {
		Map changedAttributes = getChangedAttributes(joinPoint, data);
		String tempEventType = getEventType(joinPoint, eventType);
		return new Event(tempEventType, "SUCCESS", eventEntity,
				data.getClass().getName(), actor, data, changedAttributes, timestamp);
	}


	private String getEventType(JoinPoint joinPoint, String type) {
		if (type.equals("PERFORM-CASE-ACTION")) {
			Object[] args = joinPoint.getArgs();
			//in perform case action method, 1st argument index-0 is id and second argument index-1 is case action
			String caseAction = (String) args[1];
		}
		return type;
	}

	private Map<String, Map<String, Object>> getChangedAttributes(JoinPoint joinPoint, Object retVal) {
		Map changedAttributes = null;
		changedAttributes = getChangedAttributesForMerge(joinPoint);
		if (changedAttributes == null)
			changedAttributes = getChangedAttributesForPerformCaseAction(joinPoint, retVal);
		return changedAttributes;
	}

	private Map<String, Map<String, Object>> getChangedAttributesForMerge(JoinPoint joinPoint) {
		Signature signature = joinPoint.getSignature();
		Class declaringType = signature.getDeclaringType();
		Map<String, Map<String, Object>> changedAttributes = null;

		//method is a member of ServiceInterface and is merge
		if (ServiceInterface.class.isAssignableFrom(declaringType) && signature.getName().equals("merge")) {
			Object[] parameters = joinPoint.getArgs();
			changedAttributes = new HashMap<>();

			BaseEntityInterface newInst = (BaseEntityInterface) parameters[0];
			BaseEntityInterface oldInst = (BaseEntityInterface) parameters[1];
			List<String> oldInsChangedAttributeNames = (List<String>) parameters[2];
			List<String> newInsChangedAttributeNames = (List<String>) parameters[3];
			String oldValueKey = "oldValue";
			String newValueKey = "newValue";
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
