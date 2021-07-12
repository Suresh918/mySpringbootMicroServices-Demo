package com.example.mirai.projectname.changerequestservice.json;

import com.example.mirai.libraries.core.model.User;

import java.util.Date;

public class ChangeRequestCommentJson extends Content {

    public ChangeRequestCommentJson(String content) {
        super(content);
    }

    public Long getId() {
        return Long.valueOf("" + documentContext.read("id"));
    }

    public String getCommentText() {
        return documentContext.read("comment_text");
    }

    public Date getCreatedOn() {
        return convertStringToDate(documentContext.read("created_on"));
    }

    public User getCreator() {
        User creator = new User();
        creator.setUserId(getCreatorUserId());
        creator.setFullName(getCreatorFullName());
        creator.setEmail(getCreatorEmail());
        creator.setDepartmentName(getCreatorDepartmentName());
        creator.setAbbreviation(getCreatorAbbreviation());
        return creator;
    }

    public String getCreatorUserId() {
        return documentContext.read("$.creator.user_id");
    }

    public String getCreatorFullName() {
        return documentContext.read("$.creator.full_name");
    }

    public String getCreatorEmail() {
        return documentContext.read("$.creator.email");
    }

    public String getCreatorDepartmentName() {
        return documentContext.read("$.creator.department_name");
    }

    public String getCreatorAbbreviation() {
        return documentContext.read("$.creator.abbreviation");
    }
}
