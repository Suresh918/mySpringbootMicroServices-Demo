package com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate;

import com.example.mirai.libraries.audit.model.ChangeLog;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.model.SolutionDefinition;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.aggregate.ImpactAnalysisChangeLogAggregate;
import com.example.mirai.projectname.changerequestservice.myteam.model.aggregate.ChangeRequestMyTeamChangeLogAggregate;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import com.example.mirai.libraries.core.annotation.Aggregate;
import com.example.mirai.libraries.core.annotation.AggregateRoot;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.annotation.LinkTo;
import com.example.mirai.libraries.core.model.AggregateInterface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class ChangeRequestChangeLogAggregate implements AggregateInterface {
    @AggregateRoot
    @EntityClass(ChangeRequest.class)
    private ChangeLog changeRequestChangeLog;
    @LinkTo({ChangeRequest.class})
    @EntityClass(Scope.class)
    private ChangeLog scopeChangeLog;
    @LinkTo({ChangeRequest.class})
    @EntityClass(SolutionDefinition.class)
    private ChangeLog solutionDefinitionChangeLog;
    @Aggregate
    private ImpactAnalysisChangeLogAggregate impactAnalysis;
    @Aggregate
    private ChangeRequestMyTeamChangeLogAggregate myTeamDetails;
}
