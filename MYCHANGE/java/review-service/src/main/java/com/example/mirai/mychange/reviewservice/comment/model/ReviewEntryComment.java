package com.example.mirai.projectname.reviewservice.comment.model;

import com.example.mirai.libraries.comment.model.Comment;
import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.annotation.SpELEvaluationContext;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.example.mirai.projectname.reviewservice.comment.service.ReviewEntryCommentEvaluationContext;
import com.example.mirai.projectname.reviewservice.comment.service.ReviewEntryCommentService;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@ServiceClass(ReviewEntryCommentService.class)
@DiscriminatorValue("ReviewEntry")
@SpELEvaluationContext(ReviewEntryCommentEvaluationContext.class)
@Getter
@Setter
public class ReviewEntryComment extends Comment implements Serializable {

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "reviewentry_id")
    @JsonIgnore
    @AbacScan
    private ReviewEntry reviewEntry;

}
