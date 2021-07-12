package com.example.mirai.projectname.changerequestservice.preinstallimpact.model;

import com.example.mirai.libraries.core.annotation.AclReferenceEntity;
import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.annotation.SpELEvaluationContext;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.ContextInterface;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestEvaluationContext;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.service.PreinstallImpactService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@DynamicUpdate
@Audited(withModifiedFlag = true)
@EntityListeners(AuditingEntityListener.class)
@ServiceClass(PreinstallImpactService.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SpELEvaluationContext(ChangeRequestEvaluationContext.class)
@AclReferenceEntity(ChangeRequest.class)
@Getter
@Setter
public class PreinstallImpact implements BaseEntityInterface, Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String preinstallImpactResult;

    @OneToOne(optional=true, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "impact_analysis_id")
    @JsonIgnore
    private ImpactAnalysis impactAnalysis;

    private String changeIntroducesNew11Nc;
    @Column(length = 1024)
    private String changeIntroducesNew11NcDetails;

    private String impactOnCustomerFactoryLayout;
    @Column(length = 1024)
    private String impactOnCustomerFactoryLayoutDetails;

    private String impactOnFacilityFlows;
    @Column(length = 1024)
    private String impactOnFacilityFlowsDetails;

    private String impactOnPreinstallInterConnectCables;
    @Column(length = 1024)
    private String impactOnPreinstallInterConnectCablesDetails;

    private String changeReplacesMentionedParts;
    @Column(length = 1024)
    private String changeReplacesMentionedPartsDetails;


    @Override
    public String toString() {
        return "" + this.id;
    }

    @Override
    public void setStatus(Integer integer) {}

    @Override
    public List<ContextInterface> getContextsAsContextInterface() {
        return null;
    }

    @Override
    public Integer getStatus() { return null; }
}
