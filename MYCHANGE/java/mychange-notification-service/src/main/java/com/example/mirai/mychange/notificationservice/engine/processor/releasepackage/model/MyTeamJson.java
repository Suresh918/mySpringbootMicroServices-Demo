package com.example.mirai.projectname.notificationservice.engine.processor.releasepackage.model;


import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MyTeamJson {
    String myTeamJson;

    public MyTeamJson(String myTeamJson) {
        this.myTeamJson = myTeamJson;
    }

    public List<User> getReleasePackageMyTeamMembers() {
        List<Object> membersList = getObjectMapper().convertValue(JsonPath.parse(myTeamJson).read("$.members"), List.class);
        List<User> users = new ArrayList<>();
        membersList.stream().forEach(member -> {
            users.add(getObjectMapper().convertValue(JsonPath.parse(member).read("$.member.user"), User.class));
        });
        return users;
    }

    public User getChangeSpecialist2() {
        List<Object> membersList = getObjectMapper().convertValue(JsonPath.parse(myTeamJson).read("$.members"), List.class);
        AtomicReference<User> changeSpecialist2 = new AtomicReference<>();
        membersList.stream().forEach(member -> {
            List<String> roles = JsonPath.parse(member).read("$.member.roles");
            if (roles.contains("changeSpecialist2")) {
                changeSpecialist2.set(getObjectMapper().convertValue(JsonPath.parse(member).read("$.member.user"), User.class));
            }
        });
        return changeSpecialist2.get();
    }

    ObjectMapper getObjectMapper() { return ApplicationContextHolder.getApplicationContext().getBean(ObjectMapper.class);}
}
