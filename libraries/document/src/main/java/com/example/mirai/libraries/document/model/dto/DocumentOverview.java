package com.example.mirai.libraries.document.model.dto;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.example.mirai.libraries.core.annotation.FieldName;
import com.example.mirai.libraries.core.annotation.JoinKey;
import com.example.mirai.libraries.core.annotation.ViewMapper;
import com.example.mirai.libraries.core.model.BaseView;
import com.example.mirai.libraries.core.model.User;
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
public class DocumentOverview implements BaseView {
	@Id
	@JoinKey
	private Long id;

	private Date createdOn;

	private User creator;

	@JsonIgnore
	private String creatorUserId;

	@JsonIgnore
	private String creatorFullName;

	@JsonIgnore
	private String creatorAbbreviation;

	@JsonIgnore
	private String creatorDepartmentName;

	@JsonIgnore
	private String creatorEmail;

	private String name;

	private String tags;

	private String type;

	private Long size;

	private String description;

	private Long parentId;

	private Long parentCommentId;

	@ViewMapper
	public DocumentOverview(@FieldName("id") Long id, @FieldName("createdOn") Date createdOn, @FieldName("creatorUserId") String creatorUserId,
			@FieldName("creatorFullName") String creatorFullName, @FieldName("creatorAbbreviation") String creatorAbbreviation,
			@FieldName("creatorEmail") String creatorEmail, @FieldName("creatorDepartmentName") String creatorDepartmentName,
			@FieldName("parentId") Long parentId, @FieldName("tags") String tags, @FieldName("name") String name,
			@FieldName("type") String type, @FieldName("description") String description, @FieldName("size") Long size,
			@FieldName("parentCommentId") Long parentCommentId) {
		this.id = id;
		this.createdOn = createdOn;
		this.creatorAbbreviation = creatorAbbreviation;
		this.creatorDepartmentName = creatorDepartmentName;
		this.creatorFullName = creatorFullName;
		this.creatorEmail = creatorEmail;
		this.creatorUserId = creatorUserId;
		this.parentCommentId = parentCommentId;
		this.parentId = parentId;
		this.tags = tags;
		this.type = type;
		this.name = name;
		this.size = size;
		this.description = description;
		this.creator = new User();
		this.creator.setAbbreviation(creatorAbbreviation);
		this.creator.setDepartmentName(creatorDepartmentName);
		this.creator.setFullName(creatorFullName);
		this.creator.setEmail(creatorEmail);
		this.creator.setUserId(creatorUserId);
	}
}
