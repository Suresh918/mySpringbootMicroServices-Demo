package com.example.mirai.projectname.releasepackageservice.document.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.model.ContextInterface;
import com.example.mirai.libraries.document.model.Document;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.example.mirai.projectname.releasepackageservice.comment.model.ReleasePackageComment;
import com.example.mirai.projectname.releasepackageservice.document.service.ReleasePackageCommentDocumentService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@ServiceClass(ReleasePackageCommentDocumentService.class)
@DiscriminatorValue("ReleasePackageCommentDocument")
@Getter
@Setter
public class ReleasePackageCommentDocument extends Document implements Serializable {
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "release_package_comment_id")
    @JsonIgnore
    @AbacScan
    private ReleasePackageComment releasePackageComment;

    @Override
    public List<ContextInterface> getContextsAsContextInterface() {
        return null;
    }
}
