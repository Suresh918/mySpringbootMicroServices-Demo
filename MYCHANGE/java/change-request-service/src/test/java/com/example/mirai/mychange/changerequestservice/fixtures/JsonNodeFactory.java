package com.example.mirai.projectname.changerequestservice.fixtures;


import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.testcontainers.shaded.com.google.common.collect.ObjectArrays;

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

    public static JsonNode getFieldUpdateRequestForlinkpbs() {
        ObjectNode fieldUpdateRequest = ObjectMapperUtil.getObjectMapper().createObjectNode();
        ObjectNode fieldUpdateRequest1 = ObjectMapperUtil.getObjectMapper().createObjectNode();
        fieldUpdateRequest.set("sources", ObjectMapperUtil.getObjectMapper().createArrayNode());
        fieldUpdateRequest1.set("id", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest1.set("type", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest1.set("action", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest1.put("id","10000");
        fieldUpdateRequest1.put("type","PBS");
        fieldUpdateRequest1.put("action","WRITE_IF_EMPTY");
        ((ArrayNode) fieldUpdateRequest.get("sources")).add(fieldUpdateRequest1);
        return fieldUpdateRequest;
    }

    public static JsonNode getFieldUpdateRequestForLinkAir() {
        ObjectNode fieldUpdateRequest = ObjectMapperUtil.getObjectMapper().createObjectNode();
        ObjectNode fieldUpdateRequest1 = ObjectMapperUtil.getObjectMapper().createObjectNode();
        fieldUpdateRequest.set("sources", ObjectMapperUtil.getObjectMapper().createArrayNode());
        fieldUpdateRequest1.set("id", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest1.set("type", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest1.set("action", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest1.put("id","P11110");
        fieldUpdateRequest1.put("type","AIR");
        fieldUpdateRequest1.put("action","LINK_ONLY");
        ((ArrayNode) fieldUpdateRequest.get("sources")).add(fieldUpdateRequest1);
        return fieldUpdateRequest;
    }

    public static JsonNode getMyTeamBulkUpdateAddRequest(List<Long> idsList, Boolean isAllSelected) {
        ObjectNode fieldUpdateRequest = ObjectMapperUtil.getObjectMapper().createObjectNode();

        fieldUpdateRequest.set("case_object_ids", ObjectMapperUtil.getObjectMapper().createArrayNode());
        if(!isAllSelected) {
            ((ArrayNode) fieldUpdateRequest.get("case_object_ids")).add(idsList.get(0));
            ((ArrayNode) fieldUpdateRequest.get("case_object_ids")).add(idsList.get(1));
        }
        fieldUpdateRequest.set("view_criteria", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("criteria", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("role", ObjectMapperUtil.getObjectMapper().createObjectNode());
        if(isAllSelected) {
            fieldUpdateRequest.put("view_criteria", "id@" + idsList.get(0) + "," + idsList.get(1));
        }else{
            fieldUpdateRequest.put("view_criteria","");
        }
        fieldUpdateRequest.put("criteria","");
        fieldUpdateRequest.put("role","businessController");

        fieldUpdateRequest.set("user_to_add", ObjectMapperUtil.getObjectMapper().createObjectNode());
        ((ObjectNode) fieldUpdateRequest.get("user_to_add")).put("user_id", "mychange05");
        ((ObjectNode) fieldUpdateRequest.get("user_to_add")).put("full_name", "projectname 05");
        ((ObjectNode) fieldUpdateRequest.get("user_to_add")).put("email", "mychange05@example.net");
        ((ObjectNode) fieldUpdateRequest.get("user_to_add")).put("abbreviation", "MYC5");
        ((ObjectNode) fieldUpdateRequest.get("user_to_add")).put("department_name", "MD DE PLM C Configuration Mgmt");

        return fieldUpdateRequest;
    }

    public static JsonNode getMyTeamBulkUpdateRemoveRequest(List<Long> idsList,String dataIdentifier, Boolean isAllSelected ) {
        ObjectNode fieldUpdateRequest = ObjectMapperUtil.getObjectMapper().createObjectNode();
        fieldUpdateRequest.set("case_object_ids", ObjectMapperUtil.getObjectMapper().createArrayNode());
        if(!isAllSelected) {
            ((ArrayNode) fieldUpdateRequest.get("case_object_ids")).add(idsList.get(0));
            //((ArrayNode) fieldUpdateRequest.get("case_object_ids")).add(idsList.get(1));
        }
        fieldUpdateRequest.set("view_criteria", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("criteria", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("role", ObjectMapperUtil.getObjectMapper().createObjectNode());
        if(isAllSelected) {
            fieldUpdateRequest.put("view_criteria", "id@"+idsList.get(0));
        }else{
            fieldUpdateRequest.put("view_criteria", "");

        }
        fieldUpdateRequest.put("criteria","");
        fieldUpdateRequest.put("role","businessController");

        fieldUpdateRequest.set("user_to_remove", ObjectMapperUtil.getObjectMapper().createObjectNode());
        ((ObjectNode) fieldUpdateRequest.get("user_to_remove")).put("user_id", dataIdentifier + "_my_team_member_user_id");
        ((ObjectNode) fieldUpdateRequest.get("user_to_remove")).put("full_name", dataIdentifier + "_my_team_member_full_name");
        ((ObjectNode) fieldUpdateRequest.get("user_to_remove")).put("email", dataIdentifier + "_my_team_member_email");
        ((ObjectNode) fieldUpdateRequest.get("user_to_remove")).put("abbreviation", dataIdentifier + "_my_team_member-abbreviation");
        ((ObjectNode) fieldUpdateRequest.get("user_to_remove")).put("department_name", dataIdentifier + "_my_team_member_department_name");

        return fieldUpdateRequest;
    }


    public static JsonNode getMyTeamBulkUpdateReplaceRequest(List<Long> idsList,String dataIdentifier, Boolean isAllSelected ) {
        ObjectNode fieldUpdateRequest = ObjectMapperUtil.getObjectMapper().createObjectNode();

        fieldUpdateRequest.set("case_object_ids", ObjectMapperUtil.getObjectMapper().createArrayNode());
        if(!isAllSelected) {
            ((ArrayNode) fieldUpdateRequest.get("case_object_ids")).add(idsList.get(0));
            //((ArrayNode) fieldUpdateRequest.get("case_object_ids")).add(idsList.get(1));
        }
        fieldUpdateRequest.set("view_criteria", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("criteria", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("role", ObjectMapperUtil.getObjectMapper().createObjectNode());
        if(isAllSelected) {
            fieldUpdateRequest.put("view_criteria", "id@"+idsList.get(0));
        }else{
            fieldUpdateRequest.put("view_criteria", "");
        }
        fieldUpdateRequest.put("criteria","");
        fieldUpdateRequest.put("role","businessController");

        fieldUpdateRequest.set("user_to_add", ObjectMapperUtil.getObjectMapper().createObjectNode());
        ((ObjectNode) fieldUpdateRequest.get("user_to_add")).put("user_id", "mychange05");
        ((ObjectNode) fieldUpdateRequest.get("user_to_add")).put("full_name", "projectname 05");
        ((ObjectNode) fieldUpdateRequest.get("user_to_add")).put("email", "mychange05@example.net");
        ((ObjectNode) fieldUpdateRequest.get("user_to_add")).put("abbreviation", "MYC5");
        ((ObjectNode) fieldUpdateRequest.get("user_to_add")).put("department_name", "MD DE PLM C Configuration Mgmt");


        fieldUpdateRequest.set("user_to_remove", ObjectMapperUtil.getObjectMapper().createObjectNode());
        ((ObjectNode) fieldUpdateRequest.get("user_to_remove")).put("user_id", dataIdentifier + "_my_team_member_user_id");
        ((ObjectNode) fieldUpdateRequest.get("user_to_remove")).put("full_name", dataIdentifier + "_my_team_member_full_name");
        ((ObjectNode) fieldUpdateRequest.get("user_to_remove")).put("email", dataIdentifier + "_my_team_member_email");
        ((ObjectNode) fieldUpdateRequest.get("user_to_remove")).put("abbreviation", dataIdentifier + "_my_team_member-abbreviation");
        ((ObjectNode) fieldUpdateRequest.get("user_to_remove")).put("department_name", dataIdentifier + "_my_team_member_department_name");

        return fieldUpdateRequest;
    }

    public static JsonNode getFieldUpdateRequestForUpdateScope() {

        ObjectNode fieldUpdateRequest = ObjectMapperUtil.getObjectMapper().createObjectNode();
        fieldUpdateRequest.set("scope_details", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("parts", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.put("scope_details","daeas");
        fieldUpdateRequest.put("parts","IN-SCOPE");

        fieldUpdateRequest.set("part_detail", ObjectMapperUtil.getObjectMapper().createObjectNode());
        ((ObjectNode) fieldUpdateRequest.get("part_detail")).put("machine_bom_part", "IN-SCOPE");
        ((ObjectNode) fieldUpdateRequest.get("part_detail")).put("service_part", "OUT-SCOPE");
        ((ObjectNode) fieldUpdateRequest.get("part_detail")).put("preinstall_part", "OUT-SCOPE");
        ((ObjectNode) fieldUpdateRequest.get("part_detail")).put("test_rig_part", "OUT-SCOPE");
        ((ObjectNode) fieldUpdateRequest.get("part_detail")).put("dev_bag_part", "OUT-SCOPE");
        ((ObjectNode) fieldUpdateRequest.get("part_detail")).put("fco_upgrade_option_csr", "OUT-SCOPE");

        fieldUpdateRequest.set("tooling", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.put("tooling","IN-SCOPE");
        fieldUpdateRequest.set("tooling_detail", ObjectMapperUtil.getObjectMapper().createObjectNode());
        ((ObjectNode) fieldUpdateRequest.get("tooling_detail")).put("supplier_tooling", "IN-SCOPE");
        ((ObjectNode) fieldUpdateRequest.get("tooling_detail")).put("manufacturing_de_tooling", "IN-SCOPE");
        ((ObjectNode) fieldUpdateRequest.get("tooling_detail")).put("service_tooling", "IN-SCOPE");

        fieldUpdateRequest.set("packaging", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("bop", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.put("packaging","OUT-SCOPE");
        fieldUpdateRequest.put("bop","OUT-SCOPE");

        return fieldUpdateRequest;
    }

    public static JsonNode getJsonRequestForCreateScia(ChangeRequest changeRequest) {

        ObjectNode fieldUpdateRequest = ObjectMapperUtil.getObjectMapper().createObjectNode();
        /*fieldUpdateRequest.set("id", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.put("id","10");*/
        fieldUpdateRequest.set("title", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.put("title",changeRequest.getTitle());
        fieldUpdateRequest.set("contexts", ObjectMapperUtil.getObjectMapper().createArrayNode());
        ObjectNode fieldUpdateRequest1 = ObjectMapperUtil.getObjectMapper().createObjectNode();
        fieldUpdateRequest1.set("type", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest1.set("context_id", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest1.set("name", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest1.set("status", ObjectMapperUtil.getObjectMapper().createObjectNode());
         fieldUpdateRequest1.put("type", "CHANGEREQUEST");
        fieldUpdateRequest1.put("context_id", ""+changeRequest.getId());
        fieldUpdateRequest1.put("name", changeRequest.getTitle());
         fieldUpdateRequest1.put("status", changeRequest.getStatus());

        ((ArrayNode) fieldUpdateRequest.get("contexts")).add(fieldUpdateRequest1);
        fieldUpdateRequest.set("plm_coordinator", ObjectMapperUtil.getObjectMapper().createObjectNode());
        ((ObjectNode) fieldUpdateRequest.get("plm_coordinator")).put("user_id", "user");
        ((ObjectNode) fieldUpdateRequest.get("plm_coordinator")).put("full_name", "test user");
        ((ObjectNode) fieldUpdateRequest.get("plm_coordinator")).put("email", "test.user@example.com");
        ((ObjectNode) fieldUpdateRequest.get("plm_coordinator")).put("department_name", "IT BAS CC Corporate BPI & Automation");
        ((ObjectNode) fieldUpdateRequest.get("plm_coordinator")).put("abbreviation", "SaIn");

        return fieldUpdateRequest;
    }

    public static JsonNode getFieldUpdateRequestForUpdatePreInstallImpact() {

        ObjectNode fieldUpdateRequest = ObjectMapperUtil.getObjectMapper().createObjectNode();
        fieldUpdateRequest.set("preinstall_impact_result", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("change_introduces_new11_nc", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.put("preinstall_impact_result","MAJOR");
        fieldUpdateRequest.put("change_introduces_new11_nc","yes");

        fieldUpdateRequest.set("change_introduces_new11_nc_details", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("impact_on_customer_factory_layout", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.put("change_introduces_new11_nc_details","details");
        fieldUpdateRequest.put("impact_on_customer_factory_layout","yes");

        fieldUpdateRequest.set("impact_on_facility_flows", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("impact_on_facility_flows_details", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.put("impact_on_facility_flows","yes");
        fieldUpdateRequest.put("impact_on_facility_flows_details","details");

        fieldUpdateRequest.set("impact_on_preinstall_inter_connect_cables", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("impact_on_preinstall_inter_connect_cables_details", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.put("impact_on_preinstall_inter_connect_cables","yes");
        fieldUpdateRequest.put("impact_on_preinstall_inter_connect_cables_details","details");

        fieldUpdateRequest.set("change_replaces_mentioned_parts", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("change_replaces_mentioned_parts_details", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.put("change_replaces_mentioned_parts","yes");
        fieldUpdateRequest.put("change_replaces_mentioned_parts_details","details");

        return fieldUpdateRequest;
    }

    public static JsonNode getFieldUpdateRequestForUnLinkPbs() {
        ObjectNode fieldUpdateRequest = ObjectMapperUtil.getObjectMapper().createObjectNode();
        fieldUpdateRequest.set("id", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("type", ObjectMapperUtil.getObjectMapper().createObjectNode());
        //fieldUpdateRequest.set("action", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.put("id","10000");
        fieldUpdateRequest.put("type","PBS");
        return fieldUpdateRequest;
    }

    public static JsonNode getFieldUpdateRequestForUnLinkAir() {
        ObjectNode fieldUpdateRequest = ObjectMapperUtil.getObjectMapper().createObjectNode();
        fieldUpdateRequest.set("id", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("type", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.put("id","P11110");
        fieldUpdateRequest.put("type","AIR");
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

    public static JsonNode getFieldUpdateRequestFordDependedntChangeRequestIds(String property, List<String> oldValues,List<String> linkIds) {
        ObjectNode fieldUpdateRequest = ObjectMapperUtil.getObjectMapper().createObjectNode();
        fieldUpdateRequest.set("oldIns", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("newIns", ObjectMapperUtil.getObjectMapper().createObjectNode());
        ArrayNode oldIns = ((ObjectNode) fieldUpdateRequest.get("oldIns")).putArray(property);
        oldValues.forEach(val -> oldIns.add(val));
        ArrayNode newIns = ((ObjectNode) fieldUpdateRequest.get("newIns")).putArray(property);
        linkIds.forEach(val -> newIns.add(val));
        return fieldUpdateRequest;
    }

    public static JsonNode getRequestForDependedentChangeRequestIdsforUnLinkCR(String property, List<String> oldValues,List<String> linkIds) {
        ObjectNode fieldUpdateRequest = ObjectMapperUtil.getObjectMapper().createObjectNode();
         fieldUpdateRequest.put(property, linkIds.get(0));
        return fieldUpdateRequest;
    }

    public static JsonNode getFieldUpdateRequestForFloat(String property, Float oldValue) {
        Float newValue = 1f;
        ObjectNode fieldUpdateRequest = ObjectMapperUtil.getObjectMapper().createObjectNode();
        fieldUpdateRequest.set("oldIns", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("newIns", ObjectMapperUtil.getObjectMapper().createObjectNode());
        ((ObjectNode) fieldUpdateRequest.get("oldIns")).put(property, oldValue);
        ((ObjectNode) fieldUpdateRequest.get("newIns")).put(property, newValue);
        return fieldUpdateRequest;
    }

    public static JsonNode getFieldUpdateRequestForInteger(String property, Integer oldValue) {
        Integer newValue = 1;
        ObjectNode fieldUpdateRequest = ObjectMapperUtil.getObjectMapper().createObjectNode();
        fieldUpdateRequest.set("oldIns", ObjectMapperUtil.getObjectMapper().createObjectNode());
        fieldUpdateRequest.set("newIns", ObjectMapperUtil.getObjectMapper().createObjectNode());
        ((ObjectNode) fieldUpdateRequest.get("oldIns")).put(property, oldValue);
        ((ObjectNode) fieldUpdateRequest.get("newIns")).put(property, newValue);
        return fieldUpdateRequest;
    }

    public static JsonNode getFieldUpdateRequestForBoolean(String property, Boolean oldValue) {
        Boolean newValue = true;//TODO
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

    public static JsonNode getCommentFieldUpdateRequestForString(String property, String oldValue) {
        String newValue = oldValue + "_updated";
        ObjectNode fieldUpdateRequest = ObjectMapperUtil.getObjectMapper().createObjectNode();
        fieldUpdateRequest.put("comment_text",newValue);
        return fieldUpdateRequest;
    }
}
