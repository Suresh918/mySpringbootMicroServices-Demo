package com.example.mirai.libraries.comment.controller;

import com.example.mirai.libraries.comment.service.CommentService;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.security.core.controller.SecurityController;

import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("{parentType}/{entityType:documents}")
public abstract class CommentSecurityController extends SecurityController {
	public CommentSecurityController(CommentService commentService) {
		super(commentService);
	}

	@Override
	public Class<AggregateInterface> getCaseStatusAggregateClass() {
		return null;
	}
}
