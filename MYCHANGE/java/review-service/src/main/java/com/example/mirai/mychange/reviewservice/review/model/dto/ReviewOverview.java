package com.example.mirai.projectname.reviewservice.review.model.dto;

import com.example.mirai.libraries.core.annotation.JoinKey;
import com.example.mirai.libraries.core.annotation.ViewMapper;
import com.example.mirai.libraries.core.model.BaseView;
import com.example.mirai.projectname.reviewservice.review.model.ReviewStatus;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

@Entity
@Immutable
@NoArgsConstructor
public class ReviewOverview implements BaseView {
    @Id
    @JoinKey
    public Long id;
    public String title;
    public Long reviewTaskCount;
    public Long reviewEntryCount;
    public Timestamp completionDate;
    public Integer status;
    public String releasepackageId;
    public String releasepackageName;
    public String teamcenterId;
    public String teamcenterName;
    public String ecnId;
    public String ecnName;
    public String statusLabel;
    public Integer completedReviewEntryCount;
    public Integer completedReviewTaskCount;
    public String reviewTasksAssigneeInfo;
    public String reviewEntryAssignees;

    @ViewMapper
    public ReviewOverview(Long id, String title, Long reviewTaskCount, Long reviewEntryCount, Date completionDate, Integer status,
                          Integer completedReviewTaskCount, Integer completedReviewEntryCount, String releasepackageId, String releasepackageName,
                          String ecnId, String ecnName, String teamcenterId, String teamcenterName, String reviewTasksAssigneeInfo, String reviewEntryAssignees) {
        this.id = id;
        this.title = title;
        this.reviewTaskCount = reviewTaskCount;
        if (Objects.nonNull(completionDate))
            this.completionDate = new Timestamp(completionDate.getTime());
        this.reviewEntryCount = reviewEntryCount;
        this.status = status;
        this.statusLabel = ReviewStatus.getLabelByCode(status);
        this.releasepackageId = releasepackageId;
        this.releasepackageName = releasepackageName;
        this.teamcenterId = teamcenterId;
        this.teamcenterName = teamcenterName;
        this.ecnId = ecnId;
        this.ecnName = ecnName;
        this.completedReviewTaskCount = completedReviewTaskCount;
        this.completedReviewEntryCount = completedReviewEntryCount;
        this.reviewTasksAssigneeInfo = reviewTasksAssigneeInfo;
        this.reviewEntryAssignees = reviewEntryAssignees;
    }
}

