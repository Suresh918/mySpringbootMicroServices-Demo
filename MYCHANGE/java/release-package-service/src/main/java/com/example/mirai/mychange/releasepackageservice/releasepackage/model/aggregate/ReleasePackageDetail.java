package com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.releasepackageservice.myteam.model.aggregate.ReleasePackageMyTeamAggregate;
import com.example.mirai.projectname.releasepackageservice.myteam.model.aggregate.ReleasePackageMyTeamMemberAggregate;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.PrerequisiteReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReleasePackageDetail implements Serializable {
    private Long id;
    private String releasePackageNumber;
    private List<ReleasePackageContext> contexts;
    private String title;
    private Integer status;
    private Boolean isSecure;
    private User changeSpecialist3;
    private User executor;
    private User creator;
    private Date createdOn;
    private Date plannedReleaseDate;
    private Date plannedEffectiveDate;
    private Boolean sapChangeControl;
    private String prerequisitesApplicable;
    private String prerequisitesDetail;
    private List<PrerequisiteReleasePackage> releasePackagePrerequisites;
    private List<String> changeControlBoards;
    private List<String> tags;
    private List<String> types;
    private String productId;
    private String projectId;
    private User changeOwner;
    private User plmCoordinator;
    private String changeOwnerType;
    private String erValidFromInputStrategy;
    private MyTeamDetail myTeam;

    public ReleasePackageDetail(ReleasePackageAggregate releasePackageAggregate) {
        ReleasePackage releasePackage = releasePackageAggregate.getReleasePackage();
        this.id = releasePackage.getId();
        this.changeControlBoards = releasePackage.getChangeControlBoards();
        this.projectId = releasePackage.getProjectId();
        this.plannedEffectiveDate = releasePackage.getPlannedEffectiveDate();
        this.changeSpecialist3 = releasePackage.getChangeSpecialist3();
        this.contexts = releasePackage.getContexts();
        this.createdOn = releasePackage.getCreatedOn();
        this.creator = releasePackage.getCreator();
        this.executor = releasePackage.getExecutor();
        this.isSecure = releasePackage.getIsSecure();
        this.plannedReleaseDate = releasePackage.getPlannedReleaseDate();
        this.prerequisitesApplicable = releasePackage.getPrerequisitesApplicable();
        this.releasePackageNumber = releasePackage.getReleasePackageNumber();
        this.prerequisitesDetail = releasePackage.getPrerequisitesDetail();
        this.sapChangeControl = releasePackage.getSapChangeControl();
        this.status = releasePackage.getStatus();
        this.tags = releasePackage.getTags();
        this.releasePackagePrerequisites = releasePackage.getPrerequisiteReleasePackages();
        this.title = releasePackage.getTitle();
        this.types = releasePackage.getTypes();
        this.changeOwner = releasePackage.getChangeOwner();
        this.changeOwnerType = releasePackage.getChangeOwnerType();
        this.erValidFromInputStrategy = releasePackage.getErValidFromInputStrategy();
        this.plmCoordinator = releasePackage.getPlmCoordinator();
        this.productId = releasePackage.getProductId();
        this.myTeam = new MyTeamDetail(releasePackageAggregate.getMyTeamDetails());
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyTeamDetail {
        private Long id;
        private Set<ReleasePackageMyTeamMemberAggregate> members;

        public MyTeamDetail(ReleasePackageMyTeamDetailsAggregate changeRequestMyTeamAggregate) {
            this.id = changeRequestMyTeamAggregate.getMyTeam().getId();
            this.members = changeRequestMyTeamAggregate.getMembers();
        }

        public MyTeamDetail(ReleasePackageMyTeamAggregate releasePackageMyTeamAggregate) {
            this.id = releasePackageMyTeamAggregate.getMyTeam().getId();
            this.members = releasePackageMyTeamAggregate.getMembers();
        }
    }

}
