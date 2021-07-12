package com.example.mirai.projectname.changerequestservice.myteam.listener;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.JSONArray;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PreferredRolesMessage {
    private String jsonData;
    private static ObjectMapper objectMapper = (ObjectMapper) ApplicationContextHolder.getBean(ObjectMapper.class);;
    public String getUserId() {
        return JsonPath.parse(jsonData).read("$.user_id");
    }
    public List<String> getRoles() {
        JSONArray jsonArray =  JsonPath.parse(jsonData).read("$.preferred_roles");
        return objectMapper.convertValue(jsonArray, List.class);
    }
}
