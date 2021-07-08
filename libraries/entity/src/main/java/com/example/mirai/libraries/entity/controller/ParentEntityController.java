package com.example.mirai.libraries.entity.controller;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.entity.service.EntityResolverDefaultInterface;
import com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
public abstract class ParentEntityController {
	protected final ObjectMapper objectMapper;

	protected final EntityServiceDefaultInterface entityServiceDefaultInterface;

	protected final EntityResolverDefaultInterface entityResolverDefaultInterface;

	public abstract Class<? extends BaseEntityInterface> getEntityClass();

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BaseEntityInterface create(@RequestBody final JsonNode jsonNode) throws JsonProcessingException {
		BaseEntityInterface entityIns = objectMapper.treeToValue(jsonNode, getEntityClass());
		entityIns.setId(null);
		return entityServiceDefaultInterface.create(entityIns);
	}
}
