package com.example.mirai.projectname.releasepackageservice.comment.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.mirai.libraries.comment.model.Comment;
import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.annotation.SpELEvaluationContext;
import com.example.mirai.libraries.core.model.ContextInterface;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.example.mirai.projectname.releasepackageservice.comment.service.ReleasePackageCommentEvaluationContext;
import com.example.mirai.projectname.releasepackageservice.comment.service.ReleasePackageCommentService;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@ServiceClass(ReleasePackageCommentService.class)
@DiscriminatorValue("ReleasePackage")
@SpELEvaluationContext(ReleasePackageCommentEvaluationContext.class)
@Getter
@Setter
public class ReleasePackageComment extends Comment implements Serializable {

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "release_package_id")
    @JsonIgnore
    @AbacScan
    private ReleasePackage releasePackage;

    @Override
    public List<ContextInterface> getContextsAsContextInterface() {
        return null;
    }
}
