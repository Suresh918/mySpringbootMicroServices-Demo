package com.example.mirai.libraries.myteam.model.aggregate;

import com.example.mirai.libraries.myteam.model.MyTeamMember;

public interface MemberAggregateInterface {
	MyTeamMember getMember();

	void setMember(MyTeamMember member);
}
