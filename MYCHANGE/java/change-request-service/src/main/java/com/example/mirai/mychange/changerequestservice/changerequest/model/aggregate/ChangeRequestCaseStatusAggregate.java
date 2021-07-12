package com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate;

import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.libraries.core.annotation.AggregateRoot;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.CaseStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class ChangeRequestCaseStatusAggregate implements AggregateInterface {
    @AggregateRoot
    @EntityClass(ChangeRequest.class)
    private CaseStatus changeRequestCaseStatus;
}
