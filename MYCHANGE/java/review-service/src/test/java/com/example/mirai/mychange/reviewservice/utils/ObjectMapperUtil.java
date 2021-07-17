package com.example.mirai.projectname.reviewservice.utils;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.util.CaseUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.lang.reflect.Field;
import java.util.*;

public class ObjectMapperUtil {
    public static com.fasterxml.jackson.databind.ObjectMapper getObjectMapper() {
        return ApplicationContextHolder.getApplicationContext().getBean(com.fasterxml.jackson.databind.ObjectMapper.class);
    }

    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return getObjectMapper().convertValue(fromValue, toValueType);
    }

    public static <T> T treeToValue(TreeNode treeNode, Class<T> valueType) throws JsonProcessingException {
        return getObjectMapper().treeToValue(treeNode, valueType);
    }

    public static JsonNode valueToTree(Object value) {
        return getObjectMapper().valueToTree(value);
    }

    public static <T> T treeToArray(TreeNode treeNode) throws JsonProcessingException {
        return (T) getObjectMapper().treeToValue(treeNode, ArrayList.class);
    }

    public static String writeValueAsString(Object value) throws JsonProcessingException {
        return getObjectMapper().writeValueAsString(value);
    }

    public static Object getValue(String json, String property) {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        Map<String, Object> map = objectMapper.convertValue(json, Map.class);

        return getValue(map, property);
    }

    public static Object getValue(Map properties, String property) {
        if (getObjectMapper().getPropertyNamingStrategy().equals(PropertyNamingStrategy.SNAKE_CASE))
            property = CaseUtil.convertCamelToSnakeCase(property);
        if (property.indexOf('.') == -1)
            return properties.get(property);
        property.substring(0, property.indexOf('.'));
        return getValue((Map) properties.get(property.substring(0, property.indexOf('.'))), property.substring(property.indexOf('.') + 1));
    }

    public static Object convertMapToClass(Map map, Class clazz) {
/*        com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
        om.setVisibility(om.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));*/
        return getObjectMapper().convertValue(map, clazz);
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

    public static ObjectNode createObjectNode() {
        return getObjectMapper().createObjectNode();
    }

    public static List<String> getChangedProperties(BaseEntityInterface entity1, BaseEntityInterface entity2) throws IllegalAccessException {
        List<String> changedProperties = new ArrayList<>();
        for (Field field : entity1.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value1 = field.get(entity1);
            Object value2 = field.get(entity2);
            if (value1 != null && value2 != null) {
                System.out.println(field.getName() + "=" + value1);
                System.out.println(field.getName() + "=" + value2);
                if (!Objects.equals(value1, value2)) {
                    changedProperties.add(field.getName());
                }
            } else if ((Objects.isNull(value1) && Objects.nonNull(value2)) || (Objects.nonNull(value1) && Objects.isNull(value2))) {
                changedProperties.add(field.getName());
            }
        }
        return changedProperties;
    }
}

