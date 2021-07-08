package com.example.mirai.libraries.security.core.service;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.core.model.*;
import com.example.mirai.libraries.core.service.AggregateBuilderInterface;
import com.example.mirai.libraries.core.service.ServiceInterface;
import com.example.mirai.libraries.security.abac.AbacAwareInterface;
import com.example.mirai.libraries.security.abac.model.SubjectElement;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.example.mirai.libraries.security.core.component.EntityACL;
import com.example.mirai.libraries.security.core.component.PropertyACL;
import com.example.mirai.libraries.security.model.EntityCaseActions;
import com.example.mirai.libraries.security.model.EntityCasePermissions;
import com.example.mirai.libraries.security.rbac.RbacAwareInterface;
import com.example.mirai.libraries.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public interface SecurityServiceDefaultInterface {
	AbacAwareInterface getABACAware();

	RbacAwareInterface getRBACAware();

	EntityACL getEntityACL();

	PropertyACL getPropertyACL();

	CaseActionList getCaseActionList();

	default Set<SubjectElement> getSubjects(Long id) {
		AbacAwareInterface abacAwareInterface = getABACAware();
		RbacAwareInterface rbacAwareInterface = getRBACAware();

		Set<SubjectElement> abacSubjects = null;
		if (Objects.nonNull(abacAwareInterface)) {
			abacSubjects = abacAwareInterface.getSubjects(id, getClass());
		}

		Set<SubjectElement> rbacSubjects = null;
		if (Objects.nonNull(rbacAwareInterface)) {
			rbacSubjects = rbacAwareInterface.getSubjects();
		}
		return SubjectElement.merge(abacSubjects, rbacSubjects);
	}

	default Set<String> getRoles(String principal) {
		HashSet<String> roles = new HashSet<>();
		RbacAwareInterface rbacAwareInterface = getRBACAware();
		if (Objects.nonNull(rbacAwareInterface)) {
			Set<String> rbacRoles = rbacAwareInterface.getRoles();
			if (Objects.nonNull(rbacRoles))
				roles.addAll(rbacRoles);
		}

		return roles;
	}

	default Set<String> getRoles(Long entityId, String principal) {
		HashSet<String> roles = new HashSet<>();
		Class entityServiceClass = getClass();

		RbacAwareInterface rbacAwareInterface = getRBACAware();
		if (Objects.nonNull(rbacAwareInterface)) {
			Set<String> rbacRoles = rbacAwareInterface.getRoles();
			if (Objects.nonNull(rbacRoles))
				roles.addAll(rbacRoles);
		}

		AbacAwareInterface abacAwareInterface = getABACAware();
		if (Objects.nonNull(abacAwareInterface)) {
			Set<String> abacRoles = abacAwareInterface.getRoles(entityId, entityServiceClass, principal);
			if (Objects.nonNull(abacRoles)) {
				roles.addAll(abacRoles);
			}
		}

		return roles;
	}

	default Set<String> getRoles(BaseEntityInterface entity, String principal) {
		HashSet<String> roles = new HashSet<>();
		Class entityServiceClass = getClass();

		RbacAwareInterface rbacAwareInterface = getRBACAware();
		if (Objects.nonNull(rbacAwareInterface)) {
			Set<String> rbacRoles = rbacAwareInterface.getRoles();
			if (Objects.nonNull(rbacRoles))
				roles.addAll(rbacRoles);
		}

		AbacAwareInterface abacAwareInterface = getABACAware();
		if (Objects.nonNull(abacAwareInterface)) {
			Set<String> abacRoles = abacAwareInterface.getRoles(entity, entityServiceClass, principal);
			if (Objects.nonNull(abacRoles))
				roles.addAll(abacRoles);
		}

		return roles;
	}

	default String getPrincipal() {
		RbacAwareInterface rbacAwareInterface = getRBACAware();
		if (Objects.nonNull(rbacAwareInterface)) {
			return rbacAwareInterface.getPrincipal();
		}
		return null;
	}

	default User getAuditor() {
		RbacAwareInterface rbacAwareInterface = getRBACAware();
		if (Objects.nonNull(rbacAwareInterface)) {
			return rbacAwareInterface.getAuditableUser();
		}
		return null;
	}

	default Set<CaseAction> getCaseActions(Long entityId) {
		Class serviceClass = getClass();
		ServiceInterface serviceInterface = (ServiceInterface) ApplicationContextHolder.getApplicationContext().getBean(serviceClass);
		BaseEntityInterface entity = serviceInterface.getEntityById(entityId);
		return getCaseActions(entity);
	}

	default Set<CaseAction> getCaseActions(BaseEntityInterface entity) {
		User auditor = getAuditor();
		Set<String> roles = getRoles(entity.getId(), auditor.getUserId());
		return getCaseActions(entity, auditor, roles);
	}

	default Set<CaseAction> getCaseActions(BaseEntityInterface entity, User auditor, Set<String> roles) {
		Set<CaseAction> caseActions = new HashSet<>();
		HashSet<String> authorizedCaseActions = new HashSet<>();
		Class serviceClass = getClass();
		ServiceInterface serviceInterface = (ServiceInterface) ApplicationContextHolder.getApplicationContext().getBean(serviceClass);
		CaseActionList caseActionList = getCaseActionList();
		EntityACL entityACL = getEntityACL();
		Logger log = LoggerFactory.getLogger(ServiceInterface.class);
		if (Objects.nonNull(caseActionList) && Objects.nonNull(entityACL)) {
			caseActions.addAll(caseActionList.getCaseActions(serviceInterface.getEntityClass()));
			// BaseEntityInterface entity = serviceInterface.getEntityById(entityId);
			Set<String> tempAuthorizedCaseActions = entityACL.getAuthorizedCaseActions(roles, entity, auditor);
			log.info("User  " + auditor.getUserId() + " entity " + entity.generateObjectId() + " AuthorizedCaseActions" + tempAuthorizedCaseActions + " roles " + roles);
			if (Objects.nonNull(tempAuthorizedCaseActions))
				authorizedCaseActions.addAll(tempAuthorizedCaseActions);
			caseActions.stream().forEach(caseAction -> caseAction.setIsAllowed(authorizedCaseActions.contains(caseAction.getCaseAction())));
		}
		else if (Objects.nonNull(caseActionList) && Objects.isNull(entityACL)) {
			caseActions.addAll(caseActionList.getCaseActions(serviceInterface.getEntityClass()));
		}
		return caseActions;
	}

	default Set<CaseAction> getAllCaseActions(Class entityClass) {
		CaseActionList caseActionList = getCaseActionList();
		return caseActionList.getCaseActions(entityClass);
	}

	default Set<CaseAction> getCaseActions(Class entityClass) {
		Set<CaseAction> caseActions = new HashSet<>();
		HashSet<String> authorizedCaseActions = new HashSet<>();
		Class serviceClass = getClass();
		ServiceInterface serviceInterface = (ServiceInterface) ApplicationContextHolder.getApplicationContext().getBean(serviceClass);
		CaseActionList caseActionList = getCaseActionList();
		EntityACL entityACL = getEntityACL();

		if (Objects.nonNull(caseActionList) && Objects.nonNull(entityACL)) {
			String principal = getPrincipal();

			Set<String> roles = getRoles(principal);
			Set<String> tempAuthorizedCaseActions = entityACL.getAuthorizedCaseActions(roles, entityClass);
			caseActions.addAll(caseActionList.getCaseActions(serviceInterface.getEntityClass()));
			if (Objects.nonNull(tempAuthorizedCaseActions))
				authorizedCaseActions.addAll(tempAuthorizedCaseActions);

			caseActions.stream().forEach(caseAction -> caseAction.setIsAllowed(authorizedCaseActions.contains(caseAction.getCaseAction())));
		}
		else if (Objects.nonNull(caseActionList) && Objects.isNull(entityACL)) {
			caseActions.addAll(caseActionList.getCaseActions(entityClass));
		}
		return caseActions;
	}

	default Set<String> getFetchRulesByAuthorizedCaseAction(Class entityClass, String caseAction) {
		Set<CaseAction> caseActions = null;
		HashSet<String> fetchRules = new HashSet<>();
		CaseActionList caseActionList = getCaseActionList();
		EntityACL entityACL = getEntityACL();

		if (Objects.nonNull(caseActionList) && Objects.nonNull(entityACL)) {
			String principal = getPrincipal();
			//get all roles of principal
			Set<String> roles = getRoles(principal);
			Set<String> tempFiltersForCaseAction = entityACL.getFetchRulesByAuthorizedCaseAction(roles, entityClass, caseAction);
			if (Objects.nonNull(tempFiltersForCaseAction))
				fetchRules.addAll(tempFiltersForCaseAction);

		}
		else if (Objects.nonNull(caseActionList) && Objects.isNull(entityACL)) {
			caseActions.addAll(caseActionList.getCaseActions(entityClass));
		}
		return fetchRules;
	}

	default Set<String> getFetchViewRulesByAuthorizedCaseAction(Class entityClass, String caseAction) {
		Set<CaseAction> caseActions = null;
		HashSet<String> fetchViewRules = new HashSet<>();
		CaseActionList caseActionList = getCaseActionList();
		EntityACL entityACL = getEntityACL();

		if (Objects.nonNull(caseActionList) && Objects.nonNull(entityACL)) {
			String principal = getPrincipal();
			//get all roles of principal
			Set<String> roles = getRoles(principal);
			Set<String> tempFiltersForCaseAction = entityACL.getFetchViewRulesByAuthorizedCaseAction(roles, entityClass, caseAction);
			if (Objects.nonNull(tempFiltersForCaseAction))
				fetchViewRules.addAll(tempFiltersForCaseAction);

		}
		else if (Objects.nonNull(caseActionList) && Objects.isNull(entityACL)) {
			caseActions.addAll(caseActionList.getCaseActions(entityClass));
		}
		return fetchViewRules;
	}

	default CaseProperty getCaseProperties(Long entityId) {
		Class entityServiceClass = getClass();
		ServiceInterface serviceInterface = (ServiceInterface) ApplicationContextHolder.getApplicationContext().getBean(entityServiceClass);
		BaseEntityInterface entity = serviceInterface.getEntityById(entityId);
		return getCaseProperties(entity);
	}

	default CaseProperty getCaseProperties(BaseEntityInterface entity) {
		User auditor = getAuditor();
		Set<String> roles = getRoles(entity, auditor.getUserId());
		return getCaseProperties(entity, roles);
	}

	default CaseProperty getCaseProperties(BaseEntityInterface entity, Set<String> roles) {
		PropertyACL propertyACL = getPropertyACL();
		CaseProperty caseProperties = new CaseProperty();
		if (Objects.isNull(roles))
			return null;
		try {
			caseProperties = propertyACL.getAllPropertiesRegExps(roles, entity);
		}
		catch (RuntimeException runtimeException) {
			runtimeException.printStackTrace();
		}
		return caseProperties;
	}

	//
	default HashSet<String> getPropertiesRegexps(Long entityId, String propertyType) {
		Class entityServiceClass = getClass();
		ServiceInterface serviceInterface = (ServiceInterface) ApplicationContextHolder.getApplicationContext().getBean(entityServiceClass);
		return getPropertiesRegexps(serviceInterface.getEntityById(entityId), propertyType);
	}

	default HashSet<String> getPropertiesRegexps(BaseEntityInterface entity, String propertyType) {
		HashSet<String> propertiesRegexps = new HashSet<>();
		String principal = getPrincipal();
		PropertyACL propertyACL = getPropertyACL();
		Set<String> roles = getRoles(entity, principal);
		if (Objects.isNull(roles))
			return null;
		propertyType = propertyType == null ? "" : propertyType;

		switch (propertyType.toUpperCase()) {
			case "UNREADABLE":
				Set<String> unreadablePropertiesRegexp = propertyACL.getUnreadablePropertiesRegexps(roles, entity);
				propertiesRegexps.addAll(unreadablePropertiesRegexp);
				break;
			case "UNUPDATEABLE":
				Set<String> unupdateablePropertiesRegexps = propertyACL.getUnUpdatablePropertiesRegexps(roles, entity);
				propertiesRegexps.addAll(unupdateablePropertiesRegexps);
				break;
			case "READABLE":
				Set<String> readablePropertiesRegexp = propertyACL.getReadablePropertiesRegexps(roles, entity);
				propertiesRegexps.addAll(readablePropertiesRegexp);
				break;
			case "UPDATEABLE":
				Set<String> updateablePropertiesRegexps = propertyACL.getUpdatablePropertiesRegexps(roles, entity);
				propertiesRegexps.addAll(updateablePropertiesRegexps);
				break;
			default:
				break;
		}
		return propertiesRegexps;
	}

	default CasePermissions getCasePermissions(BaseEntityInterface entity) {
		CompletableFuture[] cfs = new CompletableFuture[2];
		CasePermissions casePermissions = new CasePermissions();
		User auditor = getAuditor();
		Set<String> roles = getRoles(entity.getId(), auditor.getUserId());
		CompletableFuture<Void> caseActionsFuture = CompletableFuture.runAsync(() -> {
			Set<CaseAction> caseActions = getCaseActions(entity, auditor, roles);
			casePermissions.setCaseActions(caseActions);
		});
		CompletableFuture<Void> casePropertiesFuture = CompletableFuture.runAsync(() -> {
			CaseProperty caseProperties = getCaseProperties(entity, roles);
			casePermissions.setCaseProperties(caseProperties);
		});
		cfs[0] = caseActionsFuture;
		cfs[1] = casePropertiesFuture;
		CompletableFuture.allOf(cfs).join();
		return casePermissions;
	}


	default CasePermissions getCasePermissions(Long entityId) {
		Class serviceClass = getClass();
		ServiceInterface serviceInterface = (ServiceInterface) ApplicationContextHolder.getApplicationContext().getBean(serviceClass);
		BaseEntityInterface entity = serviceInterface.getEntityById(entityId);
		return getCasePermissions(entity);
	}

	default CaseStatus getCaseStatus(BaseEntityInterface entity) {
		CaseStatus caseStatus = new CaseStatus();
		caseStatus.setId(entity.getId());
		caseStatus.setStatus(entity.getStatus());

		caseStatus.setCasePermissions(new CasePermissions(getCaseActions(entity),
				getCaseProperties(entity)));
		return caseStatus;
	}

	default AggregateInterface getCaseStatusAggregate(long id, Class<AggregateInterface> aggregateInterfaceClass) {
		ServiceInterface serviceInterface = (ServiceInterface) ApplicationContextHolder.getApplicationContext().getBean(this.getClass());

		BaseEntityInterface baseEntityInterface = serviceInterface.getEntityById(id);

		AggregateInterface aggregateInterface = (AggregateInterface) ReflectionUtil.createInstance(aggregateInterfaceClass);

		AggregateBuilderInterface aggregateBuilderInterface = ApplicationContextHolder.getApplicationContext().getBean(AggregateBuilderInterface.class);
		if (Objects.isNull(aggregateBuilderInterface))
			throw new InternalAssertionException("AggregateBuilder Bean not found");

		aggregateBuilderInterface.buildAggregate(aggregateInterface, baseEntityInterface, AggregateType.CASE_ACTIONS);

		return aggregateInterface;

	}

	default List<EntityCaseActions> getCaseActionsByContextIds(List<Long> contextIds, String contextType) {
		List<EntityCaseActions> caseActionsList = new ArrayList<>();
		List<Long> entityIds = getEntityIdsByContextIds(contextIds, contextType);
		entityIds.forEach(entityId -> caseActionsList.add(new EntityCaseActions(entityId, getCaseActions(entityId))));
		return caseActionsList;
	}

	default List<EntityCasePermissions> getCasePermissionsByContextIds(List<Long> contextIds, String contextType) {
		List<EntityCasePermissions> caseActionsList = new ArrayList<>();
		List<Long> entityIds = getEntityIdsByContextIds(contextIds, contextType);
		entityIds.forEach(entityId -> caseActionsList.add(new EntityCasePermissions(entityId, getCasePermissions(entityId))));
		return caseActionsList;
	}

	default List<Long> getEntityIdsByContextIds(List<Long> contextIds, String contextType) {
		List<Long> entityIds = new ArrayList<>();
		Class entityServiceClass = getClass();
		contextIds.forEach(contextId -> {
			Slice idSlice = ApplicationContextHolder.getService(entityServiceClass).filterIds("contexts.contextId:" + contextId + " and contexts.type:" + contextType, PageRequest.of(0, Integer.MAX_VALUE - 1));
			entityIds.addAll((List<Long>) idSlice.getContent().stream().map(id -> ReflectionUtil.getValueFromObject(id, "value")).collect(Collectors.toList()));
		});
		return entityIds;
	}

}
