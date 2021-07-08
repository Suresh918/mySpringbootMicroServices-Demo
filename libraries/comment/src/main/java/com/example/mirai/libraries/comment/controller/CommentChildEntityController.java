package com.example.mirai.libraries.comment.controller;

import java.util.Optional;

import com.example.mirai.libraries.comment.model.dto.CommentOverview;
import com.example.mirai.libraries.comment.service.CommentService;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.EntityLink;
import com.example.mirai.libraries.entity.controller.ChildEntityController;
import com.example.mirai.libraries.entity.service.EntityResolverDefaultInterface;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class CommentChildEntityController extends ChildEntityController {
	private final CommentService commentService;

	public CommentChildEntityController(ObjectMapper objectMapper,
			CommentService commentService, EntityResolverDefaultInterface entityResolverDefaultInterface) {
		super(objectMapper, commentService, entityResolverDefaultInterface);
		this.commentService = commentService;
	}

	@GetMapping(params = "view=overview")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public BaseEntityList<CommentOverview> getChangeRequestCommentOverview(@PathVariable String parentType,
			@PathVariable Long parentId,
			@PathVariable String entityType,
			@RequestParam(name = "criteria", defaultValue = "") String criteria,
			@RequestParam(name = "slice-select", defaultValue = "") String sliceSelect,
			@PageableDefault(value = 20) Pageable pageable) {
		Class<? extends BaseEntityInterface> parentEntityClass = this.entityResolverDefaultInterface.getEntityClass(parentType);
		EntityLink entityLink = new EntityLink<>(parentId, (Class<BaseEntityInterface>) parentEntityClass);
		return commentService.getCommentsOverviewByParent(criteria, parentEntityClass, entityLink, pageable, Optional.ofNullable(sliceSelect));

	}
}
