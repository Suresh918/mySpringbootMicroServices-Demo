package com.example.mirai.libraries.security.abac.component.helper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.example.mirai.libraries.security.abac.annotation.AbacSubject;
import com.example.mirai.libraries.security.abac.annotation.AbacSubjects;
import com.example.mirai.libraries.security.abac.model.UserRolesElement;
import com.example.mirai.libraries.util.DaoUtility;
import com.example.mirai.libraries.util.ReflectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.proxy.HibernateProxy;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class AbacProcessorHelper {
	public static UserRolesElement retrieveEntityPermissions(BaseEntityInterface object, HashMap<Object, Boolean> visitedObjects) {
		if (visitedObjects.get(object.generateObjectId()) != null)
			return null;
		visitedObjects.put(object.generateObjectId(), true);

		UserRolesElement userRolesElement = new UserRolesElement(object.getId(), object.getClass());
		processABACSubjectFields(object, userRolesElement);
		processSimpleCollectionABACSubjectFields(object, userRolesElement);
		processEmbeddableCollectionABACSubjectFields(object, userRolesElement);

		processABACScanForEmbeddable(object, userRolesElement);
		processABACScanForEmbeddableCollection(object, userRolesElement);

		List<BaseEntityInterface> linkedObjectsManyToOne = processManyToOneABACScanFields(object, userRolesElement, visitedObjects);
		List<BaseEntityInterface> linkedObjectsOneToMany = processOneToManyABACScanFields(object, userRolesElement, visitedObjects);
		List<BaseEntityInterface> linkedObjectsOneToOne = processOneToOneABACScanFields(object, userRolesElement, visitedObjects);

		List<BaseEntityInterface> linkedObjectsABACScan = processABACScanEntitiesFromClass(object, userRolesElement, visitedObjects);
		for (BaseEntityInterface linkedObject : linkedObjectsManyToOne) {
			UserRolesElement userRolesElementFromLinkedEntity = retrieveEntityPermissions(linkedObject, visitedObjects);
			userRolesElement.merge(userRolesElementFromLinkedEntity);
		}
		for (BaseEntityInterface linkedObject : linkedObjectsOneToMany) {
			UserRolesElement userRolesElementFromLinkedEntity = retrieveEntityPermissions(linkedObject, visitedObjects);
			userRolesElement.merge(userRolesElementFromLinkedEntity);
		}
		for (BaseEntityInterface linkedObject : linkedObjectsOneToOne) {
			UserRolesElement userRolesElementFromLinkedEntity = retrieveEntityPermissions(linkedObject, visitedObjects);
			userRolesElement.merge(userRolesElementFromLinkedEntity);
		}
		for (BaseEntityInterface linkedObject : linkedObjectsABACScan) {
			UserRolesElement userRolesElementOfLinkedEntity = retrieveEntityPermissions(linkedObject, visitedObjects);
			userRolesElement.merge(userRolesElementOfLinkedEntity);
		}
		return userRolesElement;
	}

	static UserRolesElement retrieveEmbeddedPermissions(Object object, UserRolesElement userRolesElement) {
		processABACSubjectFields(object, userRolesElement);
		processABACScanForEmbeddable(object, userRolesElement);
		return userRolesElement;
	}


	public static void processABACSubjectFields(Object object, UserRolesElement userRolesElement) {
		List<String> authorities = new ArrayList<>();
		Authentication authentication = null;
		User authenticatedPrincipal = null;
		if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
			authentication = SecurityContextHolder.getContext().getAuthentication();
			authentication.getAuthorities().forEach(grantedAuthority -> authorities.add(grantedAuthority.getAuthority()));
			authenticatedPrincipal = ((User) authentication.getPrincipal());
		}


		List<Field> fields = ReflectionUtil.getAllFieldsFromClassHierarchy(object.getClass());
		for (Field field : fields) {
			field.setAccessible(true);
			AbacSubject abacSubject = field.getAnnotation(AbacSubject.class);
			ElementCollection elementCollection = field.getAnnotation(ElementCollection.class);
			AbacSubjects abacSubjects = field.getAnnotation(AbacSubjects.class);
			ArrayList<AbacSubject> abacSubjectList = new ArrayList<>();
			if (abacSubjects != null) {
				abacSubjectList.addAll(Arrays.asList(abacSubjects.value()));
			}
			if (abacSubject != null) {
				abacSubjectList.add(abacSubject);
			}
			if (abacSubjectList.size() > 0 && elementCollection == null) {
				for (AbacSubject abacSubjectItem : abacSubjectList) {
					if (abacSubjectItem.role().startsWith("StaticGroup:") && authorities.isEmpty()) {
						String groupName = getPrincipalValue(object, abacSubjectItem.principal(), field);
						if (authorities.contains("ROLE_" + groupName) && authenticatedPrincipal != null) {
							String principalUserId = authenticatedPrincipal.getUserId();
							List<String> roles = getRolesValue(object, abacSubjectItem.role(), field);
							userRolesElement.add(principalUserId, roles);
						}
					}
					else {
						String principal = getPrincipalValue(object, abacSubjectItem.principal(), field);
						List<String> roles = getRolesValue(object, abacSubjectItem.role(), field);
						userRolesElement.add(principal, roles);
					}
				}
			}
		}
	}

