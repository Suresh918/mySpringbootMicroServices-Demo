package com.example.mirai.services.gds.model;

import java.util.List;

import javax.naming.Name;

import lombok.Data;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

@Data
@Entry(objectClasses = { "person", "inetOrgPerson", "top" }, base = "ou=users, o=example")
public final class GdsUser {
	@Id
	private Name dn;

	private @Attribute(name = "cn")
	String userId;

	private @Attribute
	String fullName;

	private @Attribute(name = "exampleAbbreviation")
	String abbreviation;

	private @Attribute(name = "ou")
	String departmentName;

	private @Attribute(name = "mail")
	String email;

	private @Attribute
	List<Name> groupMembership;
}
