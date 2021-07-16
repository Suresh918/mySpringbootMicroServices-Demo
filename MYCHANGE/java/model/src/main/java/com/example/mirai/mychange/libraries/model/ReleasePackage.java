package com.example.mirai.projectname.libraries.model;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

import java.util.List;
import java.util.Objects;

public class ReleasePackage extends MyChangeEvent {

    public ReleasePackage(String jsonData, String rootObjectName, String contextType) {
        super(jsonData, rootObjectName, contextType);
    }

    @Override
    public String getType() {
        return "RELEASEPACKAGE";
    }
    public String getEcnId() {
        return ((List<String>) JsonPath.parse(jsonData).read("$.data." +rootObjectName+ ".contexts[?(@.type=='ECN')].context_id")).get(0);

    }

    public String getTeamcenterId() {
        if (!((JSONArray)JsonPath.parse(jsonData).read("$.data." +rootObjectName+ ".contexts[?(@.type=='TEAMCENTER')].context_id")).isEmpty())
            return ((List<String>) JsonPath.parse(jsonData).read("$.data." +rootObjectName+ ".contexts[?(@.type=='TEAMCENTER')].context_id")).get(0);
        return null;
    }

    public String getEcnStatus() {
        return ((List<String>) JsonPath.parse(jsonData).read("$.data." +rootObjectName+ ".contexts[?(@.type=='ECN')].status")).get(0);
    }


    public String getEcnTitle() {
        return ((List<String>) JsonPath.parse(jsonData).read("$.data." +rootObjectName+ ".contexts[?(@.type=='ECN')].name")).get(0);
    }

    @Override
    public String getContextId() {
        return getReleasePackageNumber();
    }

    public JSONArray getChangeControlBoards() {
        JSONArray changeControlBoards = JsonPath.parse(jsonData).read("$.data." +rootObjectName+ ".change_control_boards");
        if (Objects.isNull(changeControlBoards)) {
            return new JSONArray();
        }
        return changeControlBoards;
    }

    public JSONArray getSubTypes() {
        JSONArray subTypes = JsonPath.parse(jsonData).read("$.data." +rootObjectName+ ".types");
        if (Objects.isNull(subTypes)) {
            return new JSONArray();
        }
        return subTypes;
    }
    public String getReleasePackageNumber() {
        return JsonPath.parse(jsonData).read("$.data." +rootObjectName+ ".release_package_number").toString();

    }
}
