package com.example.mirai.projectname.changerequestservice.impactanalysis.model.aggregate;

import com.example.mirai.projectname.changerequestservice.changerequest.model.*;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.libraries.core.annotation.Aggregate;
import com.example.mirai.libraries.core.annotation.LinkTo;
import com.example.mirai.libraries.core.model.AggregateInterface;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Immutable
@Getter
@Setter
public class ImpactAnalysisAggregate implements AggregateInterface {
    @LinkTo({ChangeRequest.class})
    private ImpactAnalysis general;
    @Aggregate
    private ImpactAnalysisDetailsAggregate details;
    
}
