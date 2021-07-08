package com.example.mirai.libraries.security.rbac.component;

import java.util.Set;

import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.security.abac.model.SubjectElement;
import com.example.mirai.libraries.security.core.RBACInitializerInterface;
import com.example.mirai.libraries.security.rbac.RbacAwareInterface;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "mirai.libraries.security.rbac.enabled", havingValue = "true", matchIfMissing = true)
public class RbacProcessor implements RbacAwareInterface {
	private final RBACInitializerInterface rbacInitializerInterfaceImpl;

	RbacProcessor(RBACInitializerInterface rbacInitializerInterfaceImpl) {
		this.rbacInitializerInterfaceImpl = rbacInitializerInterfaceImpl;
	}

	@Override
	public String getPrincipal() {
		return rbacInitializerInterfaceImpl.getPrincipal();
	}

	@Override
	public User getAuditableUser() {
		return rbacInitializerInterfaceImpl.getAuditableUser();
	}

	@Override
	public Set<SubjectElement> getSubjects() {
		return rbacInitializerInterfaceImpl.getSubjects();
	}

	@Override
	public Set<String> getRoles() {
		return rbacInitializerInterfaceImpl.getRoles();
	}
}
