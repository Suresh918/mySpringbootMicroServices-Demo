package com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto;

public interface DateActionCount {
    Long getPlannedEffectiveDatePastCount();
    Long getPlannedReleaseDatePastCount();
    Long getPlannedEffectiveDateSoonCount();
    Long getPlannedReleaseDateSoonCount();
    Long getActionCount();
    Integer getStatus();
}
