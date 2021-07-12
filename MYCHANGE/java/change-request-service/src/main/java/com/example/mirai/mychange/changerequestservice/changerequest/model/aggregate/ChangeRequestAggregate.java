package com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate;

import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.model.SolutionDefinition;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.aggregate.ImpactAnalysisAggregate;
import com.example.mirai.projectname.changerequestservice.myteam.model.aggregate.ChangeRequestMyTeamDetailsAggregate;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import com.example.mirai.libraries.core.annotation.Aggregate;
import com.example.mirai.libraries.core.annotation.AggregateRoot;
import com.example.mirai.libraries.core.annotation.LinkTo;
import com.example.mirai.libraries.core.model.AggregateInterface;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Immutable
@Getter
@Setter
public class ChangeRequestAggregate implements AggregateInterface {
    @AggregateRoot
    private ChangeRequest description;

    @LinkTo({ChangeRequest.class})
    private Scope scope;
    @LinkTo({ChangeRequest.class})
    private SolutionDefinition solutionDefinition;
    @Aggregate
    private ImpactAnalysisAggregate impactAnalysis;
    @Aggregate
    private ChangeRequestMyTeamDetailsAggregate myTeamDetails;

}
