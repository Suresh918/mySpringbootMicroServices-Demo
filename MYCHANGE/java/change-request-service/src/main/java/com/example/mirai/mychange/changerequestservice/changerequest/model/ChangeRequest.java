package com.example.mirai.projectname.changerequestservice.changerequest.model;

import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.annotation.SpELEvaluationContext;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.ContextInterface;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.example.mirai.libraries.security.abac.annotation.AbacSubject;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestEvaluationContext;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestService;
import com.example.mirai.projectname.changerequestservice.myteam.model.ChangeRequestMyTeam;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Entity
@DynamicUpdate
@Audited(withModifiedFlag = true)
@EntityListeners(AuditingEntityListener.class)
@ServiceClass(ChangeRequestService.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SpELEvaluationContext(ChangeRequestEvaluationContext.class)
@Getter
@Setter
@AbacScan({ChangeRequestMyTeam.class})
@NoArgsConstructor
public class ChangeRequest implements BaseEntityInterface, Serializable {

    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "change_request_id_seq"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "10000"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    private Long id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "change_request_contexts", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    @OrderColumn
    private List<ChangeRequestContext> contexts;

    @Column(length = 256)
    private String title;

    private Integer status;

    private Boolean isSecure;

    @AbacSubject(role = "Static:change-specialist-1", principal = "userId")
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "changeSpecialist1_userId")),
            @AttributeOverride(name = "fullName", column = @Column(name = "changeSpecialist1_fullName")),
            @AttributeOverride(name = "email", column = @Column(name = "changeSpecialist1_email")),
            @AttributeOverride(name = "departmentName", column = @Column(name = "changeSpecialist1_departmentName")),
            @AttributeOverride(name = "abbreviation", column = @Column(name = "changeSpecialist1_abbreviation"))
    })
    private User changeSpecialist1;

    @AbacSubject(role = "Static:change-specialist-2", principal = "userId")
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "changeSpecialist2_userId")),
            @AttributeOverride(name = "fullName", column = @Column(name = "changeSpecialist2_fullName")),
            @AttributeOverride(name = "email", column = @Column(name = "changeSpecialist2_email")),
            @AttributeOverride(name = "departmentName", column = @Column(name = "changeSpecialist2_departmentName")),
            @AttributeOverride(name = "abbreviation", column = @Column(name = "changeSpecialist2_abbreviation"))
    })
    private User changeSpecialist2;

    @CreatedBy
    @Embedded
    @AbacSubject(role = "Static:creator", principal = "userId")
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "creator_userId")),
            @AttributeOverride(name = "fullName", column = @Column(name = "creator_fullName")),
            @AttributeOverride(name = "email", column = @Column(name = "creator_email")),
            @AttributeOverride(name = "departmentName", column = @Column(name = "creator_departmentName")),
            @AttributeOverride(name = "abbreviation", column = @Column(name = "creator_abbreviation"))
    })
    private User creator;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "change_control_boards", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    @OrderColumn
    @AbacSubject(role = "StaticGroup:change-control-board-member")
    private List<String> changeControlBoards;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "change_boards", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    @OrderColumn
    @AbacSubject(role = "StaticGroup:change-board-member")
    private List<String> changeBoards;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "issue_types", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    @OrderColumn
    private List<String> issueTypes;

    private String changeRequestType;

    private Integer analysisPriority;
    private String projectId;
    private String productId;
    private String functionalClusterId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "reasons_for_change", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    @OrderColumn
    private List<String> reasonsForChange;

    @Column(columnDefinition = "TEXT")
    private String problemDescription;

    @Column(columnDefinition = "TEXT")
    private String proposedSolution;
    @Column(length = 1024)
    private String rootCause;

    @Column(length = 1024)
    private String benefitsOfChange;

    private Integer implementationPriority;
    @Column(length = 2048)
    private String requirementsForImplementationPlan;
    private Float excessAndObsolescenceSavings;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "dependent_change_requests", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    @OrderColumn
    private List<String> dependentChangeRequestIds;

    private RuleSet changeBoardRuleSet;

    private String changeOwnerType;

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

    //initializer block
    /*{
        this.dependentChangeRequestIds = new ArrayList<>();
        this.changeBoards = new ArrayList<>();
        this.changeControlBoards = new ArrayList<>();
        this.contexts = new ArrayList<>();
        this.reasonsForChange = new ArrayList<>();
        this.issueTypes = new ArrayList<>();
    }*/
    @Override
    public String toString() {
        return "" + this.id;
    }

    public List<ContextInterface> getContextsAsContextInterface() {
        if (Objects.isNull(contexts))
            return new ArrayList<>();
        return contexts.stream().map(context -> (ContextInterface) context).collect(Collectors.toList());
    }
}
