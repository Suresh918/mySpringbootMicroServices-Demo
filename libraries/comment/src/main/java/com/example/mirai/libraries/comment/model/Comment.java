package com.example.mirai.libraries.comment.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.ContextInterface;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.example.mirai.libraries.security.abac.annotation.AbacSubject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
public class Comment implements BaseEntityInterface, Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer status;

	@Column(columnDefinition = "TEXT")
	private String commentText;

	@CreatedBy
	@Embedded
	@AbacSubject(role = "Static:creator", principal = "userId")
	@AttributeOverrides({
			@AttributeOverride(name = "userId", column = @Column(name = "creator_user_id")),
			@AttributeOverride(name = "fullName", column = @Column(name = "creator_full_name")),
			@AttributeOverride(name = "email", column = @Column(name = "creator_email")),
			@AttributeOverride(name = "departmentName", column = @Column(name = "creator_department_name")),
			@AttributeOverride(name = "abbreviation", column = @Column(name = "creator_abbreviation"))
	})
	private User creator;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "replyto_id")
	@AbacScan
	private Comment replyTo;

	public Comment() {
		status = CommentStatus.valueOf("DRAFTED").getStatusCode();
	}

	@Override
	public List<ContextInterface> getContextsAsContextInterface() {
		return null;
	}
}
