package com.example.mirai.libraries.myteam.model.aggregate;

import com.example.mirai.libraries.core.annotation.LinkTo;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.myteam.model.MyTeam;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberAggregate implements AggregateInterface, MemberAggregateInterface {
	@LinkTo({ MyTeam.class })
	public MyTeamMember member;
}
