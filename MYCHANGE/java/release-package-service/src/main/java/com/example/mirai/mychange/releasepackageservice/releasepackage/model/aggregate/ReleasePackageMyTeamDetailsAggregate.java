package com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate;

import java.util.Set;

import com.example.mirai.libraries.core.annotation.Aggregate;
import com.example.mirai.libraries.core.annotation.LinkTo;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam;
import com.example.mirai.projectname.releasepackageservice.myteam.model.aggregate.ReleasePackageMyTeamMemberAggregate;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Immutable
@Getter
@Setter
public class ReleasePackageMyTeamDetailsAggregate implements AggregateInterface {
    @LinkTo({ReleasePackage.class})
    private ReleasePackageMyTeam myTeam;
    @Aggregate
    private Set<ReleasePackageMyTeamMemberAggregate> members;
}
