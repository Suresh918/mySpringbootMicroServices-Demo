package com.example.mirai.projectname.changerequestservice.changerequest.model.dto;

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
public class AgendaItemContext implements BaseView {
    @Id
    private Long changeRequestId;
    private String agendaItemId;

    @ViewMapper
    public AgendaItemContext(String agendaItemId, Long changeRequestId) {
        this.changeRequestId = changeRequestId;
        this.agendaItemId = agendaItemId;
    }
}
