package com.example.mirai.libraries.audit.controller;

import com.example.mirai.libraries.audit.model.ChangeLog;
import com.example.mirai.libraries.audit.service.AuditServiceDefaultInterface;
import com.example.mirai.libraries.core.model.AggregateInterface;
import lombok.Data;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public abstract class AuditController {
	private final AuditServiceDefaultInterface auditServiceDefaultInterface;

	public abstract Class<? extends AggregateInterface> getChangeLogAggregateClass();

	@GetMapping(value = "/{id}/change-log", params = "property")
	public ChangeLog getChangeLog(@PathVariable Long id, String property) {
		return auditServiceDefaultInterface.getChangeLog(id, property);
	}

	@GetMapping("/{id}/change-log")
	public ChangeLog getChangeLog(@PathVariable Long id) {
		return auditServiceDefaultInterface.getChangeLog(id);
	}

	@GetMapping(value = "/{id}/change-log", params = "view=aggregate")
	public AggregateInterface getChangeLogAggregate(@PathVariable Long id, @RequestParam(name = "include-deleted", defaultValue = "false") boolean includeDeleted) {
		return auditServiceDefaultInterface.getChangeLogAggregate(id, (Class<AggregateInterface>) getChangeLogAggregateClass(), includeDeleted);
	}
}
