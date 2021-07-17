package com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto;


import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.example.mirai.libraries.core.annotation.JoinKey;
import com.example.mirai.libraries.core.annotation.ViewMapper;
import com.example.mirai.libraries.core.model.BaseView;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Getter
@Setter
@NoArgsConstructor
public class SearchSummary implements BaseView {
    @Id
    @JoinKey
    private Long id;
    private String title;
    private Integer status;
    private String ecn;
    private String releasePackageNumber;
    @Transient
    private String statusLabel;
    @Transient
    private List<BaseView> prerequisiteReleasePackages;
    private String prerequisiteReleasePackageids;

    @ViewMapper
    public SearchSummary(Long id, String title, Integer status, String ecn, String releasePackageNumber,String prerequisiteReleasePackageids) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.ecn = ecn;
        this.releasePackageNumber = releasePackageNumber;
        this.statusLabel = ReleasePackageStatus.getLabelByCode(status);
        this.prerequisiteReleasePackageids = prerequisiteReleasePackageids;
    }
}

