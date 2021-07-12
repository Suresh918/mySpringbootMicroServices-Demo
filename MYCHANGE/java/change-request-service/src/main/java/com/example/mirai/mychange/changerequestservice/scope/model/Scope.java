package com.example.mirai.projectname.changerequestservice.scope.model;

import com.example.mirai.libraries.core.model.ContextInterface;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestEvaluationContext;
import com.example.mirai.projectname.changerequestservice.scope.model.dto.PackagingDetail;
import com.example.mirai.projectname.changerequestservice.scope.model.dto.PartDetail;
import com.example.mirai.projectname.changerequestservice.scope.model.dto.ToolingDetail;
import com.example.mirai.projectname.changerequestservice.scope.service.ScopeService;
import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.annotation.SpELEvaluationContext;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.example.mirai.libraries.core.annotation.AclReferenceEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


@Entity
@DynamicUpdate
@Audited(withModifiedFlag = true)
@EntityListeners(AuditingEntityListener.class)
@ServiceClass(ScopeService.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SpELEvaluationContext(ChangeRequestEvaluationContext.class)
@AclReferenceEntity(ChangeRequest.class)
@Getter
@Setter
public class Scope implements BaseEntityInterface, Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String scopeDetails;

    private String parts;

    @Embedded
    private PartDetail partDetail;

    private String tooling;

    @Embedded
    private ToolingDetail toolingDetail;

    private String packaging;

    @Embedded
    private PackagingDetail packagingDetail;

    private String bop;

    @OneToOne(optional=true, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "change_request_id")
    @JsonIgnore
    @AbacScan
    private ChangeRequest changeRequest;

    @Override
    public String toString() {
        return "" + this.id;
    }

    @Override
    public void setStatus(Integer integer) {}

    @Override
    public List<ContextInterface> getContextsAsContextInterface() {
        return null;
    }

    @Override
    public Integer getStatus() { return null; }
}
