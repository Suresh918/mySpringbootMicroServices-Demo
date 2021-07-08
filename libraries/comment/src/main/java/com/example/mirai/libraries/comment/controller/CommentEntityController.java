package com.example.mirai.libraries.comment.controller;

import com.example.mirai.libraries.comment.model.CommentCaseActions;
import com.example.mirai.libraries.comment.service.CommentService;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.CaseStatus;
import com.example.mirai.libraries.entity.controller.EntityController;
import com.example.mirai.libraries.entity.service.EntityResolverDefaultInterface;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping({"{parentType}/{entityType:documents}", "{parentType}/{parentId}/{entityType:documents}"})
public abstract class CommentEntityController extends EntityController {
	public CommentEntityController(ObjectMapper objectMapper,
			CommentService commentService, EntityResolverDefaultInterface entityResolver) {
		super(objectMapper, commentService, entityResolver);
	}

	CommentService getService() {
		return ((CommentService) (super.entityServiceDefaultInterface));
	}

	@PatchMapping(value = "/{id}", params = "case-action=PUBLISH")
	public CaseStatus performCaseAction(@PathVariable Long id, @RequestBody JsonNode jsonNode) {
		BaseEntityInterface comment = super.objectMapper.convertValue(jsonNode, this.getEntityClass());
		comment.setId(id);
		return getService().performCaseActionAndGetCaseStatus(CommentCaseActions.PUBLISH.name(), comment);
	}

	@PatchMapping(value = "/{id}", params = "case-action=REMOVE")
	public CaseStatus performCaseActionRemove(@PathVariable Long id) {
		return getService().performCaseActionAndGetCaseStatus(id, CommentCaseActions.REMOVE.name());
	}
}
