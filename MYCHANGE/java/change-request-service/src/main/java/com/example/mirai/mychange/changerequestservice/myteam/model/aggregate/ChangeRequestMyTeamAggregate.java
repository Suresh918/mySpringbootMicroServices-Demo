package com.example.mirai.projectname.changerequestservice.myteam.model.aggregate;

import com.example.mirai.projectname.changerequestservice.myteam.model.ChangeRequestMyTeam;
import com.example.mirai.libraries.core.annotation.Aggregate;
import com.example.mirai.libraries.core.annotation.AggregateRoot;
import com.example.mirai.libraries.core.model.AggregateInterface;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ChangeRequestMyTeamAggregate implements AggregateInterface {
    @AggregateRoot
    private ChangeRequestMyTeam myTeam;
    @Aggregate
    private Set<ChangeRequestMyTeamMemberAggregate> members;
}
