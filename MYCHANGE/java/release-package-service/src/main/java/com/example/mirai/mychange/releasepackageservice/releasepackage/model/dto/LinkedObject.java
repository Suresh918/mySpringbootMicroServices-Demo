package com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.example.mirai.libraries.core.annotation.ViewMapper;
import com.example.mirai.libraries.core.model.BaseView;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Getter
@Setter
@NoArgsConstructor
public class LinkedObject implements BaseView {
    @Id
    private Long id;
    private String releasePackageNumber;
    private String ecnNumber;
    private String teamcenterId;
    private String title;
    private Integer status;
    private String statusLabel;
    private Date plannedEffectiveDate;
    private Date plannedReleaseDate;
    private Integer memberCount;
    private Long completedActions;
    private Long openActions;
    private Long totalActions;
    private String projectId;
    private Boolean sapChangeControl;
    private String mdgCrId;
    private String erValidFromInputStrategy;
    @JsonIgnore
    private String changeNoticeId;
    @JsonIgnore
    private String actionIds;

    @ViewMapper
    public LinkedObject(Long id, String releasePackageNumber, String ecnNumber, String title, Integer status,
                        Date plannedEffectiveDate, Date plannedReleaseDate, Integer memberCount, String teamcenterId,
                        Long openActions, Long completedActions, Long totalActions, String projectId,
                        Boolean sapChangeControl, String mdgCrId, String erValidFromInputStrategy) {
        this.id = id;
        this.releasePackageNumber = releasePackageNumber;
        this.ecnNumber = ecnNumber;
        this.title = title;
        this.status = status;
        this.plannedEffectiveDate = plannedEffectiveDate;
        this.plannedReleaseDate = plannedReleaseDate;
        this.memberCount = memberCount;
        this.statusLabel = ReleasePackageStatus.getLabelByCode(status);
        this.completedActions = completedActions;
        this.openActions = openActions;
        this.totalActions = totalActions;
        this.teamcenterId = teamcenterId;
        this.projectId = projectId;
        this.sapChangeControl = sapChangeControl;
        this.mdgCrId = mdgCrId;
        this.erValidFromInputStrategy = erValidFromInputStrategy;
    }
}

