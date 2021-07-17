package com.example.mirai.projectname.releasepackageservice.core.component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.example.mirai.libraries.audit.model.AuditableUpdater;
import com.example.mirai.libraries.audit.model.ChangeLog;
import com.example.mirai.libraries.audit.service.AuditServiceDefaultInterface;
import com.example.mirai.libraries.core.annotation.Aggregate;
import com.example.mirai.libraries.core.annotation.AggregateRoot;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.annotation.LinkTo;
import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.AggregateType;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.CasePermissions;
import com.example.mirai.libraries.core.model.CaseStatus;
import com.example.mirai.libraries.core.model.StatusInterface;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.core.service.AggregateBuilderInterface;
import com.example.mirai.libraries.core.service.EntityResolverInterface;
import com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface;
import com.example.mirai.libraries.security.core.service.SecurityServiceDefaultInterface;
import com.example.mirai.libraries.util.DaoUtility;
import com.example.mirai.libraries.util.ReflectionUtil;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.proxy.HibernateProxy;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class AggregateBuilder implements AggregateBuilderInterface {

    public void buildAggregate(AggregateInterface aggregate, BaseEntityInterface baseEntityInterface, AggregateType aggregateType) {

        processAggregates(aggregate, baseEntityInterface, aggregateType);
        processEntities(aggregate, baseEntityInterface, aggregateType);
        processEmptyEntities(aggregate, baseEntityInterface, aggregateType);

        aggregate.toString();

    }

    private void processEmptyEntities(AggregateInterface aggregate, BaseEntityInterface baseEntityInterface, AggregateType aggregateType) {
        List<Field> nullBaseEntityInterfaceFields = getNullBaseEntityInterfaceFields(aggregate);
        for (Field nullBaseEntityInterfaceField : nullBaseEntityInterfaceFields) {
            Class entityType = nullBaseEntityInterfaceField.getType();
            if (entityType.equals(CaseStatus.class) || entityType.equals(ChangeLog.class)) {
                entityType = (Class) ReflectionUtil.getAnnotationValueByField(nullBaseEntityInterfaceField, EntityClass.class);
            }
            Object relatedEntity = getRelatedEntity(baseEntityInterface, entityType);
            setProperty(aggregate, relatedEntity, aggregateType);
        }
    }


    private List<Field> getNullBaseEntityInterfaceFields(AggregateInterface aggregate) {
        List<Field> baseEntityInterfaceFields = ReflectionUtil.getFieldsOfBaseEntityInterfaceType(aggregate.getClass(), BaseEntityInterface.class);
        List<Field> nullFields = new ArrayList<>();
        for (Field baseEntityInterfaceField : baseEntityInterfaceFields) {
            try {
                baseEntityInterfaceField.setAccessible(true);
                Object object = baseEntityInterfaceField.get(aggregate);
                if (object == null) {
                    nullFields.add(baseEntityInterfaceField);
                }
            } catch (Exception e) {
                throw new InternalAssertionException("COULD NOT GET VALUE");
            }
        }
        return nullFields;
    }

    private void processAggregates(AggregateInterface aggregate, BaseEntityInterface baseEntityInterface, AggregateType aggregateType) {
        List<Field> aggregateAnnotatedFields = ReflectionUtil.getFieldsWithAnnotation(aggregate.getClass(), Aggregate.class);

        for (Field aggregateAnnotatedField : aggregateAnnotatedFields) {
            Class childAggregateType = null;
            if (Collection.class.isAssignableFrom(aggregateAnnotatedField.getType())) {
                childAggregateType = ReflectionUtil.getActualType(aggregateAnnotatedField);
            } else {
                childAggregateType = aggregateAnnotatedField.getType();
            }
            //childAggregateType
            //get all types that can be set in this aggregate type
            List<Class> entityTypes = getEntityTypes(childAggregateType);

            //aggregate has a property that holds baseEntityInterface
            if (entityTypes.contains(baseEntityInterface.getClass())) {
                setProperty(aggregate, baseEntityInterface, aggregateType);
                entityTypes.remove(baseEntityInterface.getClass());
                AggregateInterface childAggregateInstance = (AggregateInterface) ReflectionUtil.createInstance(childAggregateType);
                buildAggregate(childAggregateInstance, baseEntityInterface, aggregateType);
                setProperty(aggregate, childAggregateInstance, aggregateType);
            }
            for (Class entityType : entityTypes) {
                Object relatedEntity = getRelatedEntity(baseEntityInterface, entityType);
                if (Objects.isNull(relatedEntity)) {
                    Class[] linkToValues = null;
                    if (Objects.isNull(ReflectionUtil.getFieldName(childAggregateType, entityType))) {
                        //when the field of type is not found -> need to get link to values by comparing entity class value as the type is changelog for all fields
                        linkToValues = getLinkToValuesOfFieldWithEntityClassAnnotationValue(childAggregateType, entityType);
                    } else {
                        linkToValues = (Class[]) ReflectionUtil.getAnnotationValueByFieldType(childAggregateType, entityType, LinkTo.class);
                    }
                    for (Class linkToValue : linkToValues) {
                        if (Objects.isNull(ReflectionUtil.getFieldNameWithType(aggregate.getClass(), linkToValue))) {
                            relatedEntity = ReflectionUtil.getFieldValueByAnnotationValue(aggregate, EntityClass.class, linkToValue);
                            if (relatedEntity instanceof ChangeLog && Objects.nonNull(relatedEntity)) {
                                // extracting id from changelog entries
                                Long relatedEntityId = ((ChangeLog) relatedEntity).getEntries().get(0).getId();
                                Class serviceClass = AnnotationUtils.getAnnotation(linkToValue, ServiceClass.class).value();
                                relatedEntity = getRelatedEntity(ApplicationContextHolder.getService(serviceClass).getEntityById(relatedEntityId), entityType);
                            }
                        } else {
                            if (Objects.nonNull(ReflectionUtil.getFieldValueByType(aggregate, linkToValue)))
                                relatedEntity = getRelatedEntity((BaseEntityInterface) ReflectionUtil.getFieldValueByType(aggregate, linkToValue), entityType);
                        }
                        if (relatedEntity != null) {
                            break;
                        }
                    }
                    //relatedEntity = ReflectionUtil.getFieldValueByType(aggregate, linkToValues[0]);
                }
                if (relatedEntity instanceof BaseEntityInterface) {
                    AggregateInterface childAggregateInstance = (AggregateInterface) ReflectionUtil.getFieldValueByType(aggregate, childAggregateType);
                    if (Objects.isNull(childAggregateInstance)) {
                        childAggregateInstance = (AggregateInterface) ReflectionUtil.createInstance(childAggregateType);
                    }
                    setProperty(childAggregateInstance, relatedEntity, aggregateType);
                    //TODO check when to pass baseEntityInterface and when to pass relatedEntity
                    //check if current instance has any field with aggregate annotation
                    if (ReflectionUtil.getFieldsWithAnnotation(childAggregateInstance.getClass(), Aggregate.class).size() > 0) {
                        buildAggregate(childAggregateInstance, baseEntityInterface, aggregateType);
                    }
                    setProperty(aggregate, childAggregateInstance, aggregateType);
                } else if (Objects.nonNull(relatedEntity) && Collection.class.isAssignableFrom(relatedEntity.getClass())) {
                    Collection relatedEntityCollection = (Collection) relatedEntity;
                    for (Object relatedEntityInCollection : relatedEntityCollection) {
                        AggregateInterface childAggregateInstance = (AggregateInterface) ReflectionUtil.createInstance(childAggregateType);
                        setProperty(childAggregateInstance, relatedEntityInCollection, aggregateType);
                        //TODO check when to pass baseEntityInterface and when to pass relatedEntity
                        buildAggregate(childAggregateInstance, (BaseEntityInterface) relatedEntityInCollection, aggregateType);
                        setProperty(aggregate, childAggregateInstance, aggregateType);
                    }
                }
            }
        }
    }

    private Class[] getLinkToValuesOfFieldWithEntityClassAnnotationValue(Class childAggregateType, Class entityType) {
        List<String> fieldNames = ReflectionUtil.getFieldNamesWithAnnotation(childAggregateType, EntityClass.class);
        for (String fieldName : fieldNames) {
            if (entityType.equals(ReflectionUtil.getAnnotationValueByFieldName(childAggregateType, fieldName, EntityClass.class))) {
                return (Class[]) ReflectionUtil.getAnnotationValueByFieldName(childAggregateType, fieldName, LinkTo.class);
            }
        }
        return null;
    }

    private void processEntities(AggregateInterface aggregate, BaseEntityInterface baseEntityInterface, AggregateType aggregateType) {
        //get all types that can be set in this aggregate
        List<Class> entityTypes = getEntityTypes(aggregate.getClass());

        //aggregate has a property that holds baseEntityInterface
        if (entityTypes.contains(baseEntityInterface.getClass())) {
            //set entity in aggregate
            setProperty(aggregate, baseEntityInterface, aggregateType);
            entityTypes.remove(baseEntityInterface.getClass());
        }
        for (Class entityType : entityTypes) {
            Object relatedEntity = getRelatedEntity(baseEntityInterface, entityType);
            setProperty(aggregate, relatedEntity, aggregateType);
        }
    }

    private Object getRelatedEntity(BaseEntityInterface baseEntityInterface, Class entityType) {
        if (canDeriveRelatedEntityFromBaseEntityInterfaceDirectly(baseEntityInterface, entityType)) {
            Object obj = deriveRelatedEntityFromBaseEntityInterfaceDirectly(baseEntityInterface, entityType);
            if (obj instanceof HibernateProxy)
                return DaoUtility.initializeAndUnproxy(obj);
            return obj;
        } else if (canDeriveRelatedEntityFromBaseEntityInterfaceId(baseEntityInterface, entityType)) {
            Object obj = deriveRelatedEntityFromBaseEntityInterfaceId(baseEntityInterface, entityType);
            if (obj instanceof HibernateProxy)
                return DaoUtility.initializeAndUnproxy(obj);
            return obj;
        }
        return null;
    }


    private boolean canDeriveRelatedEntityFromBaseEntityInterfaceDirectly(BaseEntityInterface baseEntityInterface, Class relatedEntityClass) {
        String fieldNameOneToMany = ReflectionUtil.getFieldNameWithTypeAndAnnotation(baseEntityInterface.getClass(), relatedEntityClass, OneToMany.class);
        String fieldNameOneToOne = ReflectionUtil.getFieldNameWithTypeAndAnnotation(baseEntityInterface.getClass(), relatedEntityClass, OneToOne.class);
        String fieldNameManyToOne = ReflectionUtil.getFieldNameWithTypeAndAnnotation(baseEntityInterface.getClass(), relatedEntityClass, ManyToOne.class);
        return (fieldNameOneToMany != null || fieldNameOneToOne != null || fieldNameManyToOne != null);
    }

    private Object deriveRelatedEntityFromBaseEntityInterfaceDirectly(BaseEntityInterface baseEntityInterface, Class relatedEntityClass) {
        String fieldNameOneToMany = ReflectionUtil.getFieldNameWithTypeAndAnnotation(baseEntityInterface.getClass(), relatedEntityClass, OneToMany.class);
        if (fieldNameOneToMany != null)
            return ReflectionUtil.getFieldValue(baseEntityInterface, fieldNameOneToMany);

        String fieldNameOneToOne = ReflectionUtil.getFieldNameWithTypeAndAnnotation(baseEntityInterface.getClass(), relatedEntityClass, OneToOne.class);
        if (fieldNameOneToOne != null)
            return ReflectionUtil.getFieldValue(baseEntityInterface, fieldNameOneToOne);

        String fieldNameManyToOne = ReflectionUtil.getFieldNameWithTypeAndAnnotation(baseEntityInterface.getClass(), relatedEntityClass, ManyToOne.class);
        if (fieldNameManyToOne != null)
            return ReflectionUtil.getFieldValue(baseEntityInterface, fieldNameManyToOne);

        return null;
    }

    private boolean canDeriveRelatedEntityFromBaseEntityInterfaceId(BaseEntityInterface baseEntityInterface, Class relatedEntityClass) {
        String fieldNameManyToOne = ReflectionUtil.getFieldNameWithTypeAndAnnotation(relatedEntityClass, baseEntityInterface.getClass(), ManyToOne.class);
        String fieldNameOneToOne = ReflectionUtil.getFieldNameWithTypeAndAnnotation(relatedEntityClass, baseEntityInterface.getClass(), OneToOne.class);
        return (fieldNameManyToOne != null || fieldNameOneToOne != null);
    }

    private Object deriveRelatedEntityFromBaseEntityInterfaceId(BaseEntityInterface baseEntityInterface, Class relatedEntityClass) {
        EntityResolverInterface entityResolverInterfaceImpl = ApplicationContextHolder.getApplicationContext().getBean(EntityResolverInterface.class);
        EntityServiceDefaultInterface entityServiceDefaultInterface = (EntityServiceDefaultInterface) entityResolverInterfaceImpl.getService(relatedEntityClass);

        String fieldNameManyToOne = ReflectionUtil.getFieldNameWithTypeAndAnnotation(relatedEntityClass, baseEntityInterface.getClass(), ManyToOne.class);
        if (fieldNameManyToOne != null) {
            BaseEntityList baseEntityList = entityServiceDefaultInterface.getAllByParent(baseEntityInterface.getId(), baseEntityInterface.getClass(), null, PageRequest.of(0, Integer.MAX_VALUE - 1));
            return baseEntityList.getResults();
        }

        String fieldNameOneToOne = ReflectionUtil.getFieldNameWithTypeAndAnnotation(relatedEntityClass, baseEntityInterface.getClass(), OneToOne.class);
        if (fieldNameOneToOne != null) {
            BaseEntityList baseEntityList = entityServiceDefaultInterface.getAllByParent(baseEntityInterface.getId(), baseEntityInterface.getClass(), null, PageRequest.of(0, Integer.MAX_VALUE - 1));
            List<BaseEntityInterface> relatedEntityList = baseEntityList.getResults();
            if (relatedEntityList.size() == 1)
                return relatedEntityList.get(0);
            else
                return null;
        }

        return null;
    }


    private List<Class> getEntityTypes(Class aggregateClass) {
        List<Class> listOfClasses = new ArrayList<>();
        List<Field> listOfFields = new ArrayList<>();
        List<Field> aggregateRootAnnotatedFields = ReflectionUtil.getFieldsWithAnnotation(aggregateClass, AggregateRoot.class);
        List<Field> linkToAnnotatedFields = ReflectionUtil.getFieldsWithAnnotation(aggregateClass, LinkTo.class);
        listOfFields.addAll(aggregateRootAnnotatedFields);
        listOfFields.addAll(linkToAnnotatedFields);

        for (Field field : listOfFields) {
            if (Collection.class.isAssignableFrom(field.getType())) {
                Class actualType = ReflectionUtil.getActualType(field);
                if (actualType.equals(CaseStatus.class) || actualType.equals(ChangeLog.class)) {
                    listOfClasses.add((Class) ReflectionUtil.getAnnotationValueByField(field, EntityClass.class));
                } else {
                    listOfClasses.add(actualType);
                }
            } else {
                if (field.getType().equals(CaseStatus.class) || field.getType().equals(ChangeLog.class)) {
                    listOfClasses.add((Class) ReflectionUtil.getAnnotationValueByField(field, EntityClass.class));
                } else {
                    listOfClasses.add(field.getType());
                }
            }
        }

        return listOfClasses;
    }

    private void setProperty(AggregateInterface aggregate, Object object, AggregateType aggregateType) {
        //if this object needs to go into set
        CaseStatus caseStatus = new CaseStatus();
        if (object instanceof AggregateInterface) {
            if (ReflectionUtil.getSoleFieldOfType(aggregate, object) != null) {
                ReflectionUtil.setValueOfSoleFieldOfType(aggregate, object);
            } else if (ReflectionUtil.getSoleCollectionFieldOfType(aggregate, object) != null) {
                ReflectionUtil.setValueOfSoleCollectionFieldOfType(aggregate, object);
            } else {
                throw new InternalAssertionException("Aggregate Not Set");
            }
        } else if (object instanceof BaseEntityInterface) {
            if (aggregateType == AggregateType.CASE_ACTIONS && ReflectionUtil.getValueOfSoleFieldOfType(aggregate, caseStatus) == null) {
                setEntityCaseStatus((BaseEntityInterface) object, caseStatus);
                ReflectionUtil.setValueOfFieldByEntityClassAnnotationValue(aggregate, caseStatus, EntityClass.class, object.getClass());
            } else if (aggregateType == AggregateType.CHANGELOG && ReflectionUtil.getValueOfSoleFieldOfType(aggregate, new ChangeLog()) == null) {
                AuditServiceDefaultInterface auditServiceDefaultInterface = (AuditServiceDefaultInterface) ApplicationContextHolder.getService(AnnotationUtils.findAnnotation(object.getClass(), ServiceClass.class).value());
                ChangeLog changeLog = auditServiceDefaultInterface.getChangeLog(((BaseEntityInterface) object).getId());
                ReflectionUtil.setValueOfFieldByEntityClassAnnotationValue(aggregate, changeLog, EntityClass.class, object.getClass());
            } else if (ReflectionUtil.getFieldNameWithType(aggregate.getClass(), object.getClass()) != null && ReflectionUtil.getValueOfSoleFieldOfType(aggregate, object) == null) { //TODO what if the value of property is null
                ReflectionUtil.setValueOfSoleFieldOfType(aggregate, object);
            }
        } else if (object == null) {
            //assign entity
            System.out.println("########### OBJECT IS NULL ##########");
        } /*else if(Collection.class.isAssignableFrom(object.getClass())) {
            //assign entity collection
            setSoleCollectionFieldOfType(aggregate, object);
        }*/ else {
            throw new InternalAssertionException("Object Must Be BaseEntityInterface or AggregateInterface");
        }
    }

    private void setEntityCaseStatus(BaseEntityInterface entity, CaseStatus caseStatus) {
        setCasePermissions(entity, caseStatus);
        caseStatus.setId(entity.getId());
        caseStatus.setStatus(entity.getStatus());
        EntityResolverInterface entityResolverInterfaceImpl = ApplicationContextHolder.getApplicationContext().getBean(EntityResolverInterface.class);
        StatusInterface[] statusInterfaces = entityResolverInterfaceImpl.getEntityStatuses(entity.getClass());
        if (Objects.nonNull(statusInterfaces)) {
            Optional<StatusInterface> statusItem = Arrays.stream(statusInterfaces).filter(statusInterface -> statusInterface.getStatusCode().equals(entity.getStatus())).findFirst();
            if (statusItem.isPresent()) {
                caseStatus.setStatusLabel(statusItem.get().getStatusLabel());
            }
        }
    }

    private void setCasePermissions(BaseEntityInterface entity, CaseStatus caseStatus) {
        EntityServiceDefaultInterface entityServiceDefaultInterface = (EntityServiceDefaultInterface) ApplicationContextHolder.getService(AnnotationUtils.findAnnotation(entity.getClass(), ServiceClass.class).value());
        SecurityServiceDefaultInterface securityServiceDefaultInterface = (SecurityServiceDefaultInterface) entityServiceDefaultInterface;
        CasePermissions casePermissions = securityServiceDefaultInterface.getCasePermissions(entity);
        caseStatus.setCasePermissions(casePermissions);
    }

    public AggregateInterface addDeletedEntities(AggregateInterface aggregate, BaseEntityInterface baseEntity) {
        //List<String> fieldsWithLinkTo = ReflectionUtil.getFieldNamesWithAnnotation(aggregate.getClass(), LinkTo.class);
        List<Field> fieldsWithLinkToAnnotation = ReflectionUtil.getFieldsWithAnnotation(aggregate.getClass(), LinkTo.class);
        for (Field fieldWithLinkTo : fieldsWithLinkToAnnotation) {
            Class linkToEntity = (Class) ReflectionUtil.getAnnotationValueByField(fieldWithLinkTo, EntityClass.class);
            String fieldNameWithRootEntity = ReflectionUtil.getFieldNameWithType(linkToEntity, baseEntity.getClass());
            Field field = ReflectionUtil.getFieldFromClassHierarchy(linkToEntity, fieldNameWithRootEntity);
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);
            AuditReader auditReader = AuditReaderFactory.get(entityManager);
            final AuditQuery query = auditReader.createQuery().forRevisionsOfEntityWithChanges(linkToEntity, true);
            query.add(AuditEntity.revisionType().eq(RevisionType.DEL));
            query.add(AuditEntity.property(joinColumn.name()).eq(baseEntity.getId()));
            final List<?> results = query.getResultList();
            if (!results.isEmpty()) {
                Object[] properties = (Object[]) results.get(0);
                Object entity = properties[0];
                AuditableUpdater auditableUpdater = (AuditableUpdater) properties[1];
                RevisionType revisionType = (RevisionType) properties[2];
				/*String property = "";
				if (((HashSet) properties[3]).size() > 0) {
					property = (String) ((HashSet) properties[3]).iterator().next();
				}*/
                User updater = new User();
                updater.setUserId(auditableUpdater.getUserId());
                updater.setFullName(auditableUpdater.getFullName());
                updater.setAbbreviation(auditableUpdater.getAbbreviation());
                updater.setDepartmentName(auditableUpdater.getDepartmentName());
                Date updatedOn = new Date(auditableUpdater.getTimestamp());
                Integer revisionNumber = auditableUpdater.getId();
                ChangeLog changeLog = new ChangeLog();
                changeLog.addEntry(updater, updatedOn, revisionNumber, revisionType, "", null, entity, ((BaseEntityInterface) entity).getId());
                //fetching and adding revisions of deleted entity
                if (Objects.nonNull(((BaseEntityInterface) entity).getId())) {
                    AuditServiceDefaultInterface auditServiceDefaultInterface = (AuditServiceDefaultInterface) ApplicationContextHolder.getService(AnnotationUtils.findAnnotation(entity.getClass(), ServiceClass.class).value());
                    ChangeLog deletedEntityChangeLog = auditServiceDefaultInterface.getChangeLog(((BaseEntityInterface) entity).getId());
                    List<ChangeLog.Entry> changeLogEntries = changeLog.getEntries();
                    changeLogEntries.addAll(deletedEntityChangeLog.getEntries());
                    changeLog.setEntries(changeLogEntries);
                }
                ReflectionUtil.setFieldValue(aggregate, fieldWithLinkTo.getName(), changeLog);
            }
        }
        List<Field> aggregateAnnotatedFields = ReflectionUtil.getFieldsWithAnnotation(aggregate.getClass(), Aggregate.class);
        for (Field aggregateAnnotatedField : aggregateAnnotatedFields) {
            //AggregateInterface fieldWithAggregate = (AggregateInterface) ReflectionUtil.getFieldValue(aggregate, fieldsWithAggregate.get(0));
            Class childAggregateType = null;
            if (Collection.class.isAssignableFrom(aggregateAnnotatedField.getType())) {
                childAggregateType = ReflectionUtil.getActualType(aggregateAnnotatedField);
            } else {
                childAggregateType = aggregateAnnotatedField.getType();
            }
            AggregateInterface aggregateInstance = (AggregateInterface) ReflectionUtil.createInstance(childAggregateType);
            ReflectionUtil.setValueOfSoleCollectionFieldOfType(aggregate, aggregateInstance);
            addDeletedEntities(aggregateInstance, baseEntity);
            if (areAllFieldsNull(aggregateInstance, ChangeLog.class)) {
                String aggregateFieldName = ReflectionUtil.getFieldNameOfCollectionsType(aggregate.getClass(), aggregateInstance.getClass());
                Set<AggregateInterface> aggregateSet = (Set<AggregateInterface>) ReflectionUtil.getFieldValue(aggregate, aggregateFieldName);
                removeEmptyAggregateFromSet(aggregateSet);
                ReflectionUtil.setFieldValue(aggregate, aggregateFieldName, aggregateSet.size() == 0 ? null : aggregateSet);
            }
        }
        return aggregate;
    }

    private Set<AggregateInterface> removeEmptyAggregateFromSet(Set<AggregateInterface> aggregateSet) {
        if (aggregateSet != null) {
            Iterator<AggregateInterface> iterator = aggregateSet.iterator();
            Set<AggregateInterface> aggregatesToRemove = new HashSet<>();
            while (iterator.hasNext()) {
                AggregateInterface aggregateItem = iterator.next();
                if (areAllFieldsNull(aggregateItem, ChangeLog.class)) {
                    aggregatesToRemove.add(aggregateItem);
                }
            }
            aggregateSet.removeAll(aggregatesToRemove);
        }
        return aggregateSet;
    }

    private boolean areAllFieldsNull(AggregateInterface aggregate, Class type) {
        List<Field> fields = ReflectionUtil.getFieldsOfType(aggregate.getClass(), type);
        List<Field> nullValueFields = new ArrayList<>();
        for (Field field : fields) {
            if (ReflectionUtil.getFieldValue(aggregate, field.getName()) == null) {
                nullValueFields.add(field);
            }
        }
        if (nullValueFields.size() == fields.size()) {
            return true;
        }
        return false;
    }
}
