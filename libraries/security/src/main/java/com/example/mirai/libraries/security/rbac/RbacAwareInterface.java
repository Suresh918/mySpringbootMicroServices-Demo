package com.example.mirai.libraries.security.rbac;

import java.util.Set;

import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.security.abac.model.SubjectElement;

public interface RbacAwareInterface {
	String getPrincipal();

	User getAuditableUser();

	Set<SubjectElement> getSubjects();

	Set<String> getRoles();
}
