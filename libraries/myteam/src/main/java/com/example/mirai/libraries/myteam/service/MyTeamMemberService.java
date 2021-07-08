package com.example.mirai.libraries.myteam.service;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.example.mirai.libraries.audit.service.AuditServiceDefaultInterface;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.CaseStatus;
import com.example.mirai.libraries.core.model.EntityLink;
import com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface;
import com.example.mirai.libraries.myteam.exception.MyTeamMemberRoleEmptyException;
import com.example.mirai.libraries.myteam.model.MyTeam;
import com.example.mirai.libraries.myteam.model.MyTeamCaseActions;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.libraries.myteam.repository.MyTeamMemberRepository;
import com.example.mirai.libraries.security.abac.AbacAwareInterface;
import com.example.mirai.libraries.security.abac.component.AbacProcessor;
import com.example.mirai.libraries.security.core.aspect.PolicyEnforcementPoint;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.example.mirai.libraries.security.core.component.EntityACL;
import com.example.mirai.libraries.security.core.component.PropertyACL;
import com.example.mirai.libraries.security.core.service.SecurityServiceDefaultInterface;
import com.example.mirai.libraries.security.rbac.RbacAwareInterface;
import com.example.mirai.libraries.security.rbac.component.RbacProcessor;
import com.example.mirai.libraries.util.ReflectionUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@EntityClass(MyTeamMember.class)
public class MyTeamMemberService implements EntityServiceDefaultInterface, SecurityServiceDefaultInterface, AuditServiceDefaultInterface {
	private final AbacProcessor abacProcessor;

	private final RbacProcessor rbacProcessor;

	private final EntityACL acl;

	private final PropertyACL pacl;

	MyTeamMemberRepository myTeamMemberRepository;

	@Resource
	private MyTeamMemberService self;

	@Autowired
	public MyTeamMemberService(AbacProcessor abacProcessor, RbacProcessor rbacProcessor,
			EntityACL acl, PropertyACL pacl,
			MyTeamMemberRepository myTeamMemberRepository) {
		this.abacProcessor = abacProcessor;
		this.rbacProcessor = rbacProcessor;
		this.acl = acl;
		this.pacl = pacl;
		this.myTeamMemberRepository = myTeamMemberRepository;
	}

	@Override
	public EntityACL getEntityACL() {
		return acl;
	}

	@Override
	public PropertyACL getPropertyACL() {
		return pacl;
	}

	@Override
	public CaseActionList getCaseActionList() {
		return null;
	}

	@Override
	public AbacAwareInterface getABACAware() {
		return abacProcessor;
	}

	@Override
	public RbacAwareInterface getRBACAware() {
		return rbacProcessor;
	}

	@Override
	public BaseEntityInterface performCaseAction(Long aLong, String s) {
		return null;
	}

	@Override
	public CaseStatus performCaseActionAndGetCaseStatus(Long aLong, String s) {
		return null;
	}

	@Override
	public AggregateInterface performCaseActionAndGetCaseStatusAggregate(Long aLong, String s, Class<AggregateInterface> aClass) {
		return null;
	}

	@Override
	public CaseStatus getCaseStatus(BaseEntityInterface baseEntityInterface) {
		return null;
	}

	public MyTeamMember getMemberByUserId(MyTeamMember myTeamMember, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
		return getMemberByUserId(myTeamMember.getUser().getUserId(), entityLinkSet);
	}

	public Long getMyTeamIdFromEntityLinkSet(Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
		List<EntityLink> myTeamLink = entityLinkSet.stream().filter((entityLink) -> entityLink.getEClass().isAssignableFrom(MyTeam.class) || entityLink.getEClass().getSuperclass().isAssignableFrom(MyTeam.class)).collect(Collectors.toList());
		if (!myTeamLink.isEmpty()) {
			return myTeamLink.get(0).getId();
		}
		throw new InternalAssertionException("My Team is not available");
	}

	public MyTeamMember getMemberByUserId(String userId, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
		Long myTeamId = getMyTeamIdFromEntityLinkSet(entityLinkSet);
		if (Objects.nonNull(myTeamId)) {
			return myTeamMemberRepository.findFirstByMyteam_IdAndUser_UserId(myTeamId, userId);
		}
		throw new InternalAssertionException("My Team is not available");
	}

