package com.example.mirai.projectname.changerequestservice.changerequest.model.dto;

import com.example.mirai.libraries.core.annotation.JoinKey;
import com.example.mirai.libraries.core.annotation.ViewMapper;
import com.example.mirai.libraries.core.model.BaseView;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestStatus;
import com.example.mirai.projectname.changerequestservice.shared.util.AnalysisPriorityValues;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Objects;

@Entity
@Immutable
@Getter
@Setter
@NoArgsConstructor
public class Overview implements BaseView {
    @Id
    @JoinKey
    private Long id;
    private String title;
    private Integer status;
    private String statusLabel;
    private Integer analysisPriority;
    @Transient
    private String analysisPriorityLabel;
    private Integer memberCount;
    @JsonIgnore
    private String memberData;
    @JsonIgnore
    private String impact;
    private Long completedActions;
    private Long openActions;
    private Long totalActions;
    private String changeOwnerType;

    @ViewMapper
    public Overview(Long id, String title, Integer status, Integer analysisPriority, Integer memberCount, String memberData, String impact, Long openActions, Long completedActions, Long totalActions,String changeOwnerType) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.analysisPriority = analysisPriority;
        this.memberCount = memberCount;
        this.statusLabel = ChangeRequestStatus.getLabelByCode(status);
        if (Objects.nonNull(analysisPriority))
            this.analysisPriorityLabel = AnalysisPriorityValues.getLabelByCode(analysisPriority);
        this.memberData = memberData;
        this.impact = impact;
        this.completedActions = completedActions;
        this.openActions = openActions;
        this.totalActions = totalActions;
        this.changeOwnerType = changeOwnerType;
    }
}

