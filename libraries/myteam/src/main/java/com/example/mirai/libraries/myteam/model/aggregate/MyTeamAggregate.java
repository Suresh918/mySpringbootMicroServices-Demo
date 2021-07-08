package com.example.mirai.libraries.myteam.model.aggregate;

import java.util.Set;

import com.example.mirai.libraries.core.annotation.Aggregate;
import com.example.mirai.libraries.core.annotation.AggregateRoot;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.myteam.model.MyTeam;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyTeamAggregate implements AggregateInterface {
	@AggregateRoot
	private MyTeam myTeam;

	@Aggregate
	private Set<MemberAggregate> members;
}
