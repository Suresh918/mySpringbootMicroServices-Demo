package com.example.mirai.projectname.releasepackageservice.core.component;

import java.util.Objects;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.StatusInterface;
import com.example.mirai.libraries.entity.service.EntityResolverDefaultInterface;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.projectname.releasepackageservice.comment.model.ReleasePackageComment;
import com.example.mirai.projectname.releasepackageservice.document.model.ReleasePackageCommentDocument;
import com.example.mirai.projectname.releasepackageservice.document.model.ReleasePackageDocument;
import com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam;
import com.example.mirai.projectname.releasepackageservice.myteam.model.aggregate.ReleasePackageMyTeamAggregate;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageStatus;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.ReleasePackageAggregate;

import org.springframework.stereotype.Component;

@Component
public class EntityResolver implements EntityResolverDefaultInterface {
    @Override
    public Class getEntityClass(String link) {
        if (Objects.nonNull(link)) {
            switch (link.toUpperCase()) {
                case "COMMENTS":
                    return ReleasePackageComment.class;
                case "RELEASE-PACKAGES":
                    return ReleasePackage.class;
                case "DOCUMENTS":
                    return ReleasePackageDocument.class;
                case "COMMENTS-DOCUMENTS":
                    return ReleasePackageCommentDocument.class;
                case "MY-TEAM":
                    return ReleasePackageMyTeam.class;
                case "MY-TEAM-MEMBERS":
                    return MyTeamMember.class;
                default:
                    return null;
            }
        }
        return null;
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass(String parentType, String entityType) {
        if (parentType.toUpperCase().equals("RELEASE-PACKAGES") && entityType.toUpperCase().equals("DOCUMENTS"))
            return ReleasePackageDocument.class;
        if (parentType.toUpperCase().equals("RELEASE-PACKAGES") && entityType.toUpperCase().equals("MY-TEAM"))
            return ReleasePackageMyTeam.class;
        if (parentType.toUpperCase().equals("COMMENTS"))
            return ReleasePackageCommentDocument.class;
        return null;
    }

    @Override
    public Class<? extends AggregateInterface> getAggregateClass(String parentType, String entityType) {
        if (parentType.toUpperCase().equals("RELEASE-PACKAGES") && entityType.toUpperCase().equals("MY-TEAM"))
            return ReleasePackageMyTeamAggregate.class;
        return null;
    }


    @Override
    public StatusInterface[] getEntityStatuses(String link) {
        if (Objects.nonNull(link)) {
            switch (link.toUpperCase()) {
                case "RELEASE-PACKAGES":
                    return ReleasePackageStatus.values();
                default:
                    return null;
            }
        }
        return null;
    }

    @Override
    public StatusInterface[] getEntityStatuses(Class entityClass) {
        if (Objects.nonNull(entityClass)) {
            if (ReleasePackage.class.equals(entityClass)) {
                return ReleasePackageStatus.values();
            }
            return null;
        }
        return null;
    }

    @Override
    public Class getCaseStatusAggregateClass(String s) {
        return null;
    }

    @Override
    public Class getAggregateClass(String link) {
        if(Objects.nonNull(link)) {
            switch (link.toUpperCase()) {
                case "RELEASE-PACKAGES":
                    return ReleasePackageAggregate.class;
                default:
                    return null;
            }
        }
        return null;
    }


}
