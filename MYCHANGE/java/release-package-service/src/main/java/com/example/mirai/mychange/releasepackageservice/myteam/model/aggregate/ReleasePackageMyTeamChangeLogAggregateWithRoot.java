package com.example.mirai.projectname.releasepackageservice.myteam.model.aggregate;

import java.util.Set;

import com.example.mirai.libraries.audit.model.ChangeLog;
import com.example.mirai.libraries.core.annotation.Aggregate;
import com.example.mirai.libraries.core.annotation.AggregateRoot;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Immutable
@Getter
@Setter
public class ReleasePackageMyTeamChangeLogAggregateWithRoot implements AggregateInterface {
    @AggregateRoot
    @EntityClass(ReleasePackageMyTeam.class)
    private ChangeLog myTeam;
    @Aggregate
    private Set<ReleasePackageMyTeamMemberChangeLogAggregate> members;
}
