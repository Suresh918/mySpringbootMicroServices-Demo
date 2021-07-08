package com.example.mirai.libraries.comment.model.dto;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.example.mirai.libraries.comment.model.CommentStatus;
import com.example.mirai.libraries.core.annotation.FieldName;
import com.example.mirai.libraries.core.annotation.JoinKey;
import com.example.mirai.libraries.core.annotation.ViewMapper;
import com.example.mirai.libraries.core.model.BaseView;
import com.example.mirai.libraries.core.model.CasePermissions;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.document.model.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Getter
@Setter
@NoArgsConstructor
public class CommentOverview implements BaseView {
	@Id
	@JoinKey
	public Long id;

	public Integer status;

	public String statusLabel;

	@JsonIgnore
	public String creatorUserId;

	@JsonIgnore
	public String creatorFullName;

	@JsonIgnore
	public String creatorEmail;

	@JsonIgnore
	public String creatorDepartmentName;

	@JsonIgnore
	public String creatorAbbreviation;

	public Date createdOn;

	@Transient
	public User creator;

	@Transient
	public List<Document> documents;

	public Integer replyCount;

	@Transient
	public CasePermissions casePermissions;

	public String commentText;

	public Long parentId;

	public Long parentCommentId;

	@ViewMapper
	public CommentOverview(@FieldName("id") Long id, @FieldName("commentText") String commentText, @FieldName("status") Integer status, @FieldName("creatorUserId") String creatorUserId,
			@FieldName("creatorFullName") String creatorFullName, @FieldName("creatorEmail") String creatorEmail, @FieldName("creatorDepartmentName") String creatorDepartmentName,
			@FieldName("creatorAbbreviation") String creatorAbbreviation, @FieldName("createdOn") Date createdOn, @FieldName("replyCount") Integer replyCount,
			@FieldName("parentCommentId") Long parentCommentId, @FieldName("parentId") Long parentId) {
		this.id = id;
		this.commentText = commentText;
		this.status = status;
		this.statusLabel = CommentStatus.getLabelByCode(status);
		this.creatorUserId = creatorUserId;
		this.creatorAbbreviation = creatorAbbreviation;
		this.creatorDepartmentName = creatorDepartmentName;
		this.creatorEmail = creatorEmail;
		this.creatorFullName = creatorFullName;
		this.creator = new User();
		this.creator.setUserId(creatorUserId);
		this.creator.setEmail(creatorEmail);
		this.creator.setDepartmentName(creatorDepartmentName);
		this.createdOn = createdOn;
		this.creator.setAbbreviation(creatorAbbreviation);
		this.creator.setFullName(creatorFullName);
		this.replyCount = replyCount;
		this.parentCommentId = parentCommentId;
		this.parentId = parentId;
	}

}
