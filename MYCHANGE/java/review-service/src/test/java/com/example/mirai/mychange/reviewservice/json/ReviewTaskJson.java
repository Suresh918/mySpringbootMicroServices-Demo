package com.example.mirai.projectname.reviewservice.json;


import com.example.mirai.libraries.core.model.User;

import java.util.Date;

public class ReviewTaskJson extends Content {

    public ReviewTaskJson(String content) {
        super(content);
    }

    public Long getId() {
        return Long.valueOf("" + documentContext.read("id"));
    }

    public Date getDueDate() {
        return convertStringToDate(documentContext.read("due_date"));
    }

    public Integer getStatus() {
        return documentContext.read("status");
    }

    public Date getCreatedOn() {
        return convertStringToDate(documentContext.read("created_on"));
    }

    public User getAssignee() {
        User executor = new User();
        executor.setUserId(getAssigneeUserId());
        executor.setFullName(getAssigneeFullName());
        executor.setEmail(getAssigneeEmail());
        executor.setDepartmentName(getAssigneeDepartmentName());
        executor.setAbbreviation(getAssigneeAbbreviation());
        return executor;
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
