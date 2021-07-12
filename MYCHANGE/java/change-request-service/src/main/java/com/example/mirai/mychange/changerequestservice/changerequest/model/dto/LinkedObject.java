package com.example.mirai.projectname.changerequestservice.changerequest.model.dto;

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
public class LinkedObject implements BaseView {
    @Id
    private Long id;
    @JsonIgnore
    private String changeRequestNumber;
    private String title;
    private Integer status;
    private String statusLabel;
    private Integer analysisPriority;
    @Transient
    private String analysisPriorityLabel;
    private Integer implementationPriority;
    @Transient
    private String implementationPriorityLabel;
    private Integer memberCount;
    private Long completedActions;
    private Long openActions;
    private Long totalActions;
    @JsonIgnore
    private String changeNoticeId;
    @JsonIgnore
    private String actionIds;
    @JsonIgnore
    private String boards;
    @JsonIgnore
    private String memberData;
    @JsonIgnore
    private Boolean isSecure;


    @ViewMapper
    public LinkedObject(Long id, String title, Integer status, Integer analysisPriority, Integer implementationPriority, Integer memberCount,
                        Long openActions, Long completedActions, Long totalActions) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.analysisPriority = analysisPriority;
        this.implementationPriority = implementationPriority;
        this.memberCount = memberCount;
        this.statusLabel = ChangeRequestStatus.getLabelByCode(status);
        if (Objects.nonNull(analysisPriority))
            this.analysisPriorityLabel = AnalysisPriorityValues.getLabelByCode(analysisPriority);
        if (Objects.nonNull(implementationPriority))
            this.implementationPriorityLabel =  AnalysisPriorityValues.getLabelByCode(implementationPriority);
        this.completedActions = completedActions;
        this.openActions = openActions;
        this.totalActions = totalActions;
    }
}

