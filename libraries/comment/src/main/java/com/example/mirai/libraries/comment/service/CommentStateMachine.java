package com.example.mirai.libraries.comment.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.example.mirai.libraries.comment.model.Comment;
import com.example.mirai.libraries.comment.model.CommentStatus;
import com.example.mirai.libraries.core.exception.CaseActionNotFoundException;
import com.example.mirai.libraries.core.exception.MandatoryFieldViolationException;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.CaseAction;
import com.example.mirai.libraries.core.model.EntityUpdate;
import com.example.mirai.libraries.document.model.Document;
import com.example.mirai.libraries.entity.service.EntityStateMachineDefaultInterface;
import com.example.mirai.libraries.security.core.component.CaseActionList;

import org.springframework.stereotype.Component;

@Component
public class CommentStateMachine implements EntityStateMachineDefaultInterface {
	private final CaseActionList caseActionList;

	public CommentStateMachine(CaseActionList caseActionList) {
		this.caseActionList = caseActionList;
	}

	public EntityUpdate remove(BaseEntityInterface entity) {
		Map<String, Object> changedAttrs = new HashMap<>();

		CaseAction caseAction = caseActionList.getCaseAction(entity.getClass(), "REMOVE");
		if (Objects.isNull(caseAction))
			throw new CaseActionNotFoundException();

		if (!EntityStateMachineDefaultInterface.super.isCaseActionPerformable(entity, caseAction))
			throw new MandatoryFieldViolationException(Document.class.getSimpleName());

		entity.setStatus(CommentStatus.valueOf("REMOVED").getStatusCode());
		((Comment) entity).setCommentText("");
		changedAttrs.put("status", CommentStatus.valueOf("REMOVED").getStatusCode());
		changedAttrs.put("comment_text", "");
		return new EntityUpdate(entity, changedAttrs);
	}

	public EntityUpdate publish(BaseEntityInterface entity) {
		Map<String, Object> changedAttrs = new HashMap<>();

		CaseAction caseAction = caseActionList.getCaseAction(entity.getClass(), "PUBLISH");
		if (Objects.isNull(caseAction))
			throw new CaseActionNotFoundException();

		if (!EntityStateMachineDefaultInterface.super.isCaseActionPerformable(entity, caseAction))
			throw new MandatoryFieldViolationException(Document.class.getSimpleName());

		entity.setStatus(CommentStatus.valueOf("PUBLISHED").getStatusCode());
		changedAttrs.put("status", CommentStatus.valueOf("PUBLISHED").getStatusCode());
		if (((Comment) entity).getCommentText() != null) {
			changedAttrs.put("comment_text", ((Comment) entity).getCommentText());
		}
		return new EntityUpdate(entity, changedAttrs);
	}
}
