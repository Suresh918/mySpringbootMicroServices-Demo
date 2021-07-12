package com.example.mirai.projectname.changerequestservice.json;

import com.example.mirai.libraries.core.model.User;

import java.util.Date;
import java.util.List;

public class ChangeRequestDocumentJson extends Content {
    public ChangeRequestDocumentJson(String content) {
        super(content);
    }

    public Long getId() {
        return Long.valueOf("" + documentContext.read("id"));
    }

    public Date getCreatedOn() {
        return convertStringToDate(documentContext.read("created_on"));
    }

    public String getDescription() {
        return documentContext.read("description");
    }

    public List<String> getTags() {
        return documentContext.read("document_tag");
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
