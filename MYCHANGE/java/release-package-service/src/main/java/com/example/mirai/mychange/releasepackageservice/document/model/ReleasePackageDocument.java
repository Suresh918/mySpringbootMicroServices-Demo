package com.example.mirai.projectname.releasepackageservice.document.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.annotation.SpELEvaluationContext;
import com.example.mirai.libraries.core.model.ContextInterface;
import com.example.mirai.libraries.document.model.Document;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.example.mirai.projectname.releasepackageservice.document.service.ReleasePackageDocumentEvaluationContext;
import com.example.mirai.projectname.releasepackageservice.document.service.ReleasePackageDocumentService;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@SpELEvaluationContext(ReleasePackageDocumentEvaluationContext.class)
@ServiceClass(ReleasePackageDocumentService.class)
@DiscriminatorValue("ReleasePackageDocument")
@Getter
@Setter
public class ReleasePackageDocument extends Document implements Serializable {
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
