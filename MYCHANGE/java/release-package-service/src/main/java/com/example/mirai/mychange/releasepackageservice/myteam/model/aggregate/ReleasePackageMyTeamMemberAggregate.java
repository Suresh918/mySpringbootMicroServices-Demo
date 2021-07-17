package com.example.mirai.projectname.releasepackageservice.myteam.model.aggregate;

import com.example.mirai.libraries.core.annotation.LinkTo;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.libraries.myteam.model.aggregate.MemberAggregateInterface;
import com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam;

public class ReleasePackageMyTeamMemberAggregate implements AggregateInterface, MemberAggregateInterface {
    @LinkTo({ReleasePackageMyTeam.class})
    public MyTeamMember member;

    public MyTeamMember getMember() {
        return this.member;
    }

    @Override
    public void setMember(MyTeamMember myTeamMember) {
        this.member = myTeamMember;
    }
}
