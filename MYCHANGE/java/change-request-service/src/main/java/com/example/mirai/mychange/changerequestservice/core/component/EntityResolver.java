package com.example.mirai.projectname.changerequestservice.core.component;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.StatusInterface;
import com.example.mirai.libraries.entity.service.EntityResolverDefaultInterface;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestStatus;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestAggregate;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestCaseStatusAggregate;
import com.example.mirai.projectname.changerequestservice.comment.model.ChangeRequestComment;
import com.example.mirai.projectname.changerequestservice.completebusinesscase.model.CompleteBusinessCase;
import com.example.mirai.projectname.changerequestservice.customerimpact.model.CustomerImpact;
import com.example.mirai.projectname.changerequestservice.document.model.ChangeRequestCommentDocument;
import com.example.mirai.projectname.changerequestservice.document.model.ChangeRequestDocument;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.projectname.changerequestservice.myteam.model.ChangeRequestMyTeam;
import com.example.mirai.projectname.changerequestservice.myteam.model.aggregate.ChangeRequestMyTeamAggregate;
import com.example.mirai.projectname.changerequestservice.preinstallimpact.model.PreinstallImpact;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import com.example.mirai.projectname.changerequestservice.solutiondefinition.model.SolutionDefinition;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class EntityResolver implements EntityResolverDefaultInterface {
    @Override
    public Class getEntityClass(String link) {
        if(Objects.nonNull(link)) {
            switch (link.toUpperCase()) {
                case "CHANGE-REQUESTS":
                    return ChangeRequest.class;
                case "COMMENTS":
                    return ChangeRequestComment.class;
                case "DOCUMENTS":
                    return ChangeRequestCommentDocument.class;
                case "MY-TEAM":
                    return ChangeRequestMyTeam.class;
                case "MY-TEAM-MEMBERS":
                    return MyTeamMember.class;
                case "SCOPE":
                    return Scope.class;
                case "SOLUTION-DEFINITION":
                    return SolutionDefinition.class;
                case "COMPLETE-BUSINESS-CASE":
                    return CompleteBusinessCase.class;
                case "CUSTOMER-IMPACT":
                    return CustomerImpact.class;
                case "IMPACT-ANALYSIS":
                    return ImpactAnalysis.class;
                case "PRE-INSTALL-IMPACT":
                    return PreinstallImpact.class;
                default:
                    return null;
            }
        }
        return null;
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass(String parentType, String entityType) {
        if (parentType.toUpperCase().equals("CHANGE-REQUESTS") && entityType.toUpperCase().equals("DOCUMENTS"))
            return ChangeRequestDocument.class;
        if (parentType.toUpperCase().equals("COMMENTS") && entityType.toUpperCase().equals("DOCUMENTS"))
            return ChangeRequestCommentDocument.class;
        if (parentType.toUpperCase().equals("CHANGE-REQUESTS") && entityType.toUpperCase().equals("MY-TEAM"))
            return ChangeRequestMyTeam.class;
        return null;
    }

    @Override
    public Class<? extends AggregateInterface> getAggregateClass(String parentType, String entityType) {
        if (parentType.toUpperCase().equals("CHANGE-REQUESTS") && entityType.toUpperCase().equals("MY-TEAM"))
            return ChangeRequestMyTeamAggregate.class;
        return null;
    }


    public Class getCaseStatusAggregateClass(String link) {
        if(Objects.nonNull(link)) {
            switch (link.toUpperCase()) {
                case "CHANGE-REQUESTS":
                    return ChangeRequestCaseStatusAggregate.class;
                default:
                    return null;
            }
        }
        return null;
    }
    public Class getAggregateClass(String link) {
        if(Objects.nonNull(link)) {
            switch (link.toUpperCase()) {
                case "CHANGE-REQUESTS":
                    return ChangeRequestAggregate.class;
                case "MY-TEAM":
                    return ChangeRequestMyTeamAggregate.class;
                default:
                    return null;
            }
        }
        return null;
    }
    @Override
    public StatusInterface[] getEntityStatuses(String link) {
        if(Objects.nonNull(link)) {
            switch (link.toUpperCase()) {
                case "CHANGE-REQUESTS":
                    return ChangeRequestStatus.values();
                default:
                    return null;
            }
        }
        return null;
    }

    @Override
    public StatusInterface[] getEntityStatuses(Class entityClass) {
        if(Objects.nonNull(entityClass)) {
            if (ChangeRequest.class.equals(entityClass)) {
                return ChangeRequestStatus.values();
            }
            return null;
        }
        return null;
    }

}