/*     void processEmbeddableABACSubjectFields(Object object, UserRolesElement userRolesElement) {
        List<Field> fields = ReflectionUtil.getAllFieldsFromClassHierarchy(object.getClass());
        for (Field field : fields) {
            field.setAccessible(true);
            ABACSubject abacSubject = field.getAnnotation(ABACSubject.class);
            Embedded embedded = field.getAnnotation(Embedded.class);
            ElementCollection elementCollection = field.getAnnotation(ElementCollection.class);
            if (abacSubject != null && embedded != null && elementCollection == null) {
                String role = getRoleValue(object, abacSubject.role(), field);
                String  principal = getPrincipalValue(object, abacSubject.principal(),field);
                userRolesElement.add(principal, role);
                try {
                    processEmbeddableABACSubjectFields(field.get(object), userRolesElement);
                } catch (IllegalAccessException e) {
                    log.warn(e.getMessage());;
                    new RuntimeException("Illegal access in processEmbeddableABACSubjectFields");
                }
            }
        }
    }*/

	static void processABACScanForEmbeddable(Object object, UserRolesElement userRolesElement) {
		List<Field> fields = ReflectionUtil.getAllFieldsFromClassHierarchy(object.getClass());
		for (Field field : fields) {
			field.setAccessible(true);
			AbacScan abacScan = field.getAnnotation(AbacScan.class);
			Embedded embedded = field.getAnnotation(Embedded.class);
			ElementCollection elementCollection = field.getAnnotation(ElementCollection.class);
			if (abacScan != null && embedded != null && elementCollection == null) {
				try {
					retrieveEmbeddedPermissions(field.get(object), userRolesElement);
				}
				catch (IllegalAccessException e) {
					log.warn(e.getMessage());
					new InternalAssertionException("Illegal access in processEmbeddableABACSubjectFields");
				}
			}
		}
	}

	static void processABACScanForEmbeddableCollection(Object object, UserRolesElement userRolesElement) {
		List<Field> fields = ReflectionUtil.getAllFieldsFromClassHierarchy(object.getClass());
		for (Field field : fields) {
			field.setAccessible(true);
			AbacScan abacScan = field.getAnnotation(AbacScan.class);
			Embedded embedded = field.getAnnotation(Embedded.class);
			ElementCollection elementCollection = field.getAnnotation(ElementCollection.class);
			if (abacScan != null && embedded != null && elementCollection != null && ReflectionUtil.getFieldValue(object, field) instanceof Iterable) {
				Iterable iterable = (Iterable) ReflectionUtil.getFieldValue(object, field);
				iterable.forEach(item -> retrieveEmbeddedPermissions(item, userRolesElement));
			}
		}
	}

	public static void processSimpleCollectionABACSubjectFields(Object object, UserRolesElement userRolesElement) {
		List<Field> fields = ReflectionUtil.getAllFieldsFromClassHierarchy(object.getClass());
		List<String> authorities = new ArrayList<>();
		Authentication authentication = null;
		User authenticatedPrincipal = null;
		if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
			authentication = SecurityContextHolder.getContext().getAuthentication();
			authentication.getAuthorities().forEach(grantedAuthority -> authorities.add(grantedAuthority.getAuthority()));
			authenticatedPrincipal = ((User) authentication.getPrincipal());
		}

		for (Field field : fields) {
			field.setAccessible(true);
			AbacSubject abacSubject = field.getAnnotation(AbacSubject.class);
			AbacSubjects abacSubjects = field.getAnnotation(AbacSubjects.class);
			Embedded embedded = field.getAnnotation(Embedded.class);
			ElementCollection elementCollection = field.getAnnotation(ElementCollection.class);
			ArrayList<AbacSubject> abacSubjectList = new ArrayList<>();
			if (abacSubject != null) {
				abacSubjectList.add(abacSubject);
			}
			if (abacSubjects != null) {
				abacSubjectList.addAll(Arrays.asList(abacSubjects.value()));
			}
			if (abacSubjectList.size() > 0 && embedded == null && elementCollection != null) {
				for (AbacSubject abacSubjectItem : abacSubjectList) {
					if (abacSubjectItem.role().startsWith("StaticGroup:") && !authorities.isEmpty()) {
						List<String> groupNames = getPrincipalValues(object, abacSubjectItem.principal(), field);
						for (String groupName : groupNames) {
							if (authorities.contains("ROLE_" + groupName) && authenticatedPrincipal != null) {
								String principalUserId = authenticatedPrincipal.getUserId();
								List<String> roles = getRolesValue(object, abacSubjectItem.role(), field);
								userRolesElement.add(principalUserId, roles);
							}
						}
					}
					else {
						List<String> roles = getRolesValue(object, abacSubjectItem.role(), field);
						String principal = getPrincipalValue(object, abacSubjectItem.principal(), field);
						userRolesElement.add(principal, roles);
					}
				}
			}
		}
	}

	static void processEmbeddableCollectionABACSubjectFields(Object object, UserRolesElement userRolesElement) {
		List<Field> fields = ReflectionUtil.getAllFieldsFromClassHierarchy(object.getClass());
		String abacSubjectStaticRolePrefix = "Static:";
		String abacSubjectDynamicRolePrefix = "Dynamic:";
		for (Field field : fields) {
			field.setAccessible(true);
			AbacSubject abacSubject = field.getAnnotation(AbacSubject.class);
			AbacSubjects abacSubjects = field.getAnnotation(AbacSubjects.class);
			Embedded embedded = field.getAnnotation(Embedded.class);
			ElementCollection elementCollection = field.getAnnotation(ElementCollection.class);
			ArrayList<AbacSubject> abacSubjectList = new ArrayList<>();
			if (abacSubject != null) {
				abacSubjectList.add(abacSubject);
			}
			if (abacSubjects != null) {
				abacSubjectList.addAll(Arrays.asList(abacSubjects.value()));
			}
			if (abacSubjectList.size() > 0 && embedded != null && elementCollection != null && ReflectionUtil.getFieldValue(object, field) instanceof Iterable) {
				for (AbacSubject abacSubjectItem : abacSubjectList) {
					Iterable iterable = (Iterable) ReflectionUtil.getFieldValue(object, field);
					iterable.forEach(item -> {
						Object roleObject = null;
						boolean roleObjectIsList = false;
						String role1 = "";
						if (abacSubjectItem.role().startsWith(abacSubjectDynamicRolePrefix)) {
							roleObject = ReflectionUtil.getFieldValue(item,
									abacSubjectItem.role().substring(abacSubjectDynamicRolePrefix.length()));
							if (Iterable.class.isAssignableFrom(roleObject.getClass())) { //TODO check whether list contains strings or not
								roleObjectIsList = true;
							}
							else if (roleObject instanceof String) {
								roleObjectIsList = false;
								role1 = (String) roleObject;
							}
							else {
								log.warn("roleObject is not Iterable<String> or String");
								return;
							}

						}
						else if (abacSubjectItem.role().startsWith(abacSubjectStaticRolePrefix)) {
							role1 = abacSubjectItem.role().substring(abacSubjectStaticRolePrefix.length());
						}
						else {
							role1 = field.getName();
						}

						Object principalObject = ReflectionUtil.getFieldValue(item, abacSubjectItem.principal());
						String principalUserId = null;
						if (principalObject instanceof User) {
							principalUserId = ((User) principalObject).getUserId();
						}
						else if (principalObject instanceof String) {
							principalUserId = (String) principalObject;
						}
						else {
							log.warn("principalObject is not User or String");
							return;
						}
						if (roleObjectIsList)
							userRolesElement.add(principalUserId, (List) roleObject);
						else
							userRolesElement.add(principalUserId, role1);

						processABACSubjectFields(item, userRolesElement);
					});
				}
			}
		}
	}

	static List<BaseEntityInterface> processManyToOneABACScanFields(Object object, UserRolesElement userRolesElement, HashMap<Object, Boolean> visitedObjects) {
		List<Field> fields = ReflectionUtil.getAllFieldsFromClassHierarchy(object.getClass());
		List<BaseEntityInterface> linkedObjects = new LinkedList<>();
		for (Field field : fields) {
			field.setAccessible(true);
			AbacScan abacScan = field.getAnnotation(AbacScan.class);
			ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
			if (abacScan != null && manyToOne != null) {
				try {
					Object obj = field.get(object);
					if (obj instanceof BaseEntityInterface) {
						BaseEntityInterface linkedObject = (BaseEntityInterface) obj;
						if (linkedObject instanceof HibernateProxy) {
							linkedObject = DaoUtility.initializeAndUnproxy(linkedObject);
						}
						linkedObjects.add(linkedObject);
					}
				}
				catch (IllegalAccessException e) {
					log.warn(e.getMessage());
				}
			}
		}
		return linkedObjects;
	}

	static List<BaseEntityInterface> processOneToManyABACScanFields(Object object, UserRolesElement userRolesElement, HashMap<Object, Boolean> visitedObjects) {
		List<Field> fields = ReflectionUtil.getAllFieldsFromClassHierarchy(object.getClass());
		List<BaseEntityInterface> linkedObjects = new LinkedList<>();
		for (Field field : fields) {
			field.setAccessible(true);
			AbacScan abacScan = field.getAnnotation(AbacScan.class);
			OneToMany oneToMany = field.getAnnotation(OneToMany.class);
			if (abacScan != null && oneToMany != null && ReflectionUtil.getFieldValue(object, field) instanceof Iterable) {
				Iterable iterable = (Iterable) ReflectionUtil.getFieldValue(object, field);
				iterable.forEach(item -> {
					try {
						Object obj = field.get(object);
						if (obj instanceof BaseEntityInterface) {
							BaseEntityInterface linkedObject = (BaseEntityInterface) obj;
							linkedObject = DaoUtility.initializeAndUnproxy(linkedObject);
							linkedObjects.add(linkedObject);
						}
					}
					catch (IllegalAccessException e) {
						log.warn(e.getMessage());
					}
				});
			}
		}
		return linkedObjects;
	}

	static List<BaseEntityInterface> processOneToOneABACScanFields(Object object, UserRolesElement userRolesElement, HashMap<Object, Boolean> visitedObjects) {
		List<Field> fields = ReflectionUtil.getAllFieldsFromClassHierarchy(object.getClass());
		List<BaseEntityInterface> linkedObjects = new LinkedList<>();
		for (Field field : fields) {
			field.setAccessible(true);
			AbacScan abacScan = field.getAnnotation(AbacScan.class);
			OneToOne oneToOne = field.getAnnotation(OneToOne.class);
			if (abacScan != null && oneToOne != null && !(ReflectionUtil.getFieldValue(object, field) instanceof Iterable)) {
				try {
					Object obj = field.get(object);
					if (obj instanceof BaseEntityInterface) {
						BaseEntityInterface linkedObject = (BaseEntityInterface) obj;
						if (linkedObject instanceof HibernateProxy) {
							linkedObject = DaoUtility.initializeAndUnproxy(linkedObject);
						}
						linkedObjects.add(linkedObject);
					}
				}
				catch (IllegalAccessException e) {
					log.warn(e.getMessage());
				}
			}
		}
		return linkedObjects;
	}

	static List<BaseEntityInterface> processABACScanEntitiesFromClass(BaseEntityInterface object, UserRolesElement userRolesElement, HashMap<Object, Boolean> visitedObjects) {
		List<BaseEntityInterface> linkedObjects = new LinkedList<>();
		AbacScan abacScan = object.getClass().getAnnotation(AbacScan.class);
		if (Objects.nonNull(abacScan)) {
			Class[] scannableEntityClasses = abacScan.value();
			for (Class scannableEntityClass : scannableEntityClasses) {
				ServiceClass serviceClass = AnnotationUtils.findAnnotation(scannableEntityClass, ServiceClass.class);
				Class scannableEntityServiceClass = serviceClass.value();
				boolean isLinkedObjectVisited = visitedObjects.keySet().stream().anyMatch(key -> key.toString().startsWith(scannableEntityClass.getCanonicalName().concat("-")));
				if (isLinkedObjectVisited) {
					break;
				}
				String fieldNameOfEntityInScannableEntityClass = ReflectionUtil.getFieldName(scannableEntityClass, object.getClass());
				Long entityId = object.getId();
				Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE - 1);
				BaseEntityList scannableBaseEntityList =
						ApplicationContextHolder.getService(scannableEntityServiceClass)
								.filter("( " + fieldNameOfEntityInScannableEntityClass + ".id:" + entityId + " )", pageable);
				List<BaseEntityInterface> scannableEntityInstances = scannableBaseEntityList.getResults();
                /*List<BaseEntityInterface> scannedEntities = new ArrayList<>();
                scannableEntityInstances.forEach(item -> {
                    if (visitedObjects.get(item.generateObjectId()) != null) {
                        scannedEntities.add(item);
                    }
                });
                // this is to avoid checking all entities related to a manytoone field when the operation is performed on a field the check is not focusing the manytoone entity
                if (scannedEntities.size() > 0) {
                    scannableEntityInstances = scannedEntities;
                }*/
				if (!Objects.isNull(scannableEntityInstances) || scannableEntityInstances.size() > 0) {
					scannableEntityInstances.forEach(x -> {
						BaseEntityInterface linkedObject = DaoUtility.initializeAndUnproxy(x);
						linkedObjects.add(linkedObject);
					});
				}
			}
		}
		return linkedObjects;
	}

	static String getPrincipalValue(Object object, String principalTemp, Field entityField) {
		String principal = "";
		Object entityFieldValue = ReflectionUtil.getFieldValue(object, entityField);
		if (principalTemp.equals("[unassigned]") && entityFieldValue != null && entityFieldValue instanceof String) {
			principal = (String) entityFieldValue;
		}
		else if (entityFieldValue != null && !(entityFieldValue instanceof String)) {
			Object elementFieldValue = ReflectionUtil.getFieldValue(entityFieldValue, principalTemp);
			if (elementFieldValue instanceof String) {
				principal = (String) elementFieldValue;
			}
		}
		return principal;
	}

	static List<String> getPrincipalValues(Object object, String principalTemp, Field entityField) {
		List<String> principals = new ArrayList<>();
		Object entityFieldValue = ReflectionUtil.getFieldValue(object, entityField);
		if (principalTemp.equals("[unassigned]") && entityFieldValue != null && Collection.class.isAssignableFrom(entityFieldValue.getClass())) {
			return (List<String>) entityFieldValue;
		}
		return principals;
	}

	static List<String> getRolesValue(Object object, String roleTemp, Field entityField) {
		ArrayList<String> roles = new ArrayList<>();
		String abacSubjectStaticRolePrefix = "Static:";
		String abacSubjectDynamicRolePrefix = "Dynamic:";
		String abacSubjectUnassigned = "[unassigned]";
		String abacSubjectStaticGroup = "StaticGroup:";
		if (roleTemp.indexOf(abacSubjectUnassigned) == 0) {
			roles.add(entityField.getName());
		}
		else if (roleTemp.indexOf(abacSubjectStaticRolePrefix) == 0) {
			roles.add(roleTemp.substring(abacSubjectStaticRolePrefix.length()));
		}
		else if (roleTemp.indexOf(abacSubjectStaticGroup) == 0) {
			roles.add(roleTemp.substring(abacSubjectStaticGroup.length()));
		}
		else if (roleTemp.indexOf(abacSubjectDynamicRolePrefix) == 0) {
			String elementFieldName = roleTemp.substring(abacSubjectDynamicRolePrefix.length());
			Object entityFieldValue = ReflectionUtil.getFieldValue(object, entityField);
			if (entityFieldValue != null && !(entityFieldValue instanceof String)) {
				Object elementFieldValue = ReflectionUtil.getFieldValue(entityFieldValue, elementFieldName);
				if (elementFieldValue instanceof String) {
					roles.add((String) elementFieldValue);
				}
			}
		}
		else if (roleTemp.indexOf("ValueOfProperty:") == 0) {
			String elementFieldName = roleTemp.substring("ValueOfProperty:".length());
			Object entityFieldValue = ReflectionUtil.getFieldValue(object, elementFieldName);
			if (entityFieldValue != null && Iterable.class.isAssignableFrom(entityFieldValue.getClass())) {
				Iterable iterable = (Iterable) entityFieldValue;
				iterable.forEach(item -> roles.add((String) item));
			}
			else if (entityFieldValue instanceof String) {
				roles.add((String) entityFieldValue);
			}
		}
		return roles;
	}

}
