package com.example.mirai.libraries.document.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
import javax.persistence.OrderColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Convert;

import com.example.mirai.libraries.core.converter.UpperCaseConverter;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.ContextInterface;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.security.abac.annotation.AbacSubject;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
public class Document<P> implements BaseEntityInterface, Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String type;

	private String description;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "document_tag", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
	@OrderColumn
	@Convert(converter = UpperCaseConverter.class)
	private List<String> tags;

	private Integer status;

	private Long size; //in kb

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

	public Document() {
		status = 1;
	}

	@Override
	public List<ContextInterface> getContextsAsContextInterface() {
		return null;
	}
}
