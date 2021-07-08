package com.example.mirai.libraries.security.abac.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.service.ServiceInterface;
import lombok.Getter;

//TODO convert to abstract class
@Getter
public class UserRolesElement implements Serializable {
	String id;

	Long entityId;

	Class entityClass;

	private final HashMap<String, HashSet<String>> userRoles;

	public UserRolesElement(Long entityId, Class entityClass) {
		this.entityId = entityId;
		this.entityClass = entityClass;
		this.userRoles = new HashMap<>();
		this.id = generateId(entityId, entityClass);
	}

	public static String generateId(Long parentEntityId, Class parentEntityClass) {
		return "" + parentEntityId + "-" + parentEntityClass.getCanonicalName();
	}

	public static String generateIdByService(Long parentEntityId, Class parentEntityClass) {
		ServiceInterface baseServiceInterface = ApplicationContextHolder.getService(parentEntityClass);
		return "" + parentEntityId + "-" + baseServiceInterface.getEntityClass().getCanonicalName();
	}

	private static String getRolePrefix(UserRolesElement userRolesElement) {
		String name = userRolesElement.entityClass.getSimpleName();
		int lastIndexOfDot = name.lastIndexOf(".");
		if (lastIndexOfDot > -1) {
			name = name.substring(lastIndexOfDot);
		}
		return name;
	}

	public String getId() {
		return this.id;
	}

	// id can be added to roles here to distinguish the instances
	public void add(String principal, String role) {
		if (Objects.isNull(principal) || Objects.isNull(role) || principal.length() == 0 || role.length() == 0)
			return;
		HashSet<String> foundRoleSet = userRoles.get(principal);
		if (foundRoleSet == null) {
			HashSet<String> roleSet = new HashSet<>();
			roleSet.add(getRolePrefix(this) + "." + role);
			userRoles.put(principal, roleSet);
		}
		else {
			foundRoleSet.add(getRolePrefix(this) + "." + role);
		}
	}

	public void add(String principal, List<String> roles) {
		if (Objects.isNull(principal) || Objects.isNull(roles) || principal.length() == 0 || roles.isEmpty())
			return;
		roles.forEach(role -> add(principal, role));
	}

	public Set<String> get(String principal) {
		return userRoles.get(principal);
	}

	public void merge(UserRolesElement userRolesElement) {
		if (Objects.nonNull(userRolesElement)) {
			userRolesElement.userRoles.forEach((key, src) -> {
				HashSet<String> dest = userRoles.get(key);
				if (dest == null) {
					dest = new HashSet<>();
					dest.addAll(src);
					userRoles.put(key, dest);
				}
				else {
					dest.addAll(src);
				}
			});
		}
	}

	@Override
	public String toString() {
		return "[id=" + id + ", entityId=" + entityId + ", entityClass=" + entityClass + ", userRoles=" + userRoles.toString() + "]\n";
	}
}
