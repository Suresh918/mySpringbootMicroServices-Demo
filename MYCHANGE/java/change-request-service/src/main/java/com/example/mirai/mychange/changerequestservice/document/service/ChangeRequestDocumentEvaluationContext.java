package com.example.mirai.projectname.changerequestservice.document.service;

import com.example.mirai.libraries.core.model.BaseEvaluationContext;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestStatus;
import com.example.mirai.projectname.changerequestservice.document.model.ChangeRequestDocument;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
public class ChangeRequestDocumentEvaluationContext extends BaseEvaluationContext<ChangeRequestDocument> {
    //TODO
    public boolean isScia() {
        if(Objects.isNull(context.getTags()) || context.getTags().isEmpty())
            return false;
        return context.getTags().stream().anyMatch(tag->tag.toString().equalsIgnoreCase("SCIA"));
    }
    public boolean isCbc() {
        if(Objects.isNull(context.getTags()) || context.getTags().isEmpty())
            return false;
        return context.getTags().stream().anyMatch(tag->tag.toString().equalsIgnoreCase("CBC"));
    }
    public Boolean isChangeRequestNotSecure() {
        return context.getChangeRequest().getIsSecure() == false;
    }
    public boolean isNotObsoleted() {return context.getChangeRequest().getStatus() != ChangeRequestStatus.OBSOLETED.getStatusCode();}
}