	public MyTeamMember addMember(BaseEntityInterface entity, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
		if (Objects.isNull(((MyTeamMember) entity).getRoles()) || ((MyTeamMember) entity).getRoles().isEmpty()) {
			throw new MyTeamMemberRoleEmptyException();
		}
		return (MyTeamMember) self.createLinkedEntityWithLinks(entity, entityLinkSet);
	}

	@Override
	public void delete(Long id) {
		EntityServiceDefaultInterface.super.delete(id);
	}

	@Override
	@Transactional
	public MyTeamMember merge(BaseEntityInterface newInst, BaseEntityInterface oldInst, List<String> oldInsChangedAttributeNames,
			List<String> newInsChangedAttrNames) {
		MyTeamMember myTeamMember = (MyTeamMember) self.getEntityById(newInst.getId());
		Set<EntityLink<BaseEntityInterface>> entityLinkSet = new HashSet<>();
		entityLinkSet.add(new EntityLink(myTeamMember.getMyteam().getId(), MyTeam.class));
		List<String> oldRoles = (List<String>) ReflectionUtil.getValueFromObject(myTeamMember, "roles");
		List<String> newRoles = (List<String>) ReflectionUtil.getValueFromObject(newInst, "roles");
		List<String> differenceRoles = new ArrayList<>();
		BaseEntityInterface updatedEntity = null;
		differenceRoles.addAll(newRoles);
		differenceRoles.removeAll(oldRoles);
		PolicyEnforcementPoint policyEnforcementPoint = ApplicationContextHolder.getApplicationContext().getBean(PolicyEnforcementPoint.class);
		if (newRoles.size() > oldRoles.size() && newRoles.containsAll(differenceRoles)) {
			//if no roles are removed
			policyEnforcementPoint.checkForSecureLinkedEntityCaseAction(entityLinkSet, MyTeamCaseActions.ADD_ROLE_TO_MEMBER.name());
			updatedEntity = EntityServiceDefaultInterface.super.merge(newInst, oldInst, oldInsChangedAttributeNames, newInsChangedAttrNames);
		}
		else {
			policyEnforcementPoint.checkForSecureLinkedEntityCaseAction(entityLinkSet, MyTeamCaseActions.REMOVE_ROLE_FROM_MEMBER.name());
			updatedEntity = EntityServiceDefaultInterface.super.merge(newInst, oldInst, oldInsChangedAttributeNames, newInsChangedAttrNames);
		}
		return (MyTeamMember) updatedEntity;
	}

	public List<MyTeamMember> getMembersByRole(String role, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
		if (entityLinkSet.iterator().hasNext()) {
			String criteria = "roles#" + role + " and myteam.id:" + entityLinkSet.iterator().next().getId();
			BaseEntityList baseEntityList = self.filter(criteria, PageRequest.of(0, Integer.MAX_VALUE - 1));
			return baseEntityList.getResults();
		}
		return null;
	}

	public List<MyTeamMember> getMembersByRole(String role, Long myTeamId) {
		if (Objects.nonNull(myTeamId)) {
			String criteria = "roles#" + role + " and myteam.id:" + myTeamId;
			BaseEntityList baseEntityList = self.filter(criteria, PageRequest.of(0, Integer.MAX_VALUE - 1));
			return baseEntityList.getResults();
		}
		return new ArrayList<>();
	}

	public List<MyTeamMember> getAllMembersOfMyTeam(Long myTeamId) {
		if (Objects.isNull(myTeamId))
			return new ArrayList<>();
		String criteria = "myteam.id:" + myTeamId;
		BaseEntityList myTeamMemberList = self.filter(criteria, PageRequest.of(0, Integer.MAX_VALUE - 1));
		List<MyTeamMember> myTeamMembers = myTeamMemberList.getResults();
		myTeamMembers.sort(Comparator.comparing(myTeamMember -> myTeamMember.getUser().getFullName()));
		return myTeamMembers;
	}
}
