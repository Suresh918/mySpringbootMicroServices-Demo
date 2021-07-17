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
public class PrerequisitesOverview implements BaseView {
    @Id
    @JoinKey
    private Long id;
    private String title;
    private Integer status;
    private String releasePackageNumber;

    @Temporal(TemporalType.TIMESTAMP)
    private Date plannedReleaseDate;

    //timezone must be present
    @Temporal(TemporalType.TIMESTAMP)
    private Date plannedEffectiveDate;

    private Integer sequenceNumber;

    private Integer memberCount;
    @JsonIgnore
    private String memberData;
    @JsonIgnore

    private Long completedActions;
    private Long openActions;
    private Long totalActions;
    @Transient
    private String statusLabel;

    private String ecn;

    private Long prerequisiteReleasePackageId;

    @ViewMapper
    public PrerequisitesOverview(Long id, String title, Integer status, String releasePackageNumber,
                                 Date plannedReleaseDate, Date plannedEffectiveDate, Integer sequenceNumber, Integer memberCount, String memberData,
                                 Long completedActions, Long openActions, Long totalActions, String ecn, Long prerequisiteReleasePackageId) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.releasePackageNumber = releasePackageNumber;
        this.plannedReleaseDate = plannedReleaseDate;
        this.plannedEffectiveDate = plannedEffectiveDate;
        this.sequenceNumber = sequenceNumber;
        this.memberCount = memberCount;
        this.memberData = memberData;
        this.completedActions = completedActions;
        this.openActions = openActions;
        this.totalActions = totalActions;
        this.ecn = ecn;
        this.statusLabel = ReleasePackageStatus.getLabelByCode(status);
        this.prerequisiteReleasePackageId = prerequisiteReleasePackageId;
    }
}

