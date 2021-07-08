package com.example.mirai.libraries.security.core.component;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.mirai.libraries.core.model.CaseAction;
import com.example.mirai.libraries.security.core.CaseActionListInitializerInterface;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "mirai.libraries.security.acl.case-action-list")
public class CaseActionList {
	private final Map<String, HashSet<CaseAction>> caseActions;

	public CaseActionList(CaseActionListInitializerInterface caseActionListInitializerInterface) {
		this.caseActions = caseActionListInitializerInterface.getCaseActionList();
	}

	public static String generateKey(Class objectClass) {
		return objectClass.getCanonicalName();
	}

	public CaseAction getCaseAction(Class entityClass, String caseActionName) {
		HashSet<CaseAction> caseActionsForEntity = caseActions.get(generateKey(entityClass));
		Optional<CaseAction> caseActionFound = caseActionsForEntity.stream().filter(caseAction -> caseAction.getCaseAction().equals(caseActionName)).findFirst();
		return caseActionFound.isPresent() ? caseActionFound.get() : null;
	}

	public List<CaseAction> getCaseActions(Class entityClass, String caseActionName) {
		HashSet<CaseAction> caseActionsForEntity = caseActions.get(generateKey(entityClass));
		List<CaseAction> caseActions = caseActionsForEntity.stream().filter(caseAction -> caseAction.getCaseAction().equals(caseActionName)).collect(Collectors.toList());
		return caseActions;
	}

	public Set<CaseAction> getCaseActions(Class entityClass) {
		Set<CaseAction> caseActionData = new HashSet<>();
		HashSet<CaseAction> caseActionSet = caseActions.get(generateKey(entityClass));
		Iterator iterator = caseActionSet.iterator();
		while (iterator.hasNext()) {
			caseActionData.add(new CaseAction((CaseAction) iterator.next()));
		}
		return caseActionData;
	}

}
