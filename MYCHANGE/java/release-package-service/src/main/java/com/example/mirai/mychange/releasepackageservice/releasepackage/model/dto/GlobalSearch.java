package com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto;

import javax.persistence.Entity;
import javax.persistence.Id;

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
public class GlobalSearch implements BaseView {
    @Id
    private Long id;
    private String releasePackageNumber;
    private String title;
    private Integer status;
    private String statusLabel;
    private String type;
    private String ecnNumber;
    private String teamcenterId;

    @ViewMapper
    public GlobalSearch(Long id, String releasePackageNumber, String title, Integer status, String ecnNumber, String teamcenterId) {
        this.id = id;
        this.releasePackageNumber = releasePackageNumber;
        this.title = title;
        this.status = status;
        this.statusLabel = ReleasePackageStatus.getLabelByCode(status);
        this.ecnNumber = ecnNumber;
        this.teamcenterId = teamcenterId;
        this.type = "ReleasePackage";
    }
}

