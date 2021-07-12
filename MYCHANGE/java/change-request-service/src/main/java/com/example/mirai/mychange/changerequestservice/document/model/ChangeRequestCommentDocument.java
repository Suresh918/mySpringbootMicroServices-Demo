package com.example.mirai.projectname.changerequestservice.document.model;

import com.example.mirai.projectname.changerequestservice.comment.model.ChangeRequestComment;
import com.example.mirai.projectname.changerequestservice.document.service.ChangeRequestCommentDocumentService;
import com.example.mirai.libraries.document.model.Document;
import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@ServiceClass(ChangeRequestCommentDocumentService.class)
@DiscriminatorValue("ChangeRequestCommentDocument")
@Getter
@Setter
public class ChangeRequestCommentDocument extends Document implements Serializable {
    @ManyToOne(optional=true, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "change_request_comment_id")
    @JsonIgnore
    @AbacScan
    private ChangeRequestComment changeRequestComment;
}
