package com.example.mirai.projectname.notificationservice.engine.processor.releasepackage.model;


import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AutomaticClosureJson {

    Map automaticClosureJson;

    public AutomaticClosureJson(Map automaticClosureJson) {
        this.automaticClosureJson = automaticClosureJson;
    }

    ObjectMapper getObjectMapper() {
        return ApplicationContextHolder.getApplicationContext().getBean(ObjectMapper.class);
    }

    public JSONArray getAutomaticClosureErrors(Map data) {
        JSONArray automaticClosureErrors = getObjectMapper().convertValue(JsonPath.parse(automaticClosureJson).read("$.automatic_closure_errors"), JSONArray.class);
        return automaticClosureErrors;
    }

    public List<String> getAutomaticClosureErrorValues(Map data) {
        JSONArray automaticClosureErrors = getAutomaticClosureErrors(data);
        List<String> mailContent = new ArrayList<>();
        for (int i = 0; i < automaticClosureErrors.size(); i++) {
            String releasePackageNumber = getReleasePackageNumber((Map) automaticClosureErrors.get(i));
            String ecn = getEcnId((Map) automaticClosureErrors.get(i));
            String errorMessage = getErrorMessage((Map) automaticClosureErrors.get(i));
            mailContent.add("Release Package Number - " + releasePackageNumber + ", ECN number - " + ecn + ", Error Message - " + errorMessage);
        }
        return mailContent;
    }

    public String getReleasePackageNumber(Map error) {
        return JsonPath.parse(error).read("$.release_package_number");
    }

    public String getEcnId(Map error) {
        return JsonPath.parse(error).read("$.ecn_id");
    }

    public String getErrorMessage(Map error) {
        return JsonPath.parse(error).read("$.error_message");
    }

    public List<String> getEmailIds(Map data) {
        JSONArray automaticClosureErrors = getAutomaticClosureErrors(data);
        List<String> emailIdsInItem;
        List<String> emailIds = new ArrayList<>();
        for (Object automaticClosureError : automaticClosureErrors) {
            if (Objects.nonNull(automaticClosureError) && Objects.nonNull(JsonPath.parse(automaticClosureError).read("$.recipient_mail_ids"))) {
                emailIdsInItem = getObjectMapper().convertValue(JsonPath.parse(automaticClosureError).read("$.recipient_mail_ids"), ArrayList.class);
                emailIds.addAll(emailIdsInItem);
            }
        }
        return emailIds.stream().distinct().collect(Collectors.toList());
    }

    public String getReleasePackageNumberForEmailSubject(Map data) {
        JSONArray automaticClosureErrors = getAutomaticClosureErrors(data);
        List<String> values = new ArrayList<>();
        LinkedHashMap finalMap = new LinkedHashMap();
        for (int i = 0; i < automaticClosureErrors.size(); i++) {
            finalMap = (LinkedHashMap) automaticClosureErrors.get(i);
            values.add(JsonPath.parse(finalMap).read("$.release_package_number"));
        }
        return values.toString();
    }

    public String getEcnIdForEmailSubject(Map data) {
        JSONArray automaticClosureErrors = getAutomaticClosureErrors(data);
        List<String> values = new ArrayList<>();
        LinkedHashMap finalMap = new LinkedHashMap();
        for (int i = 0; i < automaticClosureErrors.size(); i++) {
            finalMap = (LinkedHashMap) automaticClosureErrors.get(i);
            values.add(JsonPath.parse(finalMap).read("$.ecn_id"));
        }
        return values.toString();
    }

}
