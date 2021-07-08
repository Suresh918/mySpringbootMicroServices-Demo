package com.example.mirai.libraries.security.abac;

import java.util.Set;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.security.abac.model.SubjectElement;

public interface AbacAwareInterface {
	Set<SubjectElement> getSubjects(Long entityId, Class entityServiceClass);

	Set<String> getRoles(Long entityId, Class entityServiceClass, String principal);

	Set<String> getRoles(BaseEntityInterface entity, Class entityServiceClass, String principal);
}
