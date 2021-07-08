package com.example.mirai.libraries.audit.model;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.example.mirai.libraries.audit.component.AuditableUserAware;
import com.example.mirai.libraries.core.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Entity
@RevisionEntity(AuditableUserAware.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "aud_updater")
public class AuditableUpdater {
	String userId;

	String fullName;

	String email;

	String departmentName;

	String abbreviation;

	@Id
	@GeneratedValue
	@RevisionNumber
	private int id;

	@RevisionTimestamp
	private long timestamp;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof User)) return false;
		User userElement = (User) o;
		return Objects.equals(getUserId(), userElement.getUserId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getUserId());
	}

	public void initializeUser(User user) {
		this.userId = user.getUserId();
		this.fullName = user.getFullName();
		this.abbreviation = user.getAbbreviation();
		this.departmentName = user.getDepartmentName();
		this.email = user.getEmail();
	}
}
