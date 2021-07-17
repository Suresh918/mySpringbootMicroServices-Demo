package com.example.mirai.projectname.releasepackageservice.releasepackage.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.annotation.SpELEvaluationContext;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.ContextInterface;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.example.mirai.libraries.security.abac.annotation.AbacSubject;
import com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageEvaluationContext;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageService;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@DynamicUpdate
@Audited(withModifiedFlag = true)
@EntityListeners(AuditingEntityListener.class)
@ServiceClass(ReleasePackageService.class)
@SpELEvaluationContext(ReleasePackageEvaluationContext.class)
@Getter
@Setter
@AbacScan({ReleasePackageMyTeam.class})
public class ReleasePackage implements BaseEntityInterface, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String releasePackageNumber;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "release_package_contexts", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    @OrderColumn
    private List<ReleasePackageContext> contexts;

    public List<ContextInterface> getContextsAsContextInterface() {
        if (Objects.isNull(contexts))
            return new ArrayList<>();
        return contexts.stream().map(context -> (ContextInterface) context).collect(Collectors.toList());
    }

    @Column(length = 256)
    private String title;

    private Integer status;

    private Boolean isSecure;
    @AbacSubject(role = "Static:change-specialist-3", principal = "userId")
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "change_specialist_3_user_id")),
            @AttributeOverride(name = "fullName", column = @Column(name = "change_specialist_3_full_name")),
            @AttributeOverride(name = "email", column = @Column(name = "change_specialist_3_email")),
            @AttributeOverride(name = "departmentName", column = @Column(name = "change_specialist_3_department_name")),
            @AttributeOverride(name = "abbreviation", column = @Column(name = "change_specialist_3_abbreviation"))
    })
    private User changeSpecialist3;

    @AbacSubject(role = "Static:executor", principal = "userId")
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "executor_user_id")),
            @AttributeOverride(name = "fullName", column = @Column(name = "executor_full_name")),
            @AttributeOverride(name = "email", column = @Column(name = "executor_email")),
            @AttributeOverride(name = "departmentName", column = @Column(name = "executor_department_name")),
            @AttributeOverride(name = "abbreviation", column = @Column(name = "executor_abbreviation"))
    })
    private User executor;

    @CreatedBy
    @Embedded
    @AbacSubject(role = "Static:creator", principal = "userId")
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "creator_user_id")),
            @AttributeOverride(name = "fullName", column = @Column(name = "creator_full_name")),
            @AttributeOverride(name = "email", column = @Column(name = "creator_email")),
            @AttributeOverride(name = "departmentName", column = @Column(name = "creator_department_name")),
            @AttributeOverride(name = "abbreviation", column = @Column(name = "creator_abbreviation"))
    })
    private User creator;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    //timezone must be present
    @Temporal(TemporalType.TIMESTAMP)
    private Date plannedReleaseDate;

    //timezone must be present
    @Temporal(TemporalType.TIMESTAMP)
    private Date plannedEffectiveDate;

    private Boolean sapChangeControl;

    private String prerequisitesApplicable;

    @Column(length = 1024)
    private String prerequisitesDetail;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "prerequisite_release_packages", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    @OrderColumn
    private List<PrerequisiteReleasePackage> prerequisiteReleasePackages;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "change_control_boards", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    @OrderColumn
    @AbacSubject(role = "StaticGroup:change-control-board-member")
    private List<String> changeControlBoards;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tags", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    @OrderColumn
    private List<String> tags;

    private String productId;
    private String projectId;

    private String erValidFromInputStrategy;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "types", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    @OrderColumn
    private List<String> types;

    @Embedded
    @AbacSubject(role = "Static:change-owner", principal = "userId")
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "change_owner_user_id")),
            @AttributeOverride(name = "fullName", column = @Column(name = "change_owner_full_name")),
            @AttributeOverride(name = "email", column = @Column(name = "change_owner_email")),
            @AttributeOverride(name = "departmentName", column = @Column(name = "change_owner_department_name")),
            @AttributeOverride(name = "abbreviation", column = @Column(name = "change_owner_abbreviation"))
    })
    private User changeOwner;

    @Embedded
    @AbacSubject(role = "Static:plm-coordinator", principal = "userId")
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "plm_coordinator_user_id")),
            @AttributeOverride(name = "fullName", column = @Column(name = "plm_coordinator_full_name")),
            @AttributeOverride(name = "email", column = @Column(name = "plm_coordinator_email")),
            @AttributeOverride(name = "departmentName", column = @Column(name = "plm_coordinator_department_name")),
            @AttributeOverride(name = "abbreviation", column = @Column(name = "plm_coordinator_abbreviation"))
    })
    private User plmCoordinator;

    private String changeOwnerType;

    public ReleasePackage() {
    }

    @Override
    public String toString() {
        return "" + this.id;
    }
}
