package com.example.mirai.projectname.reviewservice.reviewentry.model.dto;

import com.example.mirai.libraries.core.annotation.JoinKey;
import com.example.mirai.libraries.core.annotation.ViewMapper;
import com.example.mirai.libraries.core.model.BaseView;
import com.example.mirai.libraries.core.model.CasePermissions;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntryStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
@Immutable
@Getter
@Setter
@NoArgsConstructor
public class ReviewEntryOverview implements BaseView {
    @Id
    @JoinKey
    private Long id;
    private Integer sequenceNumber;
    private Integer status;
    private String statusLabel;
    private String description;
    private String classification;
    private String remark;
    private String solutionItemsId;
    private String solutionItemsName;
    @JsonIgnore
    private String assigneeUserId;
    @JsonIgnore
    private String assigneeAbbreviation;
    @JsonIgnore
    private String assigneeFullName;
    @JsonIgnore
    private String assigneeDepartmentName;
    @JsonIgnore
    private String assigneeEmail;
    @JsonIgnore
    private String creatorUserId;
    @JsonIgnore
    private String creatorAbbreviation;
    @JsonIgnore
    private String creatorFullName;
    @JsonIgnore
    private String creatorDepartmentName;
    @JsonIgnore
    private String creatorEmail;
    private Integer commentCount;
    @Transient
    private CasePermissions casePermissions;
    @Transient
    private User assignee;
    @Transient
    private User creator;

    @ViewMapper
    public ReviewEntryOverview(Long id, Integer sequenceNumber, Integer status, String description,
                               String classification, String remark, String solutionItemsId, String solutionItemsName, String assigneeUserId, String assigneeAbbreviation,
                               String assigneeFullName, String assigneeDepartmentName, String assigneeEmail,
                               String creatorUserId, String creatorAbbreviation, String creatorFullName,
                               String creatorDepartmentName, String creatorEmail, Integer commentCount) {
        this.id = id;
        this.sequenceNumber = sequenceNumber;
        this.status = status;
        this.statusLabel = ReviewEntryStatus.getLabelByCode(status);
        this.description = description;
        this.classification = classification;
        this.remark = remark;
        this.commentCount = commentCount;
        this.solutionItemsId= solutionItemsId;
        this.solutionItemsName = solutionItemsName;
        this.creator = new User();
        this.creator.setFullName(creatorFullName);
        this.creator.setAbbreviation(creatorAbbreviation);
        this.creator.setUserId(creatorUserId);
        this.creator.setDepartmentName(creatorDepartmentName);
        this.creator.setEmail(creatorEmail);
        if (assigneeUserId != null) {
            this.assignee = new User();
            this.assignee.setFullName(assigneeFullName);
            this.assignee.setAbbreviation(assigneeAbbreviation);
            this.assignee.setUserId(assigneeUserId);
            this.assignee.setDepartmentName(assigneeDepartmentName);
            this.assignee.setEmail(assigneeEmail);
        }

    }

}
