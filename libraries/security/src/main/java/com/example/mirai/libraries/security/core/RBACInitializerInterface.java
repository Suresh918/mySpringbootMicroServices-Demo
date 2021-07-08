package com.example.mirai.libraries.security.core;

import java.util.Set;

import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.security.abac.model.SubjectElement;

public interface RBACInitializerInterface {
	String getPrincipal();

	Set<SubjectElement> getSubjects();

	Set<String> getRoles();

	User getAuditableUser();
}
