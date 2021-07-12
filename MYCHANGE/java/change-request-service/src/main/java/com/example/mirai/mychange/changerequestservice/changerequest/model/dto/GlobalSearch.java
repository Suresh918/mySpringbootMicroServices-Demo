package com.example.mirai.projectname.changerequestservice.changerequest.model.dto;

import com.example.mirai.libraries.core.annotation.ViewMapper;
import com.example.mirai.libraries.core.model.BaseView;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Immutable
@Getter
@Setter
@NoArgsConstructor
public class GlobalSearch implements BaseView {
    @Id
    private Long id;
    @JsonIgnore
    private String changeRequestNumber;
    private String title;
    private Integer status;
    private String statusLabel;
    private String type;
    @JsonIgnore
    private String boards;
    @JsonIgnore
    private String memberData;
    @JsonIgnore
    private Boolean isSecure;

    @ViewMapper
    public GlobalSearch(Long id, String title, Integer status) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.statusLabel = ChangeRequestStatus.getLabelByCode(status);
        this.type = "ChangeRequest";
    }
}

