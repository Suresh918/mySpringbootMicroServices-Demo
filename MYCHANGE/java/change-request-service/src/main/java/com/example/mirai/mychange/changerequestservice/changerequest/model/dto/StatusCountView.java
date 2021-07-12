package com.example.mirai.projectname.changerequestservice.changerequest.model.dto;

import com.example.mirai.libraries.core.annotation.JoinKey;
import com.example.mirai.libraries.core.annotation.ViewMapper;
import com.example.mirai.libraries.core.model.BaseView;
import com.example.mirai.libraries.entity.model.StatusOverview.StatusCount;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestState;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestStatus;
import com.example.mirai.projectname.changerequestservice.shared.util.AnalysisPriorityValues;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Immutable
@Getter
@Setter
@NoArgsConstructor
public class StatusCountView implements BaseView {
    @Id
    private Long id;
    @JsonIgnore
    private String changeRequestNumber;
    private String title;
    private Integer status;
    private Integer analysisPriority;
    private Integer implementationPriority;
    @JsonIgnore
    private String memberData;
    @JsonIgnore
    private String impact;
    @JsonIgnore
    private String changeOwnerType;
    @JsonIgnore
    private String projectId;
    @JsonIgnore
    private String productId;
    @JsonIgnore
    private Date createdOn;
    @JsonIgnore
    private String boards;
    @JsonIgnore
    private String airIds;
    @JsonIgnore
    private String pbsId;
    @JsonIgnore
    private String changeSpecialist1;
    @JsonIgnore
    private String changeSpecialist2;
    @JsonIgnore
    private String creator;
    @JsonIgnore
    private String changeOwner;

}

