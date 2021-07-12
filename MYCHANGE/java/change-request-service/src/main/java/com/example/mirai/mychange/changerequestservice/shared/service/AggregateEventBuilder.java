package com.example.mirai.projectname.changerequestservice.shared.service;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.libraries.myteam.model.dto.MyTeamPublishData;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestAggregate;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestService;
import com.example.mirai.projectname.changerequestservice.completebusinesscase.model.CompleteBusinessCase;
import com.example.mirai.projectname.changerequestservice.customerimpact.model.CustomerImpact;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.projectname.changerequestservice.myteam.model.ChangeRequestMyTeam;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.model.PreinstallImpact;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.model.SolutionDefinition;

import java.util.HashMap;
import java.util.Map;

public class AggregateEventBuilder extends com.example.mirai.libraries.event.AggregateEventBuilder {
    @Override
    public Object translateResponse(Object obj) {
        if (obj instanceof ImpactAnalysis) {
            return super.translateResponse(((ImpactAnalysis) obj).getChangeRequest());
        } else if (obj instanceof SolutionDefinition) {
            return super.translateResponse(((SolutionDefinition) obj).getChangeRequest());
        }  else if (obj instanceof Scope) {
            return super.translateResponse(((Scope) obj).getChangeRequest());
        } else if (obj instanceof CompleteBusinessCase) {
            ImpactAnalysis impactAnalysis = ((CompleteBusinessCase) obj).getImpactAnalysis();
            return super.translateResponse(impactAnalysis.getChangeRequest());
        } else if (obj instanceof PreinstallImpact) {
            ImpactAnalysis impactAnalysis = ((PreinstallImpact) obj).getImpactAnalysis();
            return super.translateResponse(impactAnalysis.getChangeRequest());
        } else if (obj instanceof CustomerImpact) {
            ImpactAnalysis impactAnalysis = ((CustomerImpact) obj).getImpactAnalysis();
            return super.translateResponse(impactAnalysis.getChangeRequest());
        } else if (obj instanceof MyTeamMember) {
            ChangeRequestMyTeam myTeam = (ChangeRequestMyTeam) ((MyTeamMember) obj).getMyteam();
            ChangeRequestService changeRequestService = (ChangeRequestService) ApplicationContextHolder.getService(ChangeRequestService.class);
            ChangeRequestAggregate changeRequestAggregate = changeRequestService.getAggregate(myTeam.getChangeRequest().getId());
            Map aggregateData = new HashMap<String, AggregateInterface>();
            aggregateData.put("change_request", changeRequestAggregate);
            return new MyTeamPublishData(aggregateData, (MyTeamMember) obj);
        } else if (obj instanceof ChangeRequestAggregate) {
            return obj;
        }
        return super.translateResponse(obj);
    }
}
