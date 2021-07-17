package com.example.mirai.projectname.releasepackageservice.releasepackage.service;


import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.BaseEvaluationContext;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ChangeOwnerType;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageContext;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageStatus;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageTypes;
import com.example.mirai.projectname.releasepackageservice.shared.Constants;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ReleasePackageEvaluationContext extends BaseEvaluationContext<ReleasePackage> {
    public Integer getStatus() {
        return context.getStatus();
    }

    public boolean isReleasePackageSubmitted() {
        return context.getStatus().equals(ReleasePackageStatus.DRAFTED.getStatusCode());
    }

    public boolean isReleasePackageCreated() {
        return context.getStatus().equals(ReleasePackageStatus.CREATED.getStatusCode());
    }

    public boolean isReleasePackageReadyForRelease() {
        return context.getStatus().equals(ReleasePackageStatus.READY_FOR_RELEASE.getStatusCode());
    }

    public boolean isReleasePackageReleased() {
        return context.getStatus().equals(ReleasePackageStatus.RELEASED.getStatusCode());
    }

    public boolean isReleasePackageClosed() {
        return context.getStatus().equals(ReleasePackageStatus.CLOSED.getStatusCode());
    }

    public boolean isReleasePackageObsoleted() {
        return context.getStatus().equals(ReleasePackageStatus.OBSOLETED.getStatusCode());
    }

    public boolean isTypeSelected() {
        return Objects.nonNull(context.getTypes()) && !context.getTypes().isEmpty();
    }

    public boolean isReleasePackageSecure() {
        if (Objects.isNull(context.getIsSecure())) {
            return false;
        }
        return context.getIsSecure().equals(true);
    }

    public boolean isReleasePackageHavingReview() {
        Optional<ReleasePackageContext> releasePackageContext = context.getContexts().stream().filter(context -> context.getType().equals("REVIEW")).findAny();
        return !releasePackageContext.isEmpty();
    }

    public boolean isAllReviewStatusCompleted() {
        List<ReleasePackageContext> reviewContextList = context.getContexts().stream().filter(context -> context.getType().equals("REVIEW")).collect(Collectors.toList());
        List<ReleasePackageContext> completedReviewContextList = context.getContexts().stream().filter(context -> context.getType().equals("REVIEW") && context.getStatus().equals("4")).collect(Collectors.toList());
        return (reviewContextList.size()==completedReviewContextList.size());
    }

    public boolean isChangeNoticePlanned() {
        if (Objects.isNull(context.getContexts()))
            return false;
        Optional<ReleasePackageContext> changeNoticeContextWithPlannedStatus = context.getContexts().stream().filter(context -> context.getType().equals("CHANGENOTICE") && context.getStatus().equals("PLANNED")).findFirst();
        return changeNoticeContextWithPlannedStatus.isPresent();
    }

    public boolean isReleasePackageTypeWorkInstruction(){
        if (Objects.isNull(context.getTypes()))
            return false;
        return context.getTypes().contains(ReleasePackageTypes.WI.name());
    }

    public boolean isChangeObjectContextStatusNew() {
        if (Objects.isNull(context.getContexts()))
            return false;
        Optional<ReleasePackageContext> changeObjectContext = context.getContexts().stream().filter(context -> context.getType().equals("CHANGEOBJECT")).findAny();
        if(changeObjectContext.isPresent()) {
            return changeObjectContext.get().getStatus().equalsIgnoreCase(Constants.CHANGE_OBJECT_STATUS_NEW);
        }
        return false;
    }

    public boolean isChangeObjectContextStatusCreated() {
        if (Objects.isNull(context.getContexts()))
            return false;
        Optional<ReleasePackageContext> changeObjectContext = context.getContexts().stream().filter(context -> context.getType().equals("CHANGEOBJECT")).findFirst();
        if(changeObjectContext.isPresent()) {
            return changeObjectContext.get().getStatus().equalsIgnoreCase(Constants.CHANGE_OBJECT_STATUS_CREATED);
        }
        return false;
    }

    public boolean isChangeObjectContextStatusReadyForReleased() {
        if (Objects.isNull(context.getContexts()))
            return false;
        Optional<ReleasePackageContext> changeObjectContext = context.getContexts().stream().filter(context -> context.getType().equals("CHANGEOBJECT")).findFirst();
        if(changeObjectContext.isPresent()) {
            return changeObjectContext.get().getStatus().equalsIgnoreCase(Constants.CHANGE_OBJECT_STATUS_VALIDATED);
        }
        return false;
    }

    public boolean isChangeObjectContextStatusReleased() {
        if (Objects.isNull(context.getContexts()))
            return false;
        Optional<ReleasePackageContext> changeObjectContext = context.getContexts().stream().filter(context -> context.getType().equals("CHANGEOBJECT")).findFirst();
        if(changeObjectContext.isPresent()) {
            return changeObjectContext.get().getStatus().equalsIgnoreCase(Constants.CHANGE_OBJECT_STATUS_RELEASED);
        }
        return false;
    }

    public boolean isChangeObjectContextStatusClosed() {
        if (Objects.isNull(context.getContexts()))
            return false;
        Optional<ReleasePackageContext> changeObjectContext = context.getContexts().stream().filter(context -> context.getType().equals("CHANGEOBJECT")).findFirst();
        if(changeObjectContext.isPresent()) {
            return changeObjectContext.get().getStatus().equalsIgnoreCase(Constants.CHANGE_OBJECT_STATUS_CLOSED);
        }
        return false;
    }

    public boolean isChangeObjectContextStatusPublished() {
        if (Objects.isNull(context.getContexts()))
            return false;
        Optional<ReleasePackageContext> changeObjectContext = context.getContexts().stream().filter(context -> context.getType().equals("CHANGEOBJECT")).findFirst();
        if(changeObjectContext.isPresent()) {
            return changeObjectContext.get().getStatus().equalsIgnoreCase("PUBLISHED");
        }
        return false;
    }

    public boolean isChangeOwnerTypeProject() {
        return Objects.isNull(context.getChangeOwnerType()) || context.getChangeOwnerType().equalsIgnoreCase(ChangeOwnerType.PROJECT.name());

    }

    public boolean isChangeOwnerTypeCreator() {
        return Objects.nonNull(context.getChangeOwnerType()) && context.getChangeOwnerType().equalsIgnoreCase(ChangeOwnerType.CREATOR.name());

    }

    public boolean isReleasePackageTypeHwOrOp(){
        return Objects.nonNull(context.getTypes()) && (context.getTypes().contains(ReleasePackageTypes.HW.getType()) || context.getTypes().contains(ReleasePackageTypes.PR.getType()));
    }

    public boolean isSapChangeControlSetToTrue() {
        return Objects.nonNull(context.getSapChangeControl()) && context.getSapChangeControl().equals(true);
    }

    public boolean isSapChangeControlSetToFalse() {
        return Objects.isNull(context.getSapChangeControl()) || context.getSapChangeControl().equals(false);
    }

    public boolean isPlmCoordinatorSet() {
        return Objects.nonNull(context.getPlmCoordinator()) && Objects.nonNull(context.getPlmCoordinator().getUserId());
    }

    public boolean isReleasePackageHavingChangeObject() {
        Optional<ReleasePackageContext> changeObjectContext = context.getContexts().stream().filter(context -> context.getType().equals("CHANGEOBJECT")).findAny();
        return !changeObjectContext.isEmpty();
    }

    public boolean isReleasePackageHavingReviewAndIsCompleted() {
        return !isReleasePackageHavingReview() || (isReleasePackageHavingReview() && isAllReviewStatusCompleted());
    }

    public boolean isReleasePackageHavingChangeObjectAndIsReadyForReleaseOrReleasedOrClosed() {
        return !isReleasePackageHavingChangeObject() || (isReleasePackageHavingChangeObject() && (isChangeObjectContextStatusReadyForReleased() || isChangeObjectContextStatusReleased() || isChangeObjectContextStatusClosed()));
    }

    public boolean isReleasePackageHavingChangeObjectAndIsReleasedOrPublished() {
        return !isReleasePackageHavingChangeObject() || (isReleasePackageHavingChangeObject() && (isChangeObjectContextStatusReleased() || isChangeObjectContextStatusClosed()));
    }

    public boolean isReleasePackageHavingChangeObjectAndIsPublished() {
        return !isReleasePackageHavingChangeObject() || (isReleasePackageHavingChangeObject() && isChangeObjectContextStatusClosed());
    }
    public boolean isTeamCenterCreated() {
        if (Objects.isNull(context.getContexts()))
            return false;
        Optional<ReleasePackageContext> teamCenterIdExists = context.getContexts().stream().filter(context -> context.getType().equals("TEAMCENTER")).findFirst();
        return teamCenterIdExists.isPresent();
    }

    public boolean isSapMdgCrCreated() {
        if (Objects.isNull(context.getContexts()))
            return false;
        Optional<ReleasePackageContext> sapMdgCrExists = context.getContexts().stream().filter(context -> context.getType().equals("MDG-CR")).findFirst();
        return sapMdgCrExists.isPresent();
    }

    public boolean isPafEnabled() {
        ReleasePackageConfigurationProperties pafAttribute = (ReleasePackageConfigurationProperties) ApplicationContextHolder.getBean(ReleasePackageConfigurationProperties.class);
        if(pafAttribute.getEnabled().equalsIgnoreCase("true"))
            return true;
        if(pafAttribute.getEnabled().equalsIgnoreCase("false"))
            return false;
        return false;
    }


}
