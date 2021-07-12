package com.example.mirai.projectname.changerequestservice.document.model;

import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.document.service.ChangeRequestDocumentEvaluationContext;
import com.example.mirai.projectname.changerequestservice.document.service.ChangeRequestDocumentService;
import com.example.mirai.libraries.document.model.Document;
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
@ServiceClass(ChangeRequestDocumentService.class)
@SpELEvaluationContext(ChangeRequestDocumentEvaluationContext.class)
@DiscriminatorValue("ChangeRequestDocument")
@Getter
@Setter
public class ChangeRequestDocument extends Document implements Serializable {
    @ManyToOne(optional=true, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "change_request_id")
    @JsonIgnore
    @AbacScan
    private ChangeRequest changeRequest;

}
