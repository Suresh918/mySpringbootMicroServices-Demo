package com.example.mirai.services.gds.repository;

import java.util.List;

import javax.naming.Name;

import com.example.mirai.services.gds.model.GdsUser;

import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GdsUserRepository extends LdapRepository<GdsUser> {
	GdsUser getGdsUserByUserIdIs(String userId);

	GdsUser getGdsUserByAbbreviationIs(String abbreviation);

	List<GdsUser> findGdsUserByFullNameContainsOrAbbreviationIsOrUserIdIs(String fullName, String abbreviation, String userId);

	List<GdsUser> getGdsUsersByGroupMembershipEquals(Name groupMembership);
}
