package com.example.mirai.libraries.audit.service;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import com.example.mirai.libraries.audit.model.AuditableUpdater;
import com.example.mirai.libraries.audit.model.ChangeLog;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.AggregateType;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.core.service.AggregateBuilderInterface;
import com.example.mirai.libraries.core.service.ServiceInterface;
import com.example.mirai.libraries.util.ReflectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;

import org.springframework.core.annotation.AnnotationUtils;

public interface AuditServiceDefaultInterface {
	default Class<? extends BaseEntityInterface> getEntityType() {
		EntityClass entityClass = AnnotationUtils.findAnnotation(this.getClass(), EntityClass.class);
		return Objects.requireNonNull(entityClass).value();
	}

	default ChangeLog getChangeLog(Long id, String property) {
		Class baseEntityClass = this.getEntityType();
		EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);
		AuditReader reader = AuditReaderFactory.get(entityManager);

		AuditQuery auditQuery = reader.createQuery().forRevisionsOfEntityWithChanges(baseEntityClass, true);

		//add criteria
		auditQuery.add(AuditEntity.id().eq(id));
		auditQuery.add(AuditEntity.property(property).hasChanged());

		//add order
		auditQuery.addOrder(AuditEntity.revisionNumber().desc());

		List revisions = auditQuery.getResultList();
		ChangeLog changeLog = new ChangeLog();
		if (Objects.isNull(revisions) || revisions.isEmpty())
			return changeLog;
		Object[] initialRevision = (Object[]) revisions.get(revisions.size() - 1);
		for (Object revision : revisions) {

			Object[] properties = (Object[]) revision;
			Object entity = properties[0];
			AuditableUpdater auditableUpdater = (AuditableUpdater) properties[1];
			RevisionType revisionType = (RevisionType) properties[2];

			User updater = new User();
			updater.setUserId(auditableUpdater.getUserId());
			updater.setFullName(auditableUpdater.getFullName());
			updater.setAbbreviation(auditableUpdater.getAbbreviation());
			updater.setDepartmentName(auditableUpdater.getDepartmentName());

			Date updatedOn = new Date(auditableUpdater.getTimestamp());

			Integer revisionNumber = auditableUpdater.getId();
			Object oldValue = getOldValue(revisions, properties, property);
			if (Objects.isNull(oldValue) && !revisionType.toString().equals("ADD")) {
				oldValue = ReflectionUtil.getFieldValue(initialRevision[0], property);
			}
			Object value = ReflectionUtil.getFieldValue(entity, property);
			changeLog.addEntry(updater, updatedOn, revisionNumber, revisionType, property, value, oldValue, ((BaseEntityInterface) entity).getId());

		}
		return changeLog;
	}

	default ChangeLog getChangeLog(Long id) {
		Class baseEntityClass = this.getEntityType();
		EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);
		AuditReader reader = AuditReaderFactory.get(entityManager);
		AuditQuery auditQuery = reader.createQuery().forRevisionsOfEntityWithChanges(baseEntityClass, false);
		auditQuery.add(AuditEntity.id().eq(id));
		auditQuery.addOrder(AuditEntity.revisionNumber().desc());
		List revisions = auditQuery.getResultList();
		ChangeLog changeLog = new ChangeLog();
		Iterator iterator = revisions.iterator();
		if (Objects.isNull(revisions) || revisions.isEmpty())
			return changeLog;
		Object[] initialRevision = (Object[]) revisions.get(revisions.size() - 1);
		while (iterator.hasNext()) {
			Object[] properties = (Object[]) iterator.next();
			BaseEntityInterface entity = (BaseEntityInterface) properties[0];
			AuditableUpdater auditableUpdater = (AuditableUpdater) properties[1];
			RevisionType revisionType = (RevisionType) properties[2];
			String property = "";
			if (!(((HashSet) properties[3]).isEmpty())) {
				property = (String) ((HashSet) properties[3]).iterator().next();
			}
			User updater = new User();
			updater.setUserId(auditableUpdater.getUserId());
			updater.setFullName(auditableUpdater.getFullName());
			updater.setAbbreviation(auditableUpdater.getAbbreviation());
			updater.setDepartmentName(auditableUpdater.getDepartmentName());
			Date updatedOn = new Date(auditableUpdater.getTimestamp());
			Integer revisionNumber = auditableUpdater.getId();
			Object value = null;
			Object oldValue = null;
			if (!property.equals("")) {
				value = ReflectionUtil.getFieldValue(entity, property);
				oldValue = getOldValue(revisions, properties, property);
				if (oldValue == null) {
					oldValue = ReflectionUtil.getFieldValue(initialRevision[0], property);
				}
				changeLog.addEntry(updater, updatedOn, revisionNumber, revisionType, property, value, oldValue, entity.getId());
			}
			else if (property.length() == 0 && revisionType.toString().equals("ADD")) {
				ReflectionUtil.getAllFieldsFromClassHierarchy(entity.getClass()).forEach(field -> {
					Object fieldValue = ReflectionUtil.getFieldValue(entity, field.getName());
					List<Field> jsonIgnoredFields = ReflectionUtil.getFieldsWithAnnotation(entity.getClass(), JsonIgnore.class);
					List<String> jsonIgnoredFieldNames = jsonIgnoredFields.stream().map(item -> item.getName()).collect(Collectors.toList());
					if (!jsonIgnoredFieldNames.contains(field.getName())) {
						changeLog.addEntry(updater, updatedOn, revisionNumber, revisionType, field.getName(), fieldValue, null, entity.getId());
					}
				});
			}
		}

		return changeLog;
	}

	private Object getOldValue(List<Object> revisions, Object[] currentRevision, String property) {
		for (Integer count = revisions.indexOf(currentRevision) + 1; count < revisions.size(); count++) {
			Object[] nextRevision = (Object[]) revisions.get(count);
			boolean isPropertyValueModifiedRecord = !(((HashSet) nextRevision[3]).isEmpty());
			if (isPropertyValueModifiedRecord && property.equals(((HashSet) nextRevision[3]).iterator().next())) {
				return ReflectionUtil.getFieldValue(nextRevision[0], property);
			}
		}
		return null;
	}

	default AggregateInterface getChangeLogAggregate(long id, Class<AggregateInterface> aggregateInterfaceClass, boolean includeDeleted) {
		ServiceInterface serviceInterface = (ServiceInterface) ApplicationContextHolder.getApplicationContext().getBean(this.getClass());

		BaseEntityInterface baseEntityInterface = serviceInterface.getEntityById(id);

		AggregateInterface aggregateInterface = (AggregateInterface) ReflectionUtil.createInstance(aggregateInterfaceClass);

		AggregateBuilderInterface aggregateBuilderInterface = ApplicationContextHolder.getApplicationContext().getBean(AggregateBuilderInterface.class);
		if (Objects.isNull(aggregateBuilderInterface))
			throw new InternalAssertionException("AggregateBuilder Bean not found");

		aggregateBuilderInterface.buildAggregate(aggregateInterface, baseEntityInterface, AggregateType.CHANGELOG);

		if (includeDeleted) {
			aggregateBuilderInterface.addDeletedEntities(aggregateInterface, baseEntityInterface);
		}
		return aggregateInterface;
	}

	default List<BaseEntityInterface> getEntitiesUpdatedInDuration(Timestamp startTime, Timestamp endTime) {
		Class baseEntityClass = this.getEntityType();
		EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);
		AuditReader reader = AuditReaderFactory.get(entityManager);
		AuditQuery auditQuery = reader.createQuery().forRevisionsOfEntityWithChanges(baseEntityClass, false);
		auditQuery.add(AuditEntity.revisionProperty("timestamp").ge(startTime.getTime()));
		auditQuery.add(AuditEntity.revisionProperty("timestamp").le(endTime.getTime()));
		auditQuery.addOrder(AuditEntity.revisionNumber().desc());
		List<Object> revisions = auditQuery.getResultList();
		Iterator<Object> iterator = revisions.iterator();
		ArrayList<BaseEntityInterface> entitiesUpdated = new ArrayList<>();
		ArrayList<BaseEntityInterface> distinctObjects = new ArrayList<>();
		while (iterator.hasNext()) {
			Object[] properties = (Object[]) iterator.next();
			BaseEntityInterface entity = (BaseEntityInterface) properties[0];
			entitiesUpdated.add(entity);
		}
		for (BaseEntityInterface updatedEntity : entitiesUpdated) {
			boolean objectAdded = false;
			for (BaseEntityInterface distinctObject : distinctObjects) {
				if (distinctObject.getId() == updatedEntity.getId()) {
					objectAdded = true;
					break;
				}
			}
			if (!objectAdded) {
				distinctObjects.add(updatedEntity);
			}
		}
		return distinctObjects;
	}

	default boolean propertyHadSpecifiedValueEarlier(Long entityId, String property, Object propertyValue) {
		ChangeLog propertyChangeLog = getChangeLog(entityId, property);
		if (propertyChangeLog.getEntries().size() > 0) {
			for (ChangeLog.Entry item : propertyChangeLog.getEntries()) {
				if (Objects.nonNull(item.getOldValue()) && item.getOldValue().equals(propertyValue)) {
					return true;
				}
			}
		}
		return false;
	}
}
