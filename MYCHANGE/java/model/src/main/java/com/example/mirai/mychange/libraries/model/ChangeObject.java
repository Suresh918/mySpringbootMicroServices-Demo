package com.example.mirai.projectname.libraries.model;

import com.jayway.jsonpath.JsonPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class ChangeObject extends MyChangeEvent {

    public ChangeObject(String jsonData) {
        super(jsonData, "change_object", null);
    }

    @Override
    public String getParentId() {
        String changeRequestId = JsonPath.parse(this.jsonData).read("$.data.change_object.change_object_number");
        return (Objects.nonNull(changeRequestId)) ? changeRequestId : null;
    }

    public String getChangeObjectType() {
        String changeObjectType = JsonPath.parse(this.jsonData).read("$.data.change_object.change_object_type");
        return (Objects.nonNull(changeObjectType)) ? changeObjectType : null;
    }

    @Override
    public String getStatus() {
        Integer status = JsonPath.parse(this.jsonData).read("$.data." + this.rootObjectName + ".status");
        if (Objects.nonNull(status))
            return status.toString();
        return null;

    }

    @Override
    public String getType() {
        return "CHANGEOBJECT";
    }

    @Override
    public String getTitle() {
        return null;
    }

    public List<HashMap> getChangeObjectMyTeamMembers() {
        List<HashMap> myTeamMembers = new ArrayList<>();
        if (Objects.nonNull(JsonPath.parse(this.jsonData).read("$.data.my_team.members"))) {
            List myTeamMemberAggregates = JsonPath.parse(this.jsonData).read("$.data.my_team.members");
            myTeamMemberAggregates.forEach(myTeamMemberAggregate -> myTeamMembers.add(JsonPath.parse(myTeamMemberAggregate).read("$.member")));
        }
        return myTeamMembers;
    }

    @Override
    public List<String> getParentIds() {
        List<String> parentIds = new ArrayList<>();
        parentIds.add(getParentId());
        return parentIds;
    }
}
