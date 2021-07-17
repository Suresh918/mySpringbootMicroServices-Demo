package com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto;


import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.example.mirai.libraries.core.annotation.JoinKey;
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
public class ReleasePackageList implements BaseView {
    @Id
    @JoinKey
    private Long id;
    private String title;
    private Integer status;
    //private String statusLabel;

    private Integer memberCount;
    @JsonIgnore
    private String memberData;
    @JsonIgnore
    private String creator;
    @JsonIgnore
    private String changeSpecialist3;
    private Long completedActions;
    private Long openActions;
    private Long totalActions;
    private String releasePackageNumber;
    @Transient
    private String statusLabel;

    //timezone must be present
    @Temporal(TemporalType.TIMESTAMP)
    private Date plannedReleaseDate;

    //timezone must be present
    @Temporal(TemporalType.TIMESTAMP)
    private Date plannedEffectiveDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    private String ecnNumber;
    private String teamCenterId;
    private String projectId;
    private Boolean sapChangeControl;
    private String mdgCrId;
    private String erValidFromInputStrategy;
    private String changeOwnerType;
    @JsonIgnore
    private String boards;
    @JsonIgnore
    private String productId;
    @JsonIgnore
    private String tags;


    @ViewMapper
    public ReleasePackageList(Long id, String title, Integer status, String releasePackageNumber,
                              Date plannedReleaseDate, Date plannedEffectiveDate, Date createdOn, String ecnNumber,
                              Integer memberCount, String memberData, Long openActions,
                              Long completedActions, Long totalActions, String teamCenterId, String mdgCrId,
                              String projectId, Boolean sapChangeControl, String erValidFromInputStrategy, String changeOwnerType,
                              String changeSpecialist3) {

        this.id = id;
        this.title = title;
        this.status = status;
        this.releasePackageNumber = releasePackageNumber;
        this.plannedReleaseDate = plannedReleaseDate;
        this.plannedEffectiveDate = plannedEffectiveDate;
        this.ecnNumber = ecnNumber;
        this.createdOn = createdOn;

        this.memberCount = memberCount;
        this.statusLabel = ReleasePackageStatus.getLabelByCode(status);
        this.memberData = memberData;

        this.completedActions = completedActions;
        this.openActions = openActions;
        this.totalActions = totalActions;
        this.projectId=projectId;
        this.teamCenterId=teamCenterId;
        this.sapChangeControl =sapChangeControl;
        this.mdgCrId = mdgCrId;
        this.erValidFromInputStrategy = erValidFromInputStrategy;
        this.changeOwnerType = changeOwnerType;
        this.changeSpecialist3 = changeSpecialist3;
    }
}

