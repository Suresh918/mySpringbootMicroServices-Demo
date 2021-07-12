package com.example.mirai.projectname.changerequestservice.myteam.model.aggregate;

import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.myteam.model.ChangeRequestMyTeam;
import com.example.mirai.libraries.core.annotation.Aggregate;
import com.example.mirai.libraries.core.annotation.LinkTo;
import com.example.mirai.libraries.core.model.AggregateInterface;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.util.Set;

@Immutable
@Getter
@Setter
public class ChangeRequestMyTeamDetailsAggregate implements AggregateInterface {
    @LinkTo({ChangeRequest.class})
    private ChangeRequestMyTeam myTeam;
    @Aggregate
    private Set<ChangeRequestMyTeamMemberAggregate> members;
}
