package com.example.mirai.projectname.changerequestservice.impactanalysis.model.aggregate;

import com.example.mirai.libraries.audit.model.ChangeLog;
import com.example.mirai.projectname.changerequestservice.completebusinesscase.model.CompleteBusinessCase;
import com.example.mirai.projectname.changerequestservice.customerimpact.model.CustomerImpact;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.model.PreinstallImpact;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.annotation.LinkTo;
import com.example.mirai.libraries.core.model.AggregateInterface;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Immutable
@Getter
@Setter
public class ImpactAnalysisDetailsChangeLogAggregate implements AggregateInterface {
    @LinkTo({ImpactAnalysis.class})
    @EntityClass(CustomerImpact.class)
    private ChangeLog customerImpact;
    @LinkTo({ImpactAnalysis.class})
    @EntityClass(PreinstallImpact.class)
    private ChangeLog preinstallImpact;
    @LinkTo({ImpactAnalysis.class})
    @EntityClass(CompleteBusinessCase.class)
    private ChangeLog completeBusinessCase;

}
