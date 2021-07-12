package com.example.mirai.projectname.changerequestservice.impactanalysis.model.aggregate;

import com.example.mirai.projectname.changerequestservice.completebusinesscase.model.CompleteBusinessCase;
import com.example.mirai.projectname.changerequestservice.customerimpact.model.CustomerImpact;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.model.PreinstallImpact;
import com.example.mirai.libraries.core.annotation.LinkTo;
import com.example.mirai.libraries.core.model.AggregateInterface;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Immutable
@Getter
@Setter
public class ImpactAnalysisDetailsAggregate implements AggregateInterface {
    @LinkTo({ImpactAnalysis.class})
    private CustomerImpact customerImpact;
    @LinkTo({ImpactAnalysis.class})
    private PreinstallImpact preinstallImpact;
    @LinkTo({ImpactAnalysis.class})
    private CompleteBusinessCase completeBusinessCase;

}
