package com.example.mirai.services.userservice.fixtures;

import com.example.mirai.services.userservice.favorite.model.Case;
import com.example.mirai.services.userservice.favorite.model.Favorite;
import com.example.mirai.services.userservice.preferredrole.PreferredRole;
import com.example.mirai.services.userservice.profile.model.Profile;
import com.example.mirai.services.userservice.state.State;

public class EntityPojoGenerationFactory {

	public static Favorite generateFavorite() {
		Favorite favorite = new Favorite();
		Case case1 = new Case();
		case1.setId("1");
		case1.setName("name");
		case1.setType("type");
		favorite.setCases(new Case[] { case1 });
		return favorite;
	}

	public static PreferredRole generatePreferredRole() {
		PreferredRole preferredRole = new PreferredRole();
		preferredRole.setPreferredRoles(new String[] { "role1", "role2", "role3" });
		return preferredRole;
	}


	public static Profile generateProfile(String userId) {
		Profile profile = new Profile();
		profile.setUserId(userId);
		profile.setEmail(userId + "_email@example.net");
		profile.setEmployeeNumber(userId + "_employee_number");
		profile.setFullName(userId + "_full_name");
		profile.setAbbreviation(userId + "_abbreviation");
		profile.setDepartmentNumber(userId + "_department_number");
		profile.setDepartmentName(userId + "_department_name");
		profile.setRoles(new String[] {});
		profile.setLastAccessedOn(null);
		return profile;
	}

	public static State generateState() {
		State state = new State();
		state.setState("{\"teststate\": \"XYZ\"}");
		return state;
	}
}
