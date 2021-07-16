package com.example.mirai.projectname.libraries.model;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

import java.util.Objects;

public class ChangeRequest extends MyChangeEvent {
    private final String changeRequestJson;

    public ChangeRequest(String changeRequestJson, String rootObjectName, String contextType) {
        super(changeRequestJson, rootObjectName, contextType);
        this.changeRequestJson = changeRequestJson;
    }

    @Override
    public String getType() {
        return "CHANGEREQUEST";
    }

    public Boolean getIsSecure() {
        return (JsonPath.parse(jsonData).read("$.data." + rootObjectName + ".is_secure"));
    }

    public JSONArray getChangeBoards() {
        JSONArray changeBoards = JsonPath.parse(jsonData).read("$.data." +rootObjectName+ ".change_boards");
        if (Objects.isNull(changeBoards)) {
            return new JSONArray();
        }
        return changeBoards;
    }

    public JSONArray getChangeControlBoards() {
        JSONArray changeControlBoards = JsonPath.parse(jsonData).read("$.data." +rootObjectName+ ".change_control_boards");
        if (Objects.isNull(changeControlBoards)) {
            return new JSONArray();
        }
        return changeControlBoards;
    }


}
