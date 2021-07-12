package com.example.mirai.projectname.changerequestservice.myteam.service;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.BaseEvaluationContext;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestStatus;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestService;
import com.example.mirai.projectname.changerequestservice.myteam.model.ChangeRequestMyTeam;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
public class ChangeRequestMyTeamEvaluationContext  extends BaseEvaluationContext<ChangeRequestMyTeam> {
    public ChangeRequest getChangeRequest() {
        ChangeRequestService changeRequestService = (ChangeRequestService) ApplicationContextHolder.getService(ChangeRequestService.class);
        return (ChangeRequest) changeRequestService.getEntityById(context.getChangeRequest().getId());
    }
    public boolean isClosed() {return context.getChangeRequest().getStatus() == ChangeRequestStatus.CLOSED.getStatusCode();}
    public boolean isRejected() {return context.getChangeRequest().getStatus() == ChangeRequestStatus.REJECTED.getStatusCode();}
    public boolean isObsoleted() {return context.getChangeRequest().getStatus() == ChangeRequestStatus.OBSOLETED.getStatusCode();}

    public Boolean isChangeRequestNotSecure() {
        return Objects.equals(context.getChangeRequest().getIsSecure(), false);
    }
}
