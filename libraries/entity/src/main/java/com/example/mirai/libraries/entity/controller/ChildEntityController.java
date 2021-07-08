package com.example.mirai.libraries.entity.controller;

import java.util.HashSet;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.EntityLink;
import com.example.mirai.libraries.entity.service.EntityResolverDefaultInterface;
import com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
public abstract class ChildEntityController {
	protected final ObjectMapper objectMapper;

	protected final EntityServiceDefaultInterface entityServiceDefaultInterface;

	protected final EntityResolverDefaultInterface entityResolverDefaultInterface;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BaseEntityInterface createChildWithLink(@RequestBody final JsonNode jsonNode, @PathVariable String parentType,
			@PathVariable String entityType,
			@PathVariable Long parentId) throws JsonProcessingException {
		Class<? extends BaseEntityInterface> childEntityClass = entityResolverDefaultInterface.getEntityClass(entityType);
		Class<? extends BaseEntityInterface> parentEntityClass = entityResolverDefaultInterface.getEntityClass(parentType);
		BaseEntityInterface entityIns = objectMapper.treeToValue(jsonNode, childEntityClass);
		HashSet<EntityLink<BaseEntityInterface>> entityLinkSet = new HashSet<>();
		entityLinkSet.add(new EntityLink(parentId, parentEntityClass));
		return entityServiceDefaultInterface.createLinkedEntityWithLinks(entityIns, entityLinkSet);
	}

	@GetMapping
	public BaseEntityList filter(@PathVariable String parentType,
			@PathVariable Long parentId,
			@PathVariable String entityType,
			@RequestParam(name = "criteria", defaultValue = "") String criteria,
			@PageableDefault(value = 20) Pageable pageable) {
		Class<? extends BaseEntityInterface> parentEntityClass = entityResolverDefaultInterface.getEntityClass(parentType);
		//getChild'EntityService
		return entityServiceDefaultInterface.getAllByParent(parentId, parentEntityClass, criteria, pageable);
	}
}
