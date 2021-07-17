package com.example.mirai.projectname.releasepackageservice.myteam.service;

import com.example.mirai.libraries.core.model.BaseEvaluationContext;
import com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageStatus;

public class ReleasePackageMyTeamEvaluationContext extends BaseEvaluationContext<ReleasePackageMyTeam> {

    public boolean isReleasePackageClosed() {
        return context.getReleasePackage().getStatus().equals(ReleasePackageStatus.CLOSED.getStatusCode());
    }

    public boolean isReleasePackageObsoleted() {
        return context.getReleasePackage().getStatus().equals(ReleasePackageStatus.OBSOLETED.getStatusCode());
    }

    public boolean isReleasePackageSecure() {
        return context.getReleasePackage().getIsSecure() == true;
    }

}
