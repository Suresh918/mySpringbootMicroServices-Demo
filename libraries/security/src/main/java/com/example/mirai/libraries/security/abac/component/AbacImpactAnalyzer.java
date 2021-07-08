package com.example.mirai.libraries.security.abac.component;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.example.mirai.libraries.core.annotation.AclImpactedEntities;
import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.security.abac.annotation.AbacScan;
import com.example.mirai.libraries.util.ReflectionUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(name = "mirai.libraries.security.abac.enabled", havingValue = "true", matchIfMissing = true)
public class AbacImpactAnalyzer {
	public Set<BaseEntityInterface> getImpactedEntities(BaseEntityInterface object) {
		Set<BaseEntityInterface> impactedEntities = new LinkedHashSet<>();
		impactedEntities.add(object);
		impactedEntities.addAll(getABACScanEntitiesFromClass(object));
		impactedEntities.addAll(getABACScanEntitiesFromOneToMany(object));
		impactedEntities.addAll(getABACScanEntitiesFromOneToOne(object));
		impactedEntities.addAll(getABACScanEntitiesFromManyToOne(object));
		impactedEntities.addAll(getAclImpactedEntities(impactedEntities));
		return impactedEntities;
	}

	private Set<BaseEntityInterface> getAclImpactedEntities(Set<BaseEntityInterface> impactedEntities) {
		Set<BaseEntityInterface> aclImpactedEntities = new LinkedHashSet();
		for (BaseEntityInterface entity: impactedEntities) {
			AclImpactedEntities aclImpactedEntitiesValue = AnnotationUtils.findAnnotation(entity.getClass(), AclImpactedEntities.class);
			Class[] aclImpactedEntityClasses = Objects.nonNull(aclImpactedEntitiesValue) ? aclImpactedEntitiesValue.value() : new Class[0];
			for (Class entityClass: aclImpactedEntityClasses) {
				String aclImpactedEntityFieldName = ReflectionUtil.getFieldNameWithType(entity.getClass(), entityClass);
				if (Objects.nonNull(aclImpactedEntityFieldName) && Objects.nonNull(ReflectionUtil.getFieldValue(entity, aclImpactedEntityFieldName))) {
					aclImpactedEntities.add((BaseEntityInterface) ReflectionUtil.getFieldValue(entity, aclImpactedEntityFieldName));
				}
			}
		}
		return aclImpactedEntities;
	}

	private Set<BaseEntityInterface> getABACScanEntitiesFromClass(BaseEntityInterface object) {
		Set<BaseEntityInterface> result = new HashSet<>();
		AbacScan abacScan = object.getClass().getAnnotation(AbacScan.class);
		if (Objects.nonNull(abacScan)) {
			Class[] scannableEntityClasses = abacScan.value();
			for (Class scannableEntityClass : scannableEntityClasses) {
				ServiceClass serviceClass = AnnotationUtils.findAnnotation(scannableEntityClass, ServiceClass.class);
				Class scannableEntityServiceClass = serviceClass.value();

				String fieldNameOfEntityInScannableEntityClass = ReflectionUtil.getFieldName(scannableEntityClass, object.getClass());

				Long entityId = object.getId();
				Pageable pageable = PageRequest.of(0, 1);
				BaseEntityList scannableBaseEntityList =
						ApplicationContextHolder.getService(scannableEntityServiceClass)
								.filter("( " + fieldNameOfEntityInScannableEntityClass + ".id:" + entityId + " )", pageable);
				List<BaseEntityInterface> scannableEntityInstances = scannableBaseEntityList.getResults();
				if (Objects.isNull(scannableEntityInstances) || scannableEntityInstances.size() > 0) {
					scannableEntityInstances.forEach(scannableEntityInstance -> result.add(scannableEntityInstance));
				}
			}
		}
		return result;
	}

	private Set<BaseEntityInterface> getABACScanEntitiesFromManyToOne(BaseEntityInterface object) {
		Set<BaseEntityInterface> result = new HashSet<>();
		List<Field> fields = ReflectionUtil.getAllFieldsFromClassHierarchy(object.getClass());
		for (Field field : fields) {
			field.setAccessible(true);
			AbacScan abacScan = field.getAnnotation(AbacScan.class);
			ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
			if (abacScan != null && manyToOne != null) {
				try {
					Object obj = field.get(object);
					if (obj instanceof BaseEntityInterface) {
						result.add((BaseEntityInterface) obj);
					}
				}
				catch (IllegalAccessException e) {
					log.warn(e.getMessage());
				}
			}
		}
		return result;
	}

	private Set<BaseEntityInterface> getABACScanEntitiesFromOneToOne(BaseEntityInterface object) {
		Set<BaseEntityInterface> result = new HashSet<>();
		List<Field> fields = ReflectionUtil.getAllFieldsFromClassHierarchy(object.getClass());
		for (Field field : fields) {
			field.setAccessible(true);
			AbacScan abacScan = field.getAnnotation(AbacScan.class);
			OneToOne oneToOne = field.getAnnotation(OneToOne.class);
			if (abacScan != null && oneToOne != null) {
				try {
					Object obj = field.get(object);
					if (obj instanceof BaseEntityInterface) {
						result.add((BaseEntityInterface) obj);
					}
				}
				catch (IllegalAccessException e) {
					log.warn(e.getMessage());
				}
			}
		}
		return result;
	}

	private Set<BaseEntityInterface> getABACScanEntitiesFromOneToMany(BaseEntityInterface object) {
		Set<BaseEntityInterface> result = new HashSet<>();
		List<Field> fields = ReflectionUtil.getAllFieldsFromClassHierarchy(object.getClass());
		for (Field field : fields) {
			field.setAccessible(true);
			AbacScan abacScan = field.getAnnotation(AbacScan.class);
			OneToMany oneToMany = field.getAnnotation(OneToMany.class);
			boolean isIterable = ReflectionUtil.getFieldValue(object, field) instanceof Iterable;
			if (abacScan != null && oneToMany != null && isIterable) {
				Iterable iterable = (Iterable) ReflectionUtil.getFieldValue(object, field);
				iterable.forEach(item -> {
					if (item instanceof BaseEntityInterface)
						result.add((BaseEntityInterface) item);
				});
			}
		}
		return result;
	}
}
