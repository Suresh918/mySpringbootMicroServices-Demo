package com.example.mirai.services.gds.repository;

import java.util.List;

import com.example.mirai.services.gds.model.GdsGroup;

import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GdsGroupRepository extends LdapRepository<GdsGroup> {
	GdsGroup getGdsGroupByGroupId(String groupId);

	List<GdsGroup> findGdsGroupByGroupIdContains(String groupId);

	List<GdsGroup> findGdsGroupByGroupIdStartingWithAndGroupIdContains(String groupIdPrefix, String groupId);
}
