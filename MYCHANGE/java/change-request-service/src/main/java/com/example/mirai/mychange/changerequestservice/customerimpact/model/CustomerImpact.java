package com.example.mirai.projectname.changerequestservice.customerimpact.model;

import com.example.mirai.libraries.core.annotation.AclReferenceEntity;
import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.annotation.SpELEvaluationContext;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.ContextInterface;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestEvaluationContext;
import com.example.mirai.projectname.changerequestservice.customerimpact.service.CustomerImpactService;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
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
import java.time.Duration;
import java.time.Period;
import java.util.List;


@Entity
@DynamicUpdate
@Audited(withModifiedFlag = true)
@EntityListeners(AuditingEntityListener.class)
@ServiceClass(CustomerImpactService.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SpELEvaluationContext(ChangeRequestEvaluationContext.class)
@AclReferenceEntity(ChangeRequest.class)
@Getter
@Setter
public class CustomerImpact implements BaseEntityInterface, Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String customerImpactResult;

    private String customerApproval;
    @Column(length = 1024)
    private String customerApprovalDetails;

    private String customerCommunication;
    @Column(length = 1024)
    private String customerCommunicationDetails;

    private Duration uptimeImprovement;
    private Duration fcoImplementation;
    private Period uptimePayback;

    private Float uptimeImprovementAvailability;
    private Float fcoImplementationAvailability;
    private Period uptimePaybackAvailability;

    private String impactOnUserInterfaces;
    @Column(length = 1024)
    private String impactOnUserInterfacesDetails;

    private String impactOnWaferProcessEnvironment;
    @Column(length = 1024)
    private String impactOnWaferProcessEnvironmentDetails;

    private String changeToCustomerImpactCriticalPart;
    @Column(length = 1024)
    private String changeToCustomerImpactCriticalPartDetails;

    private String changeToProcessImpactingCustomer;
    @Column(length = 1024)
    private String changeToProcessImpactingCustomerDetails;

    private String fcoUpgradeOptionCsrImplementationChange;
    @Column(length = 1024)
    private String fcoUpgradeOptionCsrImplementationChangeDetails;

    @OneToOne(optional=true, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "impact_analysis_id")
    @JsonIgnore
    private ImpactAnalysis impactAnalysis;

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
