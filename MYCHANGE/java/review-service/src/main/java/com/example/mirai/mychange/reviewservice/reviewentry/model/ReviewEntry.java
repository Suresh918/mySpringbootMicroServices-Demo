package com.example.mirai.projectname.reviewservice.reviewentry.model;

import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.annotation.SpELEvaluationContext;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.ContextInterface;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.example.mirai.libraries.security.abac.annotation.AbacSubject;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.reviewentry.service.ReviewEntryEvaluationContext;
import com.example.mirai.projectname.reviewservice.reviewentry.service.ReviewEntryService;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@DynamicUpdate
@Audited(withModifiedFlag = true)
@EntityListeners(AuditingEntityListener.class)
@ServiceClass(ReviewEntryService.class)
@SpELEvaluationContext(ReviewEntryEvaluationContext.class)
@Getter
@Setter
@ToString
public class ReviewEntry implements BaseEntityInterface, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer sequenceNumber;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "review_entry_contexts", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    @OrderColumn
    private List<ReviewEntryContext> contexts;

    private Integer status;
    @Column(length = 3072)
    private String description;

    private String classification;

    @Column(length = 3072)
    private String remark;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "review_id")
    @JsonIgnore
    @AbacScan
    private Review review;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "reviewtask_id")
    @JsonIgnore
    @AbacScan
    private ReviewTask reviewTask;

    @AbacSubject(role = "Static:assignee", principal = "userId")
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "assignee_userId")),
            @AttributeOverride(name = "fullName", column = @Column(name = "assignee_fullName")),
            @AttributeOverride(name = "email", column = @Column(name = "assignee_email")),
            @AttributeOverride(name = "departmentName", column = @Column(name = "assignee_departmentName")),
            @AttributeOverride(name = "abbreviation", column = @Column(name = "assignee_abbreviation"))
    })
    private User assignee;

    @CreatedBy
    @Embedded
    @AbacSubject(role = "Static:creator", principal = "userId")
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "creator_userId")),
            @AttributeOverride(name = "fullName", column = @Column(name = "creator_fullName")),
            @AttributeOverride(name = "email", column = @Column(name = "creator_email")),
            @AttributeOverride(name = "departmentName", column = @Column(name = "creator_departmentName")),
            @AttributeOverride(name = "abbreviation", column = @Column(name = "creator_abbreviation"))
    })
    private User creator;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    public ReviewEntry() {
        remark = "";
    }

    @Override
    public List<ContextInterface> getContextsAsContextInterface() {
        return null;
    }
}
