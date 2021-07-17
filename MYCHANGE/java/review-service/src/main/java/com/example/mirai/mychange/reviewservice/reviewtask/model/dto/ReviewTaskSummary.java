package com.example.mirai.projectname.reviewservice.reviewtask.model.dto;

import com.example.mirai.libraries.core.annotation.JoinKey;
import com.example.mirai.libraries.core.annotation.ViewMapper;
import com.example.mirai.libraries.core.model.BaseView;
import com.example.mirai.libraries.core.model.CasePermissions;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTaskStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

@Entity
@Immutable
@Getter
@Setter
public class ReviewTaskSummary implements BaseView {
    @Id
    @JoinKey
    private Long id;
    @JsonIgnore
    private String userId;
    @JsonIgnore
    private String fullName;
    @JsonIgnore
    private String abbreviation;
    @JsonIgnore
    private String departmentName;
    @JsonIgnore
    private String email;
    private Timestamp dueDate;
    private Integer status;
    private String statusLabel;
    private Integer reviewEntryCount;
    private Integer completedReviewEntryCount;
    @Transient
    private User assignee;

    @Transient
    private CasePermissions casePermissions;

    public ReviewTaskSummary() {
    }

    @ViewMapper
    public ReviewTaskSummary(Long id, String userId, String fullName, String abbreviation, Date dueDate,
                             Integer status, Integer reviewEntryCount, Integer completedReviewEntryCount, String departmentName, String email) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.abbreviation = abbreviation;
        this.assignee = new User();
        this.assignee.setAbbreviation(abbreviation);
        this.assignee.setFullName(fullName);
        this.assignee.setUserId(userId);
        this.assignee.setDepartmentName(departmentName);
        this.assignee.setEmail(email);
        if (!Objects.isNull(dueDate))
            this.dueDate = new Timestamp(dueDate.getTime());
        this.status = status;
        this.statusLabel = ReviewTaskStatus.getLabelByCode(status);
        this.reviewEntryCount = reviewEntryCount;
        this.completedReviewEntryCount = completedReviewEntryCount;
    }
}
