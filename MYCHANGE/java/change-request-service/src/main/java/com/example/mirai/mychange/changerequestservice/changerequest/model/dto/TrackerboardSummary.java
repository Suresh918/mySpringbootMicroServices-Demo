package com.example.mirai.projectname.changerequestservice.changerequest.model.dto;

import com.example.mirai.libraries.core.annotation.JoinKey;
import com.example.mirai.libraries.core.annotation.ViewMapper;
import com.example.mirai.libraries.core.model.BaseView;
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
public class TrackerboardSummary implements BaseView {
    @Id
    @JoinKey
    private Long id;
    private String title;
    private String projectId;
    private Integer status;

    @JsonIgnore
    private String creator;
    @JsonIgnore
    private String boards;
    @JsonIgnore
    private String memberData;
    @JsonIgnore
    private Boolean isSecure;
    @JsonIgnore
    private String productId;
    @JsonIgnore
    private String airIds;
    @JsonIgnore
    private String pbsId;

    @ViewMapper
    public TrackerboardSummary(Long id, String title, String projectId, Integer status) {
        this.id = id;
        this.title = title;
        this.projectId = projectId;
        this.status = status;
    }
}
