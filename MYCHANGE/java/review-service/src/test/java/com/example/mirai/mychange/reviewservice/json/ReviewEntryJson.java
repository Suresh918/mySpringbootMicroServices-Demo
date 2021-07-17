package com.example.mirai.projectname.reviewservice.json;

import com.example.mirai.libraries.core.model.User;


public class ReviewEntryJson extends Content {

    public ReviewEntryJson(String content) {
        super(content);
    }


    public Long getId() {
        return Long.valueOf("" + documentContext.read("id"));
    }

    public Integer getStatus() {
        return Integer.valueOf("" + documentContext.read("status"));
    }

    public User getAssignee() {
        User assignee = new User();
        assignee.setUserId(getAssigneeUserId());
        assignee.setAbbreviation(getAssigneeAbbreviation());
        assignee.setDepartmentName(getAssigneeDepartmentName());
        assignee.setEmail(getAssigneeEmail());
        assignee.setFullName(getAssigneeFullName());
        return assignee;
    }

    public String getAssigneeUserId() {
        return documentContext.read("$.assignee.user_id");
    }

    public String getAssigneeFullName() {
        return documentContext.read("$.assignee.full_name");
    }

    public String getAssigneeEmail() {
        return documentContext.read("$.assignee.email");
    }

    public String getAssigneeDepartmentName() {
        return documentContext.read("$.assignee.department_name");
    }

    public String getAssigneeAbbreviation() {
        return documentContext.read("$.assignee.abbreviation");
    }


}
