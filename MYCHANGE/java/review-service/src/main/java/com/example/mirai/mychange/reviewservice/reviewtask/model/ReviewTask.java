package com.example.mirai.projectname.reviewservice.reviewtask.model;

import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.annotation.SpELEvaluationContext;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.ContextInterface;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.example.mirai.libraries.security.abac.annotation.AbacSubject;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.reviewtask.service.ReviewTaskEvaluationContext;
import com.example.mirai.projectname.reviewservice.reviewtask.service.ReviewTaskService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
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
@ServiceClass(ReviewTaskService.class)
@SpELEvaluationContext(ReviewTaskEvaluationContext.class)
@Getter
@Setter
public class ReviewTask implements BaseEntityInterface, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;
    private Integer status;

    @Embedded
    @AbacSubject(role = "Static:assignee", principal = "userId")
    private User assignee;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "review_id")
    @JsonIgnore
    @AbacScan
    private Review review;

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

    public ReviewTask() {
        status = ReviewTaskStatus.valueOf("OPENED").getStatusCode();
    }

    @Override
    public String toString() {
        return "" + this.id;
    }

    @Override
    public List<ContextInterface> getContextsAsContextInterface() {
        return null;
    }
}
