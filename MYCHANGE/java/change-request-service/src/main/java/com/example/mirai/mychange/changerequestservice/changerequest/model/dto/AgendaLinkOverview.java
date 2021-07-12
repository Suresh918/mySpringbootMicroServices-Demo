package com.example.mirai.projectname.changerequestservice.changerequest.model.dto;

import com.example.mirai.libraries.core.annotation.ViewMapper;
import com.example.mirai.libraries.core.model.BaseView;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestStatus;
import com.example.mirai.projectname.changerequestservice.changerequest.model.RuleSet;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;
import java.util.Objects;

@Entity
@Immutable
@Getter
@Setter
@NoArgsConstructor
public class AgendaLinkOverview implements BaseView {
    @Id
    private Long changeRequestId;
    private String agendaItemId;
    private String title;
    private Integer status;
    private String statusLabel;
    private String projectId;
    private Long completedActions;
    private Long openActions;
    private Long totalActions;
    private Date changeRequestClosedOn;
    @JsonIgnore
    private String changeSpecialist2UserId;
    @JsonIgnore
    private String changeSpecialist2DepartmentName;
    @JsonIgnore
    private String changeSpecialist2Email;
    @JsonIgnore
    private String changeSpecialist2FullName;
    @JsonIgnore
    private String changeSpecialist2Abbreviation;
    @Transient
    private User changeSpecialist2;
    private String changeBoards;
    private String changeControlBoards;
    @Transient
    private PmoDetails pmoDetails;
    private Integer implementationPriority;
    private String requirementsForImplementationPlan;
    private Integer analysisPriority;
    @Transient
    private RuleSet changeBoardRuleSet;
    private Integer memberCount;

    @ViewMapper
    public AgendaLinkOverview(String agendaItemId, Long changeRequestId, String title, Integer status, String projectId, String changeSpecialist2UserId,
                              String changeSpecialist2DepartmentName, String changeSpecialist2Email, String changeSpecialist2FullName,
                              String changeSpecialist2Abbreviation, String changeBoards, String changeControlBoards,
                              Long openActions, Long completedActions, Long totalActions, Integer implementationPriority,
                              String requirementsForImplementationPlan, Integer analysisPriority, Integer memberCount, Date changeRequestClosedOn) {
        this.agendaItemId = agendaItemId;
        this.changeRequestId = changeRequestId;
        this.title = title;
        this.status = status;
        this.statusLabel = ChangeRequestStatus.getLabelByCode(status);
        this.projectId = projectId;
        this.changeBoards = changeBoards;
        this.changeControlBoards = changeControlBoards;
        this.openActions = openActions;
        this.completedActions = completedActions;
        this.totalActions = totalActions;
        this.changeSpecialist2Abbreviation = changeSpecialist2Abbreviation;
        this.changeSpecialist2DepartmentName = changeSpecialist2DepartmentName;
        this.changeSpecialist2Email = changeSpecialist2Email;
        this.changeSpecialist2FullName = changeSpecialist2FullName;
        this.changeSpecialist2UserId = changeSpecialist2UserId;
        if (Objects.nonNull(changeSpecialist2UserId)) {
            this.changeSpecialist2 = new User();
            this.changeSpecialist2.setAbbreviation(changeSpecialist2Abbreviation);
            this.changeSpecialist2.setDepartmentName(changeSpecialist2DepartmentName);
            this.changeSpecialist2.setFullName(changeSpecialist2FullName);
            this.changeSpecialist2.setUserId(changeSpecialist2UserId);
            this.changeSpecialist2.setEmail(changeSpecialist2Email);
        }
        this.analysisPriority = analysisPriority;
        this.implementationPriority = implementationPriority;
        this.requirementsForImplementationPlan = requirementsForImplementationPlan;
        this.memberCount = memberCount;
        this.changeRequestClosedOn = changeRequestClosedOn;
    }
}
