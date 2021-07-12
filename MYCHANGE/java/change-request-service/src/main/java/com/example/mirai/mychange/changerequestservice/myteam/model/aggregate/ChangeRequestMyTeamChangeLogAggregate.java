package com.example.mirai.projectname.changerequestservice.myteam.model.aggregate;

import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.myteam.model.ChangeRequestMyTeam;
import com.example.mirai.libraries.core.annotation.Aggregate;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.annotation.LinkTo;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.audit.model.ChangeLog;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.util.Set;

@Immutable
@Getter
@Setter
public class ChangeRequestMyTeamChangeLogAggregate implements AggregateInterface {
    @LinkTo({ChangeRequest.class})
    @EntityClass(ChangeRequestMyTeam.class)
    private ChangeLog myTeam;
    @Aggregate
    private Set<ChangeRequestMyTeamMemberChangeLogAggregate> members;
}
