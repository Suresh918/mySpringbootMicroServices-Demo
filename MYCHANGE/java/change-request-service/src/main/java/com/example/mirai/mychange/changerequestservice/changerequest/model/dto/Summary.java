package com.example.mirai.projectname.changerequestservice.changerequest.model.dto;

import com.example.mirai.libraries.core.annotation.JoinKey;
import com.example.mirai.libraries.core.annotation.ViewMapper;
import com.example.mirai.libraries.core.model.BaseView;
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
public class Summary implements BaseView {
    @Id
    @JoinKey
    private Long id;
    private String title;
    private Integer status;
    private Integer implementationPriority;
    private String customerImpact;
    private String preinstallImpact;
    private String changeNoticeId;
    private String changeRequestNumber;

    @ViewMapper
    public Summary(Long id, String title, Integer status, Integer implementationPriority,
                   String customerImpact, String preinstallImpact, String changeNoticeId, String changeRequestNumber) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.implementationPriority = implementationPriority;
        this.customerImpact = customerImpact;
        this.preinstallImpact = preinstallImpact;
        this.changeNoticeId = changeNoticeId;
        this.changeRequestNumber = changeRequestNumber;
    }
}

