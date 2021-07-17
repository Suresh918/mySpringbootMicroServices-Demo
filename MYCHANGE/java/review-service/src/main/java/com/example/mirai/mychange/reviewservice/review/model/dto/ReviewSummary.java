package com.example.mirai.projectname.reviewservice.review.model.dto;

import com.example.mirai.libraries.core.annotation.JoinKey;
import com.example.mirai.libraries.core.annotation.ViewMapper;
import com.example.mirai.libraries.core.model.BaseView;
import com.example.mirai.projectname.reviewservice.review.model.ReviewStatus;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

@Entity
@Immutable
@Getter
@Setter
public class ReviewSummary implements BaseView {
    @Id
    @JoinKey
    private Long id;
    private String title;
    private Integer reviewTaskCount;
    private Integer completedReviewTaskCount;
    private Timestamp completionDate;
    private Integer status;
    private String releasepackageId;
    private String releasepackageName;
    private String ecnId;
    private String ecnName;
    private String statusLabel;

    public ReviewSummary() {

    }

    @ViewMapper
    public ReviewSummary(Long id, String title, Integer reviewTaskCount, Integer completedReviewTaskCount,
                         Date completionDate, Integer status,
                         String releasepackageId, String releasepackageName,
                         String ecnId, String ecnName) {
        this.id = id;
        this.title = title;
        this.reviewTaskCount = reviewTaskCount;
        this.completedReviewTaskCount = completedReviewTaskCount;
        if (Objects.nonNull(completionDate))
            this.completionDate = new Timestamp(completionDate.getTime());
        this.status = status;
        this.statusLabel = ReviewStatus.getLabelByCode(status);
        this.releasepackageId = releasepackageId;
        this.releasepackageName = releasepackageName;
        this.ecnId = ecnId;
        this.ecnName = ecnName;
    }
}

