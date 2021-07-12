package com.example.mirai.projectname.changerequestservice.myteam.model.aggregate;

import com.example.mirai.projectname.changerequestservice.myteam.model.ChangeRequestMyTeam;
import com.example.mirai.libraries.core.annotation.LinkTo;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.libraries.myteam.model.aggregate.MemberAggregateInterface;

public class ChangeRequestMyTeamMemberAggregate implements AggregateInterface, MemberAggregateInterface {
    @LinkTo({ChangeRequestMyTeam.class})
    public MyTeamMember member;

    @Override
    public MyTeamMember getMember() {
        return this.member;
    }

    @Override
    public void setMember(MyTeamMember myTeamMember) {
        this.member = myTeamMember;
    }
}
