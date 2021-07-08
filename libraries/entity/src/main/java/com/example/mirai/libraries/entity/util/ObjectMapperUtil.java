package com.example.mirai.libraries.entity.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.fasterxml.jackson.databind.JsonNode;

public class ObjectMapperUtil {
	public static com.fasterxml.jackson.databind.ObjectMapper getObjectMapper() {
		return ApplicationContextHolder.getApplicationContext().getBean(com.fasterxml.jackson.databind.ObjectMapper.class);
	}

	public static Map<String, Object> getChangedAttributes(JsonNode jsonNode) {
		Map<String, Object> changedAttrs = new HashMap<>();
		Iterator iterator = jsonNode.fieldNames();
		while (iterator.hasNext()) {
			String propName = (String) iterator.next();
			Object propValue = jsonNode.get(propName);
			changedAttrs.put(propName, propValue);
		}

		return changedAttrs;
	}

	public static List<String> getChangedAttributeNames(JsonNode jsonNode) {
		Iterator iterator = jsonNode.fieldNames();
		List<String> changedAttributeNames = new ArrayList<>();
		while (iterator.hasNext()) {
			String propName = (String) iterator.next();
			changedAttributeNames.add(propName);
		}
		return changedAttributeNames;
	}

	public static List<String> getChangedProperties(BaseEntityInterface entity1, BaseEntityInterface entity2) throws IllegalAccessException {
		return getChangedProperties(entity1, entity2, entity1.getClass());
	}

	public static List<String> getChangedProperties(BaseEntityInterface entity1, BaseEntityInterface entity2, Class<? extends BaseEntityInterface> entityClass) throws IllegalAccessException {
		List<String> changedProperties = new ArrayList<>();
		for (Field field : entityClass.getDeclaredFields()) {
			field.setAccessible(true);
			Object value1 = field.get(entity1);
			Object value2 = field.get(entity2);
			if (value1 != null && value2 != null) {
				if (!Objects.equals(value1, value2)) {
					changedProperties.add(field.getName());
				}
			}
			else if ((Objects.isNull(value1) && Objects.nonNull(value2)) || (Objects.nonNull(value1) && Objects.isNull(value2))) {
				changedProperties.add(field.getName());
			}
		}
		return changedProperties;
	}

}
