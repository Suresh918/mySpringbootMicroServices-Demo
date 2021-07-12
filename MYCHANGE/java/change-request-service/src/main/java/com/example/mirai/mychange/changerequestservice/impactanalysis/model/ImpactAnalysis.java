package com.example.mirai.projectname.changerequestservice.impactanalysis.model;

import com.example.mirai.libraries.core.annotation.AclReferenceEntity;
import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.annotation.SpELEvaluationContext;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.ContextInterface;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestEvaluationContext;
import com.example.mirai.projectname.changerequestservice.impactanalysis.service.ImpactAnalysisService;
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
import java.util.List;

@Entity
@DynamicUpdate
@Audited(withModifiedFlag = true)
@EntityListeners(AuditingEntityListener.class)
@ServiceClass(ImpactAnalysisService.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SpELEvaluationContext(ChangeRequestEvaluationContext.class)
@AclReferenceEntity(ChangeRequest.class)
@Getter
@Setter
public class ImpactAnalysis implements BaseEntityInterface, Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(length = 1024)
    private String upgradePackages;
    private Duration upgradeTime;
    private Duration recoveryTime;
    @Column(length = 1024)
    private String prePostConditions;

    private String impactOnSequence;
    @Column(length = 1024)
    private String impactOnSequenceDetails;

    private String impactOnAvailability;
    @Column(length = 1024)
    private String impactOnAvailabilityDetails;

    private String multiPlantImpact;

    private String phaseOutSparesTools;
    @Column(length = 1024)
    private String phaseOutSparesToolsDetails;

    private String techRiskAssessmentSra;
    @Column(columnDefinition = "TEXT")
    private String techRiskAssessmentSraDetails;

    private String techRiskAssessmentFmea;
    @Column(columnDefinition = "TEXT")
    private String techRiskAssessmentFmeaDetails;

    @Column(length = 512)
    private String totalInstancesAffected;

    private String impactOnSystemLevelPerformance;
    @Column(length = 1024)
    private String impactOnSystemLevelPerformanceDetails;

    private String impactOnCycleTime;
    @Column(length = 1024)
    private String impactOnCycleTimeDetails;

    private String impactOnLaborHours;//Yes Or No
    @Column(length = 1024)
    private String impactOnLaborHoursDetails;

    private String impactOnExistingParts;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "liability_risks", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    @OrderColumn
    private List<String> liabilityRisks;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "implementation_ranges", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    @OrderColumn
    private List<String> implementationRanges;

    @Column(length = 1024)
    private String implementationRangesDetails;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cbp_strategies", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    @OrderColumn
    private List<String> cbpStrategies;
    @Column(length = 1024)
    private String cbpStrategiesDetails;

    private Duration developmentLaborHours;
    private Duration investigationLaborHours;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "fco_types", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    @OrderColumn
    private List<String> fcoTypes;

    private String calendarDependency; //leading-NonLeading

    /*@ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "targeted_valid_configurations", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    @OrderColumn
    private List<String> targetedValidConfigurations; //targetedVC*/

    private String targetedValidConfigurations;

    @OneToOne(optional=true, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "change_request_id")
    @JsonIgnore
    @AbacScan
    private ChangeRequest changeRequest;

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

    /*//initializer block
    {
         this.liabilityRisks = new ArrayList<>();
         this.fcoTypes = new ArrayList<>();
         this.cbpStrategies = new ArrayList<>();
         this.implementationRanges = new ArrayList<>();
    }*/
}
