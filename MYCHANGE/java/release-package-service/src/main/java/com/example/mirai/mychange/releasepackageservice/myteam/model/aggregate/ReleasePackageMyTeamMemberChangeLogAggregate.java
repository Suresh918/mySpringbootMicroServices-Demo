package com.example.mirai.projectname.releasepackageservice.myteam.model.aggregate;

import com.example.mirai.libraries.audit.model.ChangeLog;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.annotation.LinkTo;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam;

public class ReleasePackageMyTeamMemberChangeLogAggregate implements AggregateInterface {
    @LinkTo({ReleasePackageMyTeam.class})
    @EntityClass(MyTeamMember.class)
    public ChangeLog member;

}
