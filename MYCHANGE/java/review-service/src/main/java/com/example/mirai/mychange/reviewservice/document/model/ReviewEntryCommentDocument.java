package com.example.mirai.projectname.reviewservice.document.model;

import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.document.model.Document;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.example.mirai.projectname.reviewservice.comment.model.ReviewEntryComment;
import com.example.mirai.projectname.reviewservice.document.service.ReviewEntryCommentDocumentService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@ServiceClass(ReviewEntryCommentDocumentService.class)
@DiscriminatorValue("ReviewEntryCommentDocument")
@Getter
@Setter
public class ReviewEntryCommentDocument extends Document implements Serializable {
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "reviewentrycomment_id")
    @JsonIgnore
    @AbacScan
    private ReviewEntryComment reviewEntryComment;
}
