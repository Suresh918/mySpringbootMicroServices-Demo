package com.example.mirai.projectname.changerequestservice.changerequest.service;

import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeOwnerType;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestAggregate;
import com.example.mirai.projectname.changerequestservice.completebusinesscase.model.CompleteBusinessCase;
import com.example.mirai.projectname.changerequestservice.customerimpact.model.CustomerImpact;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.aggregate.ImpactAnalysisAggregate;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.aggregate.ImpactAnalysisDetailsAggregate;
import com.example.mirai.projectname.changerequestservice.myteam.model.ChangeRequestMyTeam;
import com.example.mirai.projectname.changerequestservice.myteam.model.aggregate.ChangeRequestMyTeamDetailsAggregate;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.model.PreinstallImpact;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.model.SolutionDefinition;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;

@Service
public class ChangeRequestInitializer {

    public void initiateLinkedEntities(ChangeRequestAggregate aggregate) {
        if (Objects.isNull(aggregate.getDescription()))
            aggregate.setDescription(new ChangeRequest());
        //set default change owner type
        if (Objects.isNull(aggregate.getDescription().getChangeOwnerType()))
            aggregate.getDescription().setChangeOwnerType(ChangeOwnerType.PROJECT.name());
        aggregate.setSolutionDefinition(new SolutionDefinition());
        aggregate.setScope(new Scope());
        ChangeRequestMyTeamDetailsAggregate changeRequestMyTeamDetailsAggregate = new ChangeRequestMyTeamDetailsAggregate();
        changeRequestMyTeamDetailsAggregate.setMyTeam(new ChangeRequestMyTeam());
        changeRequestMyTeamDetailsAggregate.setMembers(new HashSet<>());
        if (Objects.nonNull(aggregate.getMyTeamDetails()) && Objects.nonNull(aggregate.getMyTeamDetails().getMembers())) {
            changeRequestMyTeamDetailsAggregate.setMembers(aggregate.getMyTeamDetails().getMembers());
        }
        aggregate.setMyTeamDetails(changeRequestMyTeamDetailsAggregate);
        ImpactAnalysisAggregate impactAnalysisAggregate = new ImpactAnalysisAggregate();
        aggregate.setImpactAnalysis(impactAnalysisAggregate);
        impactAnalysisAggregate.setGeneral(new ImpactAnalysis());
        ImpactAnalysisDetailsAggregate impactAnalysisDetailsAggregate = new ImpactAnalysisDetailsAggregate();
        impactAnalysisDetailsAggregate.setCompleteBusinessCase(new CompleteBusinessCase());
        impactAnalysisDetailsAggregate.setCustomerImpact(new CustomerImpact());
        impactAnalysisDetailsAggregate.setPreinstallImpact(new PreinstallImpact());
        impactAnalysisAggregate.setDetails(impactAnalysisDetailsAggregate);
    }
}
