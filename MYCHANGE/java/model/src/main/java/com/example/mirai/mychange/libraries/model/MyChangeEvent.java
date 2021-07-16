package com.example.mirai.projectname.libraries.model;

import com.example.mirai.libraries.core.model.SynchronizationContextInterface;
import com.jayway.jsonpath.JsonPath;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

import java.util.*;

@AllArgsConstructor
@Getter
@Slf4j
public abstract class MyChangeEvent implements SynchronizationContextInterface {

    public String jsonData;
    public String rootObjectName;
    public String contextType;

    public String getActorUserId() {
        if (Objects.nonNull(JsonPath.parse(jsonData).read("$.actor")))
            return (JsonPath.parse(jsonData).read("$.actor.user_id"));
        return null;
    }

    public String getActorAbbreviation() {
        if (Objects.nonNull(JsonPath.parse(jsonData).read("$.actor")))
            return (JsonPath.parse(jsonData).read("$.actor.abbreviation"));
        return null;
    }

    public String getActorFullName() {
        if (Objects.nonNull(JsonPath.parse(jsonData).read("$.actor")))
            return (JsonPath.parse(jsonData).read("$.actor.full_name"));
        return null;
    }

    public String getActorDepartmentName() {
        if (Objects.nonNull(JsonPath.parse(jsonData).read("$.actor")))
            return (JsonPath.parse(jsonData).read("$.actor.department_name"));
        return null;
    }

    public String getActorEmail() {
        if (Objects.nonNull(JsonPath.parse(jsonData).read("$.actor")))
            return (JsonPath.parse(jsonData).read("$.actor.email"));
        return null;
    }

    public Date getEventTimestamp() {
        Long timestamp = (JsonPath.parse(jsonData).read("$.timestamp"));
        return new Date(timestamp);
        /*Date newdate = new Date();
        newdate.setHours(21);
        return newdate;*/
    }

    @Override
    public String getContextId() {
        return (JsonPath.parse(jsonData).read("$.data." + rootObjectName + ".id")).toString();

    }

    @Override
    public String getStatus() {
        Object status = JsonPath.parse(jsonData).read("$.data." + rootObjectName + ".status");
        if (Objects.nonNull(status))
            return status.toString();
        return null;
    }

    @Override
    public String getTitle() {
        return (JsonPath.parse(jsonData).read("$.data." + rootObjectName + ".title"));
    }

    @Override
    public String getParentId() {
        if (Objects.isNull(JsonPath.parse(jsonData).read("$.data." + rootObjectName + ".contexts"))) {
            return null;
        }
        Object parentId = JsonPath.parse(jsonData).read("$.data." + rootObjectName + ".contexts[?(@.type=='" + this.contextType + "')].context_id");
        if (parentId instanceof JSONArray) {
            if (((JSONArray) parentId).isEmpty()) {
                return null;
            }
            return ((JSONArray)JsonPath.parse(jsonData).read("$.data." + rootObjectName + ".contexts[?(@.type=='" + this.contextType + "')].context_id")).get(0).toString();
        }
        return JsonPath.parse(jsonData).read("$.data." + rootObjectName + ".contexts[?(@.type=='" + this.contextType + "')].context_id").toString();

    }

    @Override
    public List<String> getParentIds() {
        JSONArray contexts = JsonPath.parse(jsonData).read("$.data." + rootObjectName + ".contexts[?(@.type=='" + this.contextType + "')].context_id");
        if (Objects.nonNull(contexts)) {
            List<String> parentIds = new ArrayList<>();
            contexts.stream().forEach(item -> parentIds.add(item.toString()));
            return parentIds;
        }
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        //return JsonPath.parse(jsonData).read("$.data." + rootObjectName + ".contexts[?(@.type=='" + this.contextType + "')].name").toString();
        return null;
    }

    public List<HashMap> getMyTeamMembers() {
        return getMyTeamMembers("my_team_details");
    }

    public List<HashMap> getMyTeamMembers(String myTeamPath) {
        List<HashMap> myTeamMembers = new ArrayList<>();
        if (Objects.nonNull(JsonPath.parse(this.jsonData).read("$.data." + myTeamPath + ".members"))) {
            List myTeamMemberAggregates = JsonPath.parse(this.jsonData).read("$.data." + myTeamPath + ".members");
            myTeamMemberAggregates.forEach(myTeamMemberAggregate -> myTeamMembers.add(JsonPath.parse(myTeamMemberAggregate).read("$.member")));
        }
        return myTeamMembers;
    }
}
