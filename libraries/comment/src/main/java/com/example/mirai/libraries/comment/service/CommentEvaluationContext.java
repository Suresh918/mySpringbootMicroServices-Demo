package com.example.mirai.libraries.comment.service;

import com.example.mirai.libraries.comment.model.Comment;
import com.example.mirai.libraries.comment.model.CommentStatus;
import com.example.mirai.libraries.core.model.BaseEvaluationContext;

public abstract class CommentEvaluationContext<T> extends BaseEvaluationContext<T> {
	public Integer getStatus() {
		return ((Comment) context).getStatus();
	}

	public Boolean isDrafted() {
		return ((Comment) context).getStatus().equals(CommentStatus.DRAFTED.getStatusCode());
	}

	public Boolean isPublished() {
		return ((Comment) context).getStatus().equals(CommentStatus.PUBLISHED.getStatusCode());
	}

	public Boolean isRemoved() {
		return ((Comment) context).getStatus().equals(CommentStatus.REMOVED.getStatusCode());
	}
}
