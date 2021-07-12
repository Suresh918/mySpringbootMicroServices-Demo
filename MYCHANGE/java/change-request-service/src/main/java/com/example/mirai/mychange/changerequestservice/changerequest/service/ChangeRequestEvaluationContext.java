package com.example.mirai.projectname.changerequestservice.changerequest.service;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.BaseEvaluationContext;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeOwnerType;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestContext;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestStatus;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestAggregate;
import com.example.mirai.projectname.changerequestservice.customerimpact.model.CustomerImpact;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.model.PreinstallImpact;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import com.example.mirai.projectname.changerequestservice.shared.util.ScopeValues;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.model.SolutionDefinition;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
public class ChangeRequestEvaluationContext extends BaseEvaluationContext<ChangeRequest> {
    private ChangeRequestAggregate changeRequestAggregate;

    private ChangeRequestAggregate getAggregate() {
        if (Objects.isNull(this.changeRequestAggregate)) {
            setAggregate();
        }
        return this.changeRequestAggregate;
    }

    private void setAggregate() {
        ChangeRequestService changeRequestService = (ChangeRequestService) ApplicationContextHolder.getService(ChangeRequestService.class);
        this.changeRequestAggregate = changeRequestService.getAggregate(context.getId());
    }

    public boolean isDrafted() {
        return Objects.equals(context.getStatus(), ChangeRequestStatus.DRAFTED.getStatusCode());
    }

    public boolean isSubmitted() {
        return Objects.equals(context.getStatus(), ChangeRequestStatus.NEW.getStatusCode());
    }

    public boolean isSolutionDefined() {
        return Objects.equals(context.getStatus(), ChangeRequestStatus.SOLUTION_DEFINED.getStatusCode());
    }

    public boolean isImpactAnalyzed() {
        return Objects.equals(context.getStatus(), ChangeRequestStatus.IMPACT_ANALYZED.getStatusCode());
    }

    public boolean isApproved() {
        return Objects.equals(context.getStatus(), ChangeRequestStatus.APPROVED.getStatusCode());
    }

    public boolean isClosed() {
        return Objects.equals(context.getStatus(), ChangeRequestStatus.CLOSED.getStatusCode());
    }

    public boolean isRejected() {
        return Objects.equals(context.getStatus(), ChangeRequestStatus.REJECTED.getStatusCode());
    }

    public boolean isObsoleted() {
        return Objects.equals(context.getStatus(), ChangeRequestStatus.OBSOLETED.getStatusCode());
    }

    public boolean isNotObsoleted() {
        return Objects.equals(context.getStatus(), ChangeRequestStatus.OBSOLETED.getStatusCode());
    }

    public User getCreator() {
        return context.getCreator();
    }


    public boolean isChangeNoticeImplemented() {
        List<ChangeRequestContext> changeNoticeContexts = context.getContexts().stream().filter(context -> context.getType().toUpperCase().equals("CHANGENOTICE")).collect(Collectors.toList());
        if (!changeNoticeContexts.isEmpty() && Objects.nonNull(changeNoticeContexts.get(0).getStatus())) {
            return changeNoticeContexts.get(0).getStatus().toUpperCase().equals("IMPLEMENTED");
        }
        return false;
    }

    public boolean isChangeNoticeNotCreated() {
        if (Objects.isNull(context.getContexts()))
            return true;
        Optional<ChangeRequestContext> changeNoticeContext = context.getContexts().stream().filter(context -> context.getType().toUpperCase().equals("CHANGENOTICE")).findFirst();
        return changeNoticeContext.isEmpty();
    }

    public Integer getStatus() {
        return context.getStatus();
    }

    public Boolean isSecure() {
        return Objects.equals(context.getIsSecure(), true);
    }

    public Boolean isNotSecure() {
        return Objects.equals(context.getIsSecure(), false);
    }


    //methods to be executed on aggregate detail
    public Boolean isFunctionalSoftwareDependenciesAdded() {
        ChangeRequestAggregate changeRequestAggregate = this.getAggregate();
        if (Objects.nonNull(changeRequestAggregate)) {
            SolutionDefinition solutionDefinition = changeRequestAggregate.getSolutionDefinition();
            return Objects.nonNull(solutionDefinition.getFunctionalSoftwareDependencies());
        }
        return false;
    }

    public Boolean isCustomerImpactAdded() {
        ChangeRequestAggregate changeRequestAggregate = this.getAggregate();
        if (Objects.nonNull(changeRequestAggregate)) {
            CustomerImpact customerImpact =  changeRequestAggregate.getImpactAnalysis().getDetails().getCustomerImpact();
            return Objects.nonNull(customerImpact.getCustomerImpactResult());
        }
        return false;
    }

    public Boolean isPreinstallImpactAdded() {
        ChangeRequestAggregate changeRequestAggregate = this.getAggregate();
        if (Objects.nonNull(changeRequestAggregate)) {
            PreinstallImpact preinstallImpact = changeRequestAggregate.getImpactAnalysis().getDetails().getPreinstallImpact();
            return Objects.nonNull(preinstallImpact.getPreinstallImpactResult());
        }
        return false;
    }

    public Boolean isImpactOnAvailabilityAdded() {
        ChangeRequestAggregate changeRequestAggregate = this.getAggregate();
        if (Objects.nonNull(changeRequestAggregate)) {
            ImpactAnalysis impactAnalysis = changeRequestAggregate.getImpactAnalysis().getGeneral();
            return Objects.nonNull(impactAnalysis.getImpactOnAvailability());
        }
        return false;
    }

