package com.example.mirai.projectname.releasepackageservice.myteam.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.example.mirai.libraries.core.annotation.AclImpactedByEntities;
import com.example.mirai.libraries.core.annotation.AclImpactedEntities;
import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.annotation.SpELEvaluationContext;
import com.example.mirai.libraries.core.model.ContextInterface;
import com.example.mirai.libraries.myteam.model.MyTeam;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.example.mirai.projectname.releasepackageservice.myteam.service.ReleasePackageMyTeamEvaluationContext;
import com.example.mirai.projectname.releasepackageservice.myteam.service.ReleasePackageMyTeamService;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.envers.Audited;

@Entity
@Audited(withModifiedFlag = true)
@ServiceClass(ReleasePackageMyTeamService.class)
@DiscriminatorValue("ReleasePackage")
@SpELEvaluationContext(ReleasePackageMyTeamEvaluationContext.class)
@AclImpactedEntities({ReleasePackage.class})
@AclImpactedByEntities({ReleasePackage.class})
@Getter
@Setter
public class ReleasePackageMyTeam extends MyTeam implements Serializable {
    @OneToOne(optional = true, fetch = FetchType.EAGER)
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
