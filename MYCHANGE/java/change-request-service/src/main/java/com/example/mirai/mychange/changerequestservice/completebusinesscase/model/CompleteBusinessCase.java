package com.example.mirai.projectname.changerequestservice.completebusinesscase.model;

import com.example.mirai.libraries.core.model.ContextInterface;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestEvaluationContext;
import com.example.mirai.projectname.changerequestservice.completebusinesscase.service.CompleteBusinessCaseService;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.annotation.SpELEvaluationContext;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.example.mirai.libraries.core.annotation.AclReferenceEntity;
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
@ServiceClass(CompleteBusinessCaseService.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SpELEvaluationContext(ChangeRequestEvaluationContext.class)
@AclReferenceEntity(ChangeRequest.class)
@Getter
@Setter
public class CompleteBusinessCase implements BaseEntityInterface, Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private Float risk;
    private Float riskInLaborHours;
    private Float hardwareCommitment;
    private Integer systemStartsImpacted;
    private Integer systemsInWipAndFieldImpacted;

    private Float factoryInvestments;
    private Float fsToolingInvestments;
    private Float supplyChainManagementInvestments;
    private Float supplierInvestments;
    private Float deInvestments;

    private Float materialRecurringCosts;
    private Float laborRecurringCosts;
    private Float cycleTimeRecurringCosts;

    private Float inventoryReplaceNonrecurringCosts;
    private Float inventoryScrapNonrecurringCosts;
    private Float supplyChainAdjustmentsNonrecurringCosts;
    private Float factoryChangeOrderNonrecurringCosts;
    private Float fieldChangeOrderNonrecurringCosts;
    private Float updateUpgradeProductDocumentationNonrecurringCosts;
    private Float farmOutDevelopmentNonrecurringCosts;
    private Float prototypeMaterialsNonrecurringCosts;

    private Float revenuesBenefits;
    private Float opexReductionFieldLaborBenefits;
    private Float opexReductionSparePartsBenefits;
    private Float customerUptimeImprovementBenefits;
    private Float otherOpexSavingsBenefits;

    private Float internalRateOfReturn;
    private Float exampleSavings;
    private Float paybackPeriod;
    private Float customerOpexSavings;

    private Float riskOnExcessAndObsolescence;
    private Float riskOnExcessAndObsolescenceReductionProposal;
    private Float riskOnExcessAndObsolescenceReductionProposalCosts;

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
