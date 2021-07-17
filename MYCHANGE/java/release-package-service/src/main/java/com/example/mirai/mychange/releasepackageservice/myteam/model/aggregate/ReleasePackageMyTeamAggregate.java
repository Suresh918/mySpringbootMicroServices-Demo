package com.example.mirai.projectname.releasepackageservice.myteam.model.aggregate;

import java.util.Set;

import com.example.mirai.libraries.core.annotation.Aggregate;
import com.example.mirai.libraries.core.annotation.AggregateRoot;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReleasePackageMyTeamAggregate implements AggregateInterface {
    @AggregateRoot
    private ReleasePackageMyTeam myTeam;
    @Aggregate
    private Set<ReleasePackageMyTeamMemberAggregate> members;
}
