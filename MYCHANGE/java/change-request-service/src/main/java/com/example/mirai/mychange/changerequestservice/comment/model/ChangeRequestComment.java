package com.example.mirai.projectname.changerequestservice.comment.model;

import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.comment.service.ChangeRequestCommentEvaluationContext;
import com.example.mirai.projectname.changerequestservice.comment.service.ChangeRequestCommentService;
import com.example.mirai.libraries.comment.model.Comment;
import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.annotation.SpELEvaluationContext;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@ServiceClass(ChangeRequestCommentService.class)
@DiscriminatorValue("ChangeRequest")
@SpELEvaluationContext(ChangeRequestCommentEvaluationContext.class)
@Getter
@Setter
public class ChangeRequestComment extends Comment implements Serializable {

    @ManyToOne(optional=true, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "change_request_id")
    @JsonIgnore
    @AbacScan
    private ChangeRequest changeRequest;

}
