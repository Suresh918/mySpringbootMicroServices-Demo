package com.example.mirai.libraries.myteam.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamDetails {
	Long myTeamId;

	List<Member> allMembers;
}
