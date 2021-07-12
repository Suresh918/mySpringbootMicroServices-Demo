package com.example.mirai.projectname.changerequestservice.changerequest.model.dto;

import com.example.mirai.libraries.entity.model.StatusOverview;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusCount extends StatusOverview.StatusCount {
    private Long openActions;
    private Long completedActions;
    private Long totalActions;

    public StatusCount(StatusOverview.StatusCount statusCount) {
        this.setCount(statusCount.getCount());
        this.setStatus(statusCount.getStatus());
        this.setStatusLabel(statusCount.getStatusLabel());
    }

}
