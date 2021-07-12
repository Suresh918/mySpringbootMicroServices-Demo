package com.example.mirai.projectname.changerequestservice.changerequest.model.dto;

import com.example.mirai.libraries.core.annotation.JoinKey;
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

@Entity
@Immutable
@Getter
@Setter
@NoArgsConstructor
public class SearchSummary implements BaseView  {
    @Id
    @JoinKey
    private Long id;
    private String title;
    private Integer status;
    private String statusLabel;
    private String analysisPriority;
    private Long completedActions;
    private Long openActions;
    private Long totalActions;
    @JsonIgnore
    private String changeSpecialist1UserId;
    @JsonIgnore
    private String changeSpecialist1DepartmentName;
    @JsonIgnore
    private String changeSpecialist1Email;
    @JsonIgnore
    private String changeSpecialist1FullName;
    @JsonIgnore
    private String changeSpecialist1Abbreviation;
    private User changeSpecialist1;
    private String changeBoards;
    private String changeControlBoards;
    @Transient
    private RuleSet changeBoardRuleSet;
    @JsonIgnore
    private String changeRequestNumber;

    @ViewMapper
    public SearchSummary(Long id, String title, Integer status, String analysisPriority, String changeSpecialist1UserId, String changeSpecialist1DepartmentName, String changeSpecialist1Email, String changeSpecialist1FullName, String changeSpecialist1Abbreviation, String changeBoards, String changeControlBoards, Long openActions, Long completedActions, Long totalActions, String changeRequestNumber) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.statusLabel = ChangeRequestStatus.getLabelByCode(status);
        this.analysisPriority = analysisPriority;
        this.changeBoards = changeBoards;
        this.changeControlBoards = changeControlBoards;
        this.openActions = openActions;
        this.completedActions = completedActions;
        this.totalActions = totalActions;
        this.changeSpecialist1Abbreviation = changeSpecialist1Abbreviation;
        this.changeSpecialist1DepartmentName = changeSpecialist1DepartmentName;
        this.changeSpecialist1Email = changeSpecialist1Email;
        this.changeSpecialist1FullName = changeSpecialist1FullName;
        this.changeSpecialist1UserId = changeSpecialist1UserId;
        this.changeSpecialist1 = new User();
        this.changeSpecialist1.setAbbreviation(changeSpecialist1Abbreviation);
        this.changeSpecialist1.setDepartmentName(changeSpecialist1DepartmentName);
        this.changeSpecialist1.setFullName(changeSpecialist1FullName);
        this.changeSpecialist1.setUserId(changeSpecialist1UserId);
        this.changeSpecialist1.setEmail(changeSpecialist1Email);
        this.changeRequestNumber = changeRequestNumber;
    }
}
