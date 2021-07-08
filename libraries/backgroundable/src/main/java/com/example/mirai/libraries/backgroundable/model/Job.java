package com.example.mirai.libraries.backgroundable.model;

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
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;

import com.example.mirai.libraries.core.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(JobId.class)
public class Job implements Serializable {
	@Id
	private String id;

	@Id
	private String name;

	private Date scheduledOn;

	private User executor;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "job_owner_groups", joinColumns = {
			@JoinColumn(name = "id", referencedColumnName = "id"),
			@JoinColumn(name = "name", referencedColumnName = "name")
	})
	@OrderColumn
	private List<String> groups;

	private Integer status;

	private Integer retryCount;

	private String parentId;

	private String parentName;

	private String title;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "type", column = @Column(name = "context_type")),
			@AttributeOverride(name = "contextId", column = @Column(name = "context_id")),
			@AttributeOverride(name = "name", column = @Column(name = "context_name")),
			@AttributeOverride(name = "status", column = @Column(name = "context_status")),
	})
	@OrderColumn
	private Context context;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "code", column = @Column(name = "error_code")),
			@AttributeOverride(name = "errorClass", column = @Column(name = "error_class")),
			@AttributeOverride(name = "description", column = @Column(name = "error_description")),
	})
	@OrderColumn
	private Error error;

	@Override
	public boolean equals(Object object) {
		if (object == this)
			return true;
		if (!(object instanceof Job))
			return false;
		Job job = (Job) object;
		return this.id.equals(job.getId()) &&
				this.getName().equals(job.getName())
				&& this.parentName.equals(job.getParentName());
	}
}
