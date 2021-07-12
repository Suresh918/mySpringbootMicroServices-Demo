package com.example.mirai.projectname.changerequestservice.myteam.model;

import com.example.mirai.libraries.core.annotation.AclImpactedByEntities;
import com.example.mirai.libraries.core.annotation.AclImpactedEntities;
import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.annotation.SpELEvaluationContext;
import com.example.mirai.libraries.myteam.model.MyTeam;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.myteam.service.ChangeRequestMyTeamEvaluationContext;
import com.example.mirai.projectname.changerequestservice.myteam.service.ChangeRequestMyTeamService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@ServiceClass(ChangeRequestMyTeamService.class)
@DiscriminatorValue("ChangeRequest")
@EntityListeners(AuditingEntityListener.class)
@Audited(withModifiedFlag = true)
@SpELEvaluationContext(ChangeRequestMyTeamEvaluationContext.class)
@AclImpactedEntities({ChangeRequest.class})
@AclImpactedByEntities({ChangeRequest.class})
@Getter
@Setter
public class ChangeRequestMyTeam extends MyTeam implements Serializable {

    @OneToOne(optional=true, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "change_request_id")
    @JsonIgnore
    @AbacScan
    private ChangeRequest changeRequest;
}
