package com.example.mirai.libraries.entity.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.CaseStatus;
import com.example.mirai.libraries.core.model.StatusInterface;
import com.example.mirai.libraries.entity.model.LinkedItems;
import com.example.mirai.libraries.entity.model.StatusCountOverview;
import com.example.mirai.libraries.entity.model.StatusOverview;
import com.example.mirai.libraries.entity.service.EntityResolverDefaultInterface;
import com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Data;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
public abstract class EntityController {
	protected final ObjectMapper objectMapper;

	protected final EntityServiceDefaultInterface entityServiceDefaultInterface;

	protected final EntityResolverDefaultInterface entityResolverDefaultInterface;

	public abstract Class<? extends BaseEntityInterface> getEntityClass();

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public BaseEntityInterface update(@PathVariable Long id, @RequestBody final JsonNode jsonNode) throws JsonProcessingException {
		Map<String, Object> newInsChangedAttrs = ObjectMapperUtil.getChangedAttributes(jsonNode);
		BaseEntityInterface entityIns = objectMapper.treeToValue(jsonNode, getEntityClass());
		entityIns.setId(id);
		return entityServiceDefaultInterface.update(entityIns, newInsChangedAttrs);
	}

	@PatchMapping(value = "/{id}", params = "case-action")
	@ResponseStatus(HttpStatus.OK)
	public CaseStatus performCaseActionAndGetPermissions(@PathVariable Long id, @RequestParam(name = "case-action") String caseAction) {
		return entityServiceDefaultInterface.performCaseActionAndGetCaseStatus(id, caseAction);
	}

	@PatchMapping(value = "/{id}", params = { "view=case-status-aggregate", "case-action" })
	@ResponseStatus(HttpStatus.OK)
	public AggregateInterface performCaseActionAndGetCaseStatusAggregate(@PathVariable String entityType, @PathVariable Long id, @RequestParam(name = "case-action") String caseAction) {
		Class<AggregateInterface> aggregateClass = entityResolverDefaultInterface.getAggregateClass(entityType);
		return entityServiceDefaultInterface.performCaseActionAndGetCaseStatusAggregate(id, caseAction, aggregateClass);
	}

	@PatchMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public BaseEntityInterface merge(@PathVariable Long id, @RequestBody final JsonNode jsonNode) throws JsonProcessingException {
		BaseEntityInterface oldIns = objectMapper.treeToValue(jsonNode.get("oldIns"), getEntityClass());
		BaseEntityInterface newIns = objectMapper.treeToValue(jsonNode.get("newIns"), getEntityClass());
		oldIns.setId(id);
		newIns.setId(id);
		List<String> oldInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("oldIns"));
		List<String> newInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("newIns"));

		return entityServiceDefaultInterface.merge(newIns, oldIns, oldInsChangedAttributeNames, newInsChangedAttributeNames);
	}

	@PatchMapping
	public List<BaseEntityInterface> mergeMultiple(@RequestBody final ArrayNode arrayNode) {
		return entityServiceDefaultInterface.mergeMultiple(arrayNode);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable Long id) {
		entityServiceDefaultInterface.delete(id);
	}

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public BaseEntityInterface get(@PathVariable Long id) {
		return entityServiceDefaultInterface.get(id);
	}

	@GetMapping
	public BaseEntityList filter(@RequestParam(name = "criteria", defaultValue = "") String criteria,
			@PageableDefault(value = 20) Pageable pageable) {
		return entityServiceDefaultInterface.filter(criteria, pageable);
	}

	@GetMapping(value = "/{id}", params = "view=aggregate")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public AggregateInterface getAggregate(@PathVariable Long id, HttpServletRequest httpServletRequest) {
		String uri = httpServletRequest.getRequestURI();
		uri = uri.startsWith("/") ? uri.substring(1) : uri;

		String[] uriParts = uri.split("/");
		String entityType = uri.split("/")[0];
		Class aggregateClass = uriParts.length > 2 ? entityResolverDefaultInterface.getAggregateClass(entityType, uriParts[1]) : entityResolverDefaultInterface.getAggregateClass(entityType);

		return entityServiceDefaultInterface.getAggregate(id, aggregateClass);
	}

	@GetMapping(value = "/{id}", params = "view=linked-items")
	@ResponseStatus(HttpStatus.OK)
	public LinkedItems getLinkedItems(@PathVariable Long id) {
		return entityServiceDefaultInterface.getLinkedItems(id);
	}

	@GetMapping(params = "view=status-overview")
	@ResponseStatus(HttpStatus.OK)
	public StatusOverview getStatusOverview(@RequestParam(name = "criteria", defaultValue = "") String criteria,
			@RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria) {
		return entityServiceDefaultInterface.getStatusOverview(criteria, viewCriteria, entityResolverDefaultInterface.getEntityStatuses(getEntityClass()),
				java.util.Optional.empty());
	}

	@GetMapping(params = "view=status-count-overview")
	@ResponseStatus(HttpStatus.OK)
	public List<StatusCountOverview> getStatusCountOverview(@RequestParam(name = "criteria", defaultValue = "") String criteria,
			@RequestParam(name = "view-criteria", defaultValue = "") String viewCriteria,
			@PathVariable String entityType) {
		// TODO: Check last parameter
		StatusInterface[] statusInterfaces = entityResolverDefaultInterface.getEntityStatuses(entityType);
		return entityServiceDefaultInterface.getStatusCountOverview(criteria, viewCriteria, statusInterfaces, java.util.Optional.empty());
	}
}
