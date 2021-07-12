package com.example.mirai.projectname.changerequestservice.myteam.model.aggregate;

import com.example.mirai.projectname.changerequestservice.myteam.model.ChangeRequestMyTeam;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.annotation.LinkTo;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.audit.model.ChangeLog;
import com.example.mirai.libraries.myteam.model.MyTeamMember;

public class ChangeRequestMyTeamMemberChangeLogAggregate implements AggregateInterface {
    @LinkTo({ChangeRequestMyTeam.class})
    @EntityClass(MyTeamMember.class)
    public ChangeLog member;

}
