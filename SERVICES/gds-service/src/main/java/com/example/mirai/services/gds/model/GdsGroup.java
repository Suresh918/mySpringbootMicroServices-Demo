package com.example.mirai.services.gds.model;

import javax.naming.Name;

import lombok.Data;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

@Data
@Entry(base = "o=example", objectClasses = { "groupOfNames", "top" })
public final class GdsGroup {
	@Id
	private Name dn;

	private @Attribute(name = "cn")
	String groupId;
}
