package com.example.mirai.libraries.security.abac.component;

import com.example.mirai.libraries.core.annotation.AclImpactedByEntities;
import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.service.ServiceInterface;
import com.example.mirai.libraries.security.abac.AbacAwareInterface;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.example.mirai.libraries.security.abac.component.helper.AbacProcessorHelper;
import com.example.mirai.libraries.security.abac.model.SubjectElement;
import com.example.mirai.libraries.security.abac.model.SubjectElementFactory;
import com.example.mirai.libraries.security.abac.model.UserRolesElement;
import com.example.mirai.libraries.util.ReflectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.lang.reflect.Field;
import java.util.*;

@Component
@CacheConfig(cacheNames = { "permissions" })
@ConditionalOnProperty(name = "mirai.libraries.security.abac.enabled", havingValue = "true", matchIfMissing = true)
public class AbacProcessor implements AbacAwareInterface {
	@Resource
	AbacProcessor self;

	AbacImpactAnalyzer abacImpactAnalyzer;

	@Autowired
	public AbacProcessor(AbacImpactAnalyzer abacImpactAnalyzer) {
		this.abacImpactAnalyzer = abacImpactAnalyzer;
	}

	public void refreshUserRolesOfImpactedEntities(BaseEntityInterface baseEntityInterface) {
		Set<BaseEntityInterface> impactedEntities = abacImpactAnalyzer.getImpactedEntities(baseEntityInterface);
		impactedEntities.forEach(impactedEntity -> self.refreshUserRoles(impactedEntity));

	}

	@CachePut(key = "#result.getId()", condition = "@securityAbacConfigSpringCacheConfiguration !=null &&  !(@securityAbacConfigSpringCacheConfiguration.getType().equalsIgnoreCase(\"NONE\"))")
	public UserRolesElement refreshUserRoles(BaseEntityInterface baseEntityInterface) {
		HashMap<Object, Boolean> visitedObjects = new HashMap<>();
		return AbacProcessorHelper.retrieveEntityPermissions(baseEntityInterface, visitedObjects);
	}

	@Cacheable(key = "#root.target.generateIdByService(#root.args[0], #root.args[1])", condition = "@securityAbacConfigSpringCacheConfiguration !=null &&  !(@securityAbacConfigSpringCacheConfiguration.getType().equalsIgnoreCase(\"NONE\"))")
	public UserRolesElement getUserRoles(Long entityId, Class entityServiceClass) {
		HashMap<Object, Boolean> visitedObjects = new HashMap<>();
		ServiceInterface service = (ServiceInterface) ApplicationContextHolder.getApplicationContext().getBean(entityServiceClass);
		BaseEntityInterface entity = service.getEntityById(entityId);
		return AbacProcessorHelper.retrieveEntityPermissions(entity, visitedObjects);
	}

	public String generateId(Long entityId, Class entityClass) {
		return UserRolesElement.generateId(entityId, entityClass);
	}

	public String generateIdByService(Long entityId, Class entityServiceClass) {
		return UserRolesElement.generateIdByService(entityId, entityServiceClass);
	}

	@Override
	public Set<SubjectElement> getSubjects(Long entityId, Class entityServiceClass) {
		UserRolesElement userRolesElement = self.getUserRoles(entityId, entityServiceClass);
		ServiceInterface service = (ServiceInterface) ApplicationContextHolder.getApplicationContext().getBean(entityServiceClass);
		BaseEntityInterface entity = service.getEntityById(entityId);
		//abac subjects are based on logged-in userId => so they are processed again
		AbacProcessorHelper.processABACSubjectFields(entity, userRolesElement);
		AbacProcessorHelper.processSimpleCollectionABACSubjectFields(entity, userRolesElement);
		//process roles of acl deriving entities
		refreshUserRolesByDerivingEntities(entity, userRolesElement);
		return SubjectElementFactory.getSubjectsFromUserElement(userRolesElement);
	}

	private void refreshUserRolesByDerivingEntities(BaseEntityInterface entity, UserRolesElement userRolesElement) {
		List<BaseEntityInterface> linkedObjects = new ArrayList<>();
		AclImpactedByEntities aclDerivingEntities = AnnotationUtils.findAnnotation(entity.getClass(), AclImpactedByEntities.class);
		Class[] aclDerivingEntityClasses = Objects.nonNull(aclDerivingEntities) ? aclDerivingEntities.value() : new Class[0];
		for (Class derivingEntityClass: aclDerivingEntityClasses) {
			List<Field> fields = ReflectionUtil.getFieldsOfType(entity.getClass(), derivingEntityClass);
			ServiceClass fieldService = (ServiceClass) derivingEntityClass.getAnnotation(ServiceClass.class);
			if (Objects.nonNull(fieldService) && Objects.nonNull(fields) && fields.size() == 1) {
				Boolean isRelatedField = Objects.nonNull(fields.get(0)) && Objects.nonNull(fields.get(0).getAnnotation(ManyToOne.class)) || Objects.nonNull(fields.get(0).getAnnotation(OneToOne.class)) || Objects.nonNull(fields.get(0).getAnnotation(OneToMany.class));
				Class fieldServiceClass = fieldService.value();
				if (Objects.nonNull(fields.get(0).getAnnotation(AbacScan.class)) && isRelatedField) {
					BaseEntityInterface fieldValue = (BaseEntityInterface) ReflectionUtil.getFieldValue(entity, fields.get(0));
					ServiceInterface service = (ServiceInterface) ApplicationContextHolder.getApplicationContext().getBean(fieldServiceClass);
					linkedObjects.add(service.getEntityById(fieldValue.getId()));
				}
			}
		}
		HashMap<Object, Boolean> visitedObjects = new HashMap<>();
		for (BaseEntityInterface linkedObject : linkedObjects) {
			UserRolesElement userRolesElementFromLinkedEntity = AbacProcessorHelper.retrieveEntityPermissions(linkedObject, visitedObjects);
			userRolesElement.merge(userRolesElementFromLinkedEntity);
		}
	}

	@Override
	public Set<String> getRoles(Long entityId, Class entityServiceClass, String principal) {
		UserRolesElement userRolesElement = self.getUserRoles(entityId, entityServiceClass);
		ServiceInterface service = (ServiceInterface) ApplicationContextHolder.getApplicationContext().getBean(entityServiceClass);
		BaseEntityInterface entity = service.getEntityById(entityId);
		//abac subjects are based on logged-in userId => so they are processed again
		AbacProcessorHelper.processABACSubjectFields(entity, userRolesElement);
		AbacProcessorHelper.processSimpleCollectionABACSubjectFields(entity, userRolesElement);
		//process roles of acl deriving entities
		refreshUserRolesByDerivingEntities(entity, userRolesElement);
		return userRolesElement.get(principal);
	}

	//TODO this is causing duplicate read, one read has already happened on the consumer of this function now one more read will happen in getRoles being called from here
	@Override
	public Set<String> getRoles(BaseEntityInterface entity, Class entityServiceClass, String principal) {
		return getRoles(entity.getId(), entityServiceClass, principal);

	}

	//@CacheEvict(key="#result.getId()")
	public void removePermissions(BaseEntityInterface object) {
		//TODO Implement Cache Evict
	}

}
