package com.example.mirai.projectname.changerequestservice.changerequest.model.dto;

import com.example.mirai.libraries.core.annotation.JoinKey;
import com.example.mirai.libraries.core.annotation.ViewMapper;
import com.example.mirai.libraries.core.model.BaseView;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Immutable
@Getter
@Setter
@NoArgsConstructor
public class OverviewActions implements BaseView {
    @Id
    @JoinKey
    private Long changeRequestId;
    private Integer changeRequestStatus;
    private Long openActions;
    private Long completedActions;
    @JsonIgnore
    private String memberData;

    @ViewMapper
    public OverviewActions(Long changeRequestId, Integer changeRequestStatus,String memberData,  Long openActions, Long completedActions) {
        this.changeRequestId = changeRequestId;
        this.changeRequestStatus = changeRequestStatus;
        this.openActions = openActions;
        this.completedActions = completedActions;
        this.memberData = memberData;
    }
}
