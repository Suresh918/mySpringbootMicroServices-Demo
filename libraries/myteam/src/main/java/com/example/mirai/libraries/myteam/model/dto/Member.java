package com.example.mirai.libraries.myteam.model.dto;

import java.util.ArrayList;
import java.util.List;

import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Member {
	private Long id;

	private User user;

	private List<String> groups;

	private List<String> otherRoles;

	private List<String> preferredRoles;

	private List<String> roles;

	private boolean addedToMyTeam;

	public Member(User groupMember, String groupName) {
		this.user = new User(groupMember);
		this.groups = new ArrayList<>();
		this.groups.add(groupName);
		this.otherRoles = new ArrayList<>();
		this.roles = new ArrayList<>();
		this.preferredRoles = new ArrayList<>();
	}

	public Member(MyTeamMember myTeamMember) {
		this.id = myTeamMember.getId();
		this.user = myTeamMember.getUser();
		this.roles = myTeamMember.getRoles();
		this.addedToMyTeam = true;
		this.otherRoles = new ArrayList<>();
		this.preferredRoles = new ArrayList<>();
		this.groups = new ArrayList<>();
	}
}
