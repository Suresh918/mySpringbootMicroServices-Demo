package com.example.mirai.projectname.releasepackageservice.fixtures;


import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDateTime;
import java.util.List;

public class JsonNodeFactory {

    public static JsonNode getFieldUpdateRequestForString(String property, String oldValue) {
        String newValue = oldValue + "_updated";
        ObjectNode fieldUpdateRequest = ObjectMapperUtil.getObjectMapper().createObjectNode();
        fieldUpdateRequest.set("oldIns", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("newIns", ObjectMapperUtil.getObjectMapper().createObjectNode());
        ((ObjectNode) fieldUpdateRequest.get("oldIns")).put(property, oldValue);
        ((ObjectNode) fieldUpdateRequest.get("newIns")).put(property, newValue);
        return fieldUpdateRequest;
    }

    public static JsonNode getFieldUpdateRequestForListOfString(String property, List<String> oldValues) {
        ObjectNode fieldUpdateRequest = ObjectMapperUtil.getObjectMapper().createObjectNode();
        fieldUpdateRequest.set("oldIns", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("newIns", ObjectMapperUtil.getObjectMapper().createObjectNode());
        ArrayNode oldIns = ((ObjectNode) fieldUpdateRequest.get("oldIns")).putArray(property);
            oldValues.forEach(val -> oldIns.add(val));
        ArrayNode newIns = ((ObjectNode) fieldUpdateRequest.get("newIns")).putArray(property);
            oldValues.forEach(val -> newIns.add(val + "-new"));
        return fieldUpdateRequest;
    }

    public static JsonNode getFieldUpdateRequestForInteger(String property, Integer oldValue) {
        Integer newValue = oldValue + 1;
        ObjectNode fieldUpdateRequest = ObjectMapperUtil.getObjectMapper().createObjectNode();
        fieldUpdateRequest.set("oldIns", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("newIns", ObjectMapperUtil.getObjectMapper().createObjectNode());
        ((ObjectNode) fieldUpdateRequest.get("oldIns")).put(property, oldValue);
        ((ObjectNode) fieldUpdateRequest.get("newIns")).put(property, newValue);
        return fieldUpdateRequest;
    }

    public static JsonNode getFieldUpdateRequestForBoolean(String property, Boolean oldValue) {
        Boolean newValue = oldValue;//TODO
        ObjectNode fieldUpdateRequest = ObjectMapperUtil.getObjectMapper().createObjectNode();
        fieldUpdateRequest.set("oldIns", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("newIns", ObjectMapperUtil.getObjectMapper().createObjectNode());
        ((ObjectNode) fieldUpdateRequest.get("oldIns")).put(property, oldValue);
        ((ObjectNode) fieldUpdateRequest.get("newIns")).put(property, newValue);
        return fieldUpdateRequest;
    }

    public static JsonNode getFieldUpdateRequestForUser(String property, User oldValue) {
        ObjectNode fieldUpdateRequest = ObjectMapperUtil.getObjectMapper().createObjectNode();
        fieldUpdateRequest.set("oldIns", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("newIns", ObjectMapperUtil.getObjectMapper().createObjectNode());
        User userToUpdate = new User();
        userToUpdate.setFullName(oldValue.getFullName() + "_updated");
        userToUpdate.setUserId(oldValue.getUserId() + "_updated");
        userToUpdate.setAbbreviation(oldValue.getAbbreviation() + "_updated");
        userToUpdate.setEmail(oldValue.getEmail() + "_updated");
        userToUpdate.setDepartmentName(oldValue.getDepartmentName() + "_updated");
        ((ObjectNode) fieldUpdateRequest.get("oldIns")).set(property, ObjectMapperUtil.getObjectMapper().convertValue(oldValue, JsonNode.class));
        ((ObjectNode) fieldUpdateRequest.get("newIns")).set(property, ObjectMapperUtil.getObjectMapper().convertValue(userToUpdate, JsonNode.class));
        return fieldUpdateRequest;
    }

    public static JsonNode getFieldUpdateRequestForLocalDateTime(String property, LocalDateTime oldValueLocalDateTime) {
        LocalDateTime newValueLocalDateTime = oldValueLocalDateTime.plusDays(1);
        String oldValue = oldValueLocalDateTime.toString();
        String newValue = newValueLocalDateTime.toString();
        ObjectNode fieldUpdateRequest = ObjectMapperUtil.getObjectMapper().createObjectNode();
        fieldUpdateRequest.set("oldIns", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("newIns", ObjectMapperUtil.getObjectMapper().createObjectNode());
        ((ObjectNode) fieldUpdateRequest.get("oldIns")).put(property, oldValue);
        ((ObjectNode) fieldUpdateRequest.get("newIns")).put(property, newValue);
        return fieldUpdateRequest;
    }

    public static JsonNode getCommentFieldUpdateRequestForString(String property, String oldValue) throws JsonProcessingException {
        String newValue = oldValue + "_updated";
        ObjectNode fieldUpdateRequest = ObjectMapperUtil.getObjectMapper().createObjectNode();
        fieldUpdateRequest.put("comment_text",newValue);
        return fieldUpdateRequest;
    }

}
