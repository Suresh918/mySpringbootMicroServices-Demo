package com.example.mirai.projectname.changerequestservice.changerequest.model.dto;

public interface ActionsCountPerStatus {
    Integer getChangeRequestStatus();
    Long getOpenActions();
    Long getCompletedActions();
    Long getTotalActions();
}
