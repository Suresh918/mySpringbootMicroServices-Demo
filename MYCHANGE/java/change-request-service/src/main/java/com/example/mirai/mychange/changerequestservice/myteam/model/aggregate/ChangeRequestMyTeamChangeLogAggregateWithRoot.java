package com.example.mirai.projectname.changerequestservice.myteam.model.aggregate;

import com.example.mirai.libraries.audit.model.ChangeLog;
import com.example.mirai.libraries.core.annotation.Aggregate;
import com.example.mirai.libraries.core.annotation.AggregateRoot;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.projectname.changerequestservice.myteam.model.ChangeRequestMyTeam;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.util.Set;

@Immutable
@Getter
@Setter
public class ChangeRequestMyTeamChangeLogAggregateWithRoot implements AggregateInterface {
    @AggregateRoot
    @EntityClass(ChangeRequestMyTeam.class)
    private ChangeLog myTeam;
    @Aggregate
    private Set<ChangeRequestMyTeamMemberChangeLogAggregate> members;
}
