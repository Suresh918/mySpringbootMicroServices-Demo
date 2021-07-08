package com.example.mirai.libraries.security.core.controller;

import java.util.List;
import java.util.Set;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.CaseAction;
import com.example.mirai.libraries.core.model.CasePermissions;
import com.example.mirai.libraries.core.model.CaseProperty;
import com.example.mirai.libraries.security.abac.model.SubjectElement;
import com.example.mirai.libraries.security.core.service.SecurityServiceDefaultInterface;
import com.example.mirai.libraries.security.model.EntityCaseActions;
import com.example.mirai.libraries.security.model.EntityCasePermissions;
import lombok.Data;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public abstract class SecurityController {
	protected final SecurityServiceDefaultInterface securityServiceDefaultInterface;

	public abstract Class<? extends AggregateInterface> getCaseStatusAggregateClass();

	public abstract Class<? extends BaseEntityInterface> getEntityClass();

	@GetMapping("/{id}/subjects")
	public Set<SubjectElement> getSubjects(@PathVariable Long id) {
		return securityServiceDefaultInterface.getSubjects(id);
	}

	@GetMapping("/{id}/case-actions")
	public Set<CaseAction> getCaseActions(@PathVariable Long id) {
		return securityServiceDefaultInterface.getCaseActions(id);
	}

	@GetMapping("/case-actions")
	public Set<CaseAction> getCaseActions() {
		return securityServiceDefaultInterface.getCaseActions(getEntityClass());
	}


	@GetMapping("/case-actions/mandatory-properties")
	public Set<CaseAction> getAllCaseActions() {
		return securityServiceDefaultInterface.getAllCaseActions(getEntityClass());
	}

	@GetMapping("/{id}/case-properties")
	public CaseProperty getCaseProperties(@PathVariable Long id) {
		return securityServiceDefaultInterface.getCaseProperties(id);
	}

	@GetMapping("/{id}/case-permissions")
	public CasePermissions getCaseActionsAndCaseProperties(@PathVariable Long id) {
		return securityServiceDefaultInterface.getCasePermissions(id);
	}

	@GetMapping(value = "/{id}/case-status", params = { "view=aggregate" })
	public AggregateInterface getCaseStatusAggregate(@PathVariable Long id) {
		return securityServiceDefaultInterface.getCaseStatusAggregate(id, (Class<AggregateInterface>) getCaseStatusAggregateClass());
	}

	@GetMapping(value = "/case-actions", params = { "context-id", "context-type" })
	public List<EntityCaseActions> getCaseActionsByContext(@RequestParam(name = "context-type", defaultValue = "") String contextType,
			@RequestParam(name = "context-id", defaultValue = "") List<Long> contextIds) {
		return securityServiceDefaultInterface.getCaseActionsByContextIds(contextIds, contextType.toUpperCase());
	}

	@GetMapping(value = "/case-permissions", params = { "context-id", "context-type" })
	public List<EntityCasePermissions> getCasePermissionsByContext(@RequestParam(name = "context-type", defaultValue = "") String contextType,
			@RequestParam(name = "context-id", defaultValue = "") List<Long> contextIds) {
		return securityServiceDefaultInterface.getCasePermissionsByContextIds(contextIds, contextType.toUpperCase());
	}
}
