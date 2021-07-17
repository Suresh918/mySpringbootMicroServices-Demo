package com.example.mirai.projectname.reviewservice.review.model;

import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.annotation.SpELEvaluationContext;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.ContextInterface;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.example.mirai.libraries.security.abac.annotation.AbacSubject;
import com.example.mirai.projectname.reviewservice.review.service.ReviewEvaluationContext;
import com.example.mirai.projectname.reviewservice.review.service.ReviewService;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
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
@ServiceClass(ReviewService.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SpELEvaluationContext(ReviewEvaluationContext.class)
@Getter
@Setter
@ToString
@AbacScan({ReviewTask.class})
public class Review implements BaseEntityInterface, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
        this will store RP-ID, ECN-ID, TC-ID
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "review_contexts", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    @OrderColumn
    private List<ReviewContext> contexts;

    private String title;

    private Integer status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date completionDate;

    @AbacSubject(role = "Static:executor", principal = "userId")
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "executor_userId")),
            @AttributeOverride(name = "fullName", column = @Column(name = "executor_fullName")),
            @AttributeOverride(name = "email", column = @Column(name = "executor_email")),
            @AttributeOverride(name = "departmentName", column = @Column(name = "executor_departmentName")),
            @AttributeOverride(name = "abbreviation", column = @Column(name = "executor_abbreviation"))
    })
    private User executor;

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

    public Review() {
    }

    @Override
    public List<ContextInterface> getContextsAsContextInterface() {
        return null;
    }
}
