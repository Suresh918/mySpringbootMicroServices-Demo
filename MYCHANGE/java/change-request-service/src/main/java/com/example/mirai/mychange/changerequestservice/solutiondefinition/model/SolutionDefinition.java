package com.example.mirai.projectname.changerequestservice.solutiondefinition.model;

import com.example.mirai.libraries.core.annotation.AclReferenceEntity;
import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.annotation.SpELEvaluationContext;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.ContextInterface;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestEvaluationContext;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.service.SolutionDefinitionService;
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
@ServiceClass(SolutionDefinitionService.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SpELEvaluationContext(ChangeRequestEvaluationContext.class)
@AclReferenceEntity(ChangeRequest.class)
@Getter
@Setter
public class SolutionDefinition implements BaseEntityInterface, Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String functionalSoftwareDependencies;
    @Column(length = 1024)
    private String functionalSoftwareDependenciesDetails;
    private String functionalHardwareDependencies;
    @Column(length = 1024)
    private String functionalHardwareDependenciesDetails;

    private String hardwareSoftwareDependenciesAligned;
    @Column(length = 1024)
    private String hardwareSoftwareDependenciesAlignedDetails;

    private String testAndReleaseStrategy;
    @Column(length = 1024)
    private String testAndReleaseStrategyDetails;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "products_affected", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    @OrderColumn
    @Column(length = 1024)
    private List<String> productsAffected;

    @Column(length = 1024)
    private String productsModuleAffected;

    private String alignedWithFo;
    @Column(length = 1024)
    private String alignedWithFoDetails;

    @Column(columnDefinition = "TEXT")
    private String technicalRecommendation;

    @OneToOne(optional=true, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "change_request_id")
    @JsonIgnore
    @AbacScan
    private ChangeRequest changeRequest;
    //initializer block
    /*{
        this.productsAffected = new ArrayList<>();
    }*/
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
