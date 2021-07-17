package com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.example.mirai.libraries.core.annotation.JoinKey;
import com.example.mirai.libraries.core.annotation.ViewMapper;
import com.example.mirai.libraries.core.model.BaseView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Getter
@Setter
@NoArgsConstructor
public class AutomaticClosure implements BaseView {
    @Id
    @JoinKey
    private Long id;
    private String releasePackageNumber;
    private String ecnId;
    private String teamcenterId;
    private Integer status;
    private Integer openActionCount;
    private Integer completedReviewCount;
    private Integer totalReviewCount;

    @ViewMapper
    public AutomaticClosure(Long id, String releasePackageNumber, String ecnId,
                            String teamcenterId, Integer status,
                            Integer openActionCount, Integer completedReviewCount, Integer totalReviewCount) {
        this.id = id;
        this.releasePackageNumber = releasePackageNumber;
        this.ecnId = ecnId;
        this.teamcenterId = teamcenterId;
        this.status = status;
        this.openActionCount = openActionCount;
        this.completedReviewCount = completedReviewCount;
        this.totalReviewCount = totalReviewCount;
    }
}
