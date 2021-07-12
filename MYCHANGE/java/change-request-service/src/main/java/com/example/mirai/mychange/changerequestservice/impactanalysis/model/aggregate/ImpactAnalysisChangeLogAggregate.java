package com.example.mirai.projectname.changerequestservice.impactanalysis.model.aggregate;

import com.example.mirai.libraries.audit.model.ChangeLog;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.libraries.core.annotation.Aggregate;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.annotation.LinkTo;
import com.example.mirai.libraries.core.model.AggregateInterface;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Immutable
@Getter
@Setter
public class ImpactAnalysisChangeLogAggregate implements AggregateInterface {
    @LinkTo({ChangeRequest.class})
    @EntityClass(ImpactAnalysis.class)
    private ChangeLog general;
    @Aggregate
    private ImpactAnalysisDetailsChangeLogAggregate details;
    
}
