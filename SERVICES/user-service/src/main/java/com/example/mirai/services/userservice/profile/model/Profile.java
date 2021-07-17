package com.example.mirai.services.userservice.profile.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Profile {
	@Id
	@NotEmpty
	private String userId;

	private String email;

	@Transient
	private String employeeNumber;

	@Transient
	private String fullName;

	@Transient
	private String abbreviation;

	@Transient
	private String departmentNumber;

	@Transient
	private String departmentName;

	@Transient
	private String[] roles; //groups cs1, cs2, cs3, member

	@Transient
	private String[] memberships;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastAccessedOn;
}