    public Boolean isImpactOnSystemLevelPerformanceAdded() {
        ChangeRequestAggregate changeRequestAggregate = this.getAggregate();
        if (Objects.nonNull(changeRequestAggregate)) {
            ImpactAnalysis impactAnalysis = changeRequestAggregate.getImpactAnalysis().getGeneral();
            return Objects.nonNull(impactAnalysis.getImpactOnSystemLevelPerformance());
        }
        return false;
    }

    public Boolean isImplementationRangesAdded() {
        ChangeRequestAggregate changeRequestAggregate = this.getAggregate();
        if (Objects.nonNull(changeRequestAggregate)) {
            ImpactAnalysis impactAnalysis = changeRequestAggregate.getImpactAnalysis().getGeneral();
            return Objects.nonNull(impactAnalysis.getImplementationRanges()) && !impactAnalysis.getImplementationRanges().isEmpty();
        }
        return false;
    }

    public Boolean isCreatorTypeChangeRequest() {
        if (Objects.nonNull(context.getChangeOwnerType())) {
            return context.getChangeOwnerType().equals(ChangeOwnerType.CREATOR.name());
        }
        return false;
    }

    public Boolean isProjectTypeChangeRequest() {
        if (Objects.nonNull(context.getChangeOwnerType())) {
            return context.getChangeOwnerType().equals(ChangeOwnerType.PROJECT.name());
        }
        return false;
    }

    public Boolean isScopeAdded() {
        ChangeRequestAggregate changeRequestAggregate = this.getAggregate();
        Scope scope = changeRequestAggregate.getScope();
        if (Objects.nonNull(scope.getPackaging()) && Objects.nonNull(scope.getParts()) &&
                Objects.nonNull(scope.getTooling())) {
            return isPackagingScopeAdded(scope) && isPartsScopeAdded(scope) && isToolingScopeAdded(scope);
        }
        return false;
    }

    private Boolean isToolingScopeAdded(Scope scope) {
        if (Objects.nonNull(scope.getTooling())) {
            if (scope.getTooling().equals(ScopeValues.IN_SCOPE))
                return Objects.nonNull(scope.getPackagingDetail().getSupplierPackaging()) &&
                        Objects.nonNull(scope.getPackagingDetail().getReusablePackaging()) &&
                        Objects.nonNull(scope.getPackagingDetail().getShippingPackaging()) &&
                        Objects.nonNull(scope.getPackagingDetail().getStoragePackaging());
            return scope.getTooling().equals(ScopeValues.OUT_SCOPE);
        }
        return false;
    }

    private Boolean isPartsScopeAdded(Scope scope) {
        if (Objects.nonNull(scope.getParts())) {
            if (scope.getParts().equals(ScopeValues.IN_SCOPE))
                return Objects.nonNull(scope.getPackagingDetail().getSupplierPackaging()) &&
                        Objects.nonNull(scope.getPackagingDetail().getReusablePackaging()) &&
                        Objects.nonNull(scope.getPackagingDetail().getShippingPackaging()) &&
                        Objects.nonNull(scope.getPackagingDetail().getStoragePackaging());
            return scope.getParts().equals(ScopeValues.OUT_SCOPE);
        }
        return false;
    }

    private Boolean isPackagingScopeAdded(Scope scope) {
        if (Objects.nonNull(scope.getPackaging())) {
            if (scope.getPackaging().equals(ScopeValues.IN_SCOPE))
                return Objects.nonNull(scope.getPackagingDetail().getSupplierPackaging()) &&
                        Objects.nonNull(scope.getPackagingDetail().getReusablePackaging()) &&
                        Objects.nonNull(scope.getPackagingDetail().getShippingPackaging()) &&
                        Objects.nonNull(scope.getPackagingDetail().getStoragePackaging());
            else
                return scope.getPackaging().equals(ScopeValues.OUT_SCOPE);
        }
        return false;
    }

    public Boolean isProjectTypeCR() {
        return Objects.isNull(context.getChangeOwnerType()) || context.getChangeOwnerType().equals(ChangeOwnerType.PROJECT.name());
    }
    public Boolean isCreatorTypeCR() {
        return Objects.nonNull(context.getChangeOwnerType()) && context.getChangeOwnerType().equals(ChangeOwnerType.CREATOR.name());
    }

    public Boolean isDevelopmentLaborHoursAdded() {
        ChangeRequestAggregate changeRequestAggregate = this.getAggregate();
        if (Objects.nonNull(changeRequestAggregate)) {
            ImpactAnalysis impactAnalysis = changeRequestAggregate.getImpactAnalysis().getGeneral();
            return Objects.nonNull(impactAnalysis.getDevelopmentLaborHours());
        }
        return false;
    }

    public Boolean isInvestigationLaborHoursAdded() {
        ChangeRequestAggregate changeRequestAggregate = this.getAggregate();
        if (Objects.nonNull(changeRequestAggregate)) {
            ImpactAnalysis impactAnalysis = changeRequestAggregate.getImpactAnalysis().getGeneral();
            return Objects.nonNull(impactAnalysis.getInvestigationLaborHours());
        }
        return false;
    }

    public Boolean isImpactOnCycleTimeAdded() {
        ChangeRequestAggregate changeRequestAggregate = this.getAggregate();
        if (Objects.nonNull(changeRequestAggregate)) {
            ImpactAnalysis impactAnalysis = changeRequestAggregate.getImpactAnalysis().getGeneral();
            return Objects.nonNull(impactAnalysis.getImpactOnCycleTime());
        }
        return false;
    }

}
