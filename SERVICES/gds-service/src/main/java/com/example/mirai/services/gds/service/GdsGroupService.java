package com.example.mirai.services.gds.service;

import java.util.ArrayList;
import java.util.List;

import com.example.mirai.libraries.core.exception.EntityIdNotFoundException;
import com.example.mirai.libraries.core.model.Group;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.services.gds.model.GdsGroup;
import com.example.mirai.services.gds.model.GdsUser;
import com.example.mirai.services.gds.repository.GdsGroupRepository;
import com.example.mirai.services.gds.repository.GdsUserRepository;
import com.example.mirai.services.gds.shared.Util;

import org.springframework.stereotype.Service;

@Service
public class GdsGroupService {
	private final GdsUserRepository gdsUserRepository;

	private final GdsGroupRepository gdsGroupRepository;

	public GdsGroupService(GdsUserRepository gdsUserRepository, GdsGroupRepository gdsGroupRepository) {
		this.gdsUserRepository = gdsUserRepository;
		this.gdsGroupRepository = gdsGroupRepository;
	}

	public List<Group> getGroupsByGroupIds(List<String> groupIds) {
		List<Group> groups = new ArrayList<>();
		groupIds.forEach(groupId -> {
			try {
				Group group = getGroupByGroupId(groupId);
				if (group != null)
					groups.add(group);
			}
			catch (EntityIdNotFoundException entityIdNotFoundException) {
				// Ignore this exception as it's only applicable for getGroupByGroupId method
			}
		});
		return groups;
	}

	public Group getGroupByGroupId(String groupId) {
		Group group;
		List<User> groupMembers = new ArrayList<>();
		GdsGroup gdsGroup = gdsGroupRepository.getGdsGroupByGroupId(groupId);
		if (gdsGroup != null) {
			group = new Group();
			group.setGroupId(gdsGroup.getGroupId());
			List<GdsUser> gdsUsers = gdsUserRepository.getGdsUsersByGroupMembershipEquals(gdsGroup.getDn());
			gdsUsers.forEach(gdsUser -> groupMembers.add(Util.convertGdsUserToUser(gdsUser)));
			group.setMembers(groupMembers);
			return group;
		}
		else
			throw new EntityIdNotFoundException();
	}

	public List<Group> findGroup(String searchQuery) {
		List<Group> groups = new ArrayList<>();
		List<GdsGroup> gdsGroups = gdsGroupRepository.findGdsGroupByGroupIdContains(searchQuery);
		if (gdsGroups != null && !gdsGroups.isEmpty()) {
			gdsGroups.forEach(gdsGroup -> groups.add(new Group(gdsGroup.getGroupId().toLowerCase(), null)));
		}
		return groups;
	}

	public List<Group> findPrefixedGroup(String groupIdPrefix, String searchQuery) {
		List<Group> groups = new ArrayList<>();
		List<GdsGroup> gdsGroups = gdsGroupRepository.findGdsGroupByGroupIdStartingWithAndGroupIdContains(groupIdPrefix, searchQuery);
		if (gdsGroups != null && !gdsGroups.isEmpty()) {
			gdsGroups.forEach(gdsGroup -> groups.add(new Group(gdsGroup.getGroupId().toLowerCase(), null)));
		}
		return groups;
	}
}
