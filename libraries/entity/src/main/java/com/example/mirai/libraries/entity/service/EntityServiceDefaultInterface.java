package com.example.mirai.libraries.entity.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.example.mirai.libraries.core.annotation.Aggregate;
import com.example.mirai.libraries.core.annotation.AggregateRoot;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.annotation.LinkTo;
import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.exception.EntityIdNotFoundException;
import com.example.mirai.libraries.core.exception.EntityLinkMismatchException;
import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.core.exception.ParallelUpdateException;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.AggregateType;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.BaseView;
import com.example.mirai.libraries.core.model.CaseStatus;
import com.example.mirai.libraries.core.model.ContextInterface;
import com.example.mirai.libraries.core.model.EntityLink;
import com.example.mirai.libraries.core.model.StatusInterface;
import com.example.mirai.libraries.core.service.AggregateBuilderInterface;
import com.example.mirai.libraries.core.service.ServiceInterface;
import com.example.mirai.libraries.entity.model.Id;
import com.example.mirai.libraries.entity.model.LinkedItems;
import com.example.mirai.libraries.entity.model.StatusCountOverview;
import com.example.mirai.libraries.entity.model.StatusOverview;
import com.example.mirai.libraries.entity.repository.BaseRepository;
import com.example.mirai.libraries.entity.service.helper.service.ServiceHelper;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.libraries.util.CaseUtil;
import com.example.mirai.libraries.util.DaoUtility;
import com.example.mirai.libraries.util.ReflectionUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

@CacheConfig(cacheResolver = "customCacheResolver")
public interface EntityServiceDefaultInterface extends ServiceInterface {
	BaseEntityInterface performCaseAction(Long id, String action);

	default Class<? extends BaseEntityInterface> getEntityClass() {
		EntityClass entityClass = AnnotationUtils.findAnnotation(this.getClass(), EntityClass.class);
		return Objects.requireNonNull(entityClass).value();
	}

	default BaseEntityInterface create(BaseEntityInterface entity) {
		JpaRepository jpaRepository = ApplicationContextHolder.getJpaRepository(entity.getClass());
		return (BaseEntityInterface) jpaRepository.save(entity);
	}

	default BaseEntityInterface createLinkedEntityWithLinks(BaseEntityInterface entity, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
		//get jpa repository of child entity
		JpaRepository jpaRepository = ApplicationContextHolder.getJpaRepository(entity.getClass());
		AtomicReference<BaseEntityInterface> result = new AtomicReference<>();

		entityLinkSet.forEach(entityLink -> {
			Class linkedEntityClass = entityLink.getEClass();
			Long linkedEntityId = entityLink.getId();
			ServiceClass serviceClass = AnnotationUtils.findAnnotation(linkedEntityClass, ServiceClass.class);

			Class<? extends EntityServiceDefaultInterface> serviceClassOfParentEntity = serviceClass.value();
			if (Objects.isNull(serviceClassOfParentEntity)) {
				throw new InternalAssertionException("Service class of linked entity not available");
			}

			//get parent entity
			ServiceInterface serviceInterface = ApplicationContextHolder.getService(serviceClassOfParentEntity);
			if (!EntityServiceDefaultInterface.class.isAssignableFrom(serviceInterface.getClass()))
				throw new InternalAssertionException("Service must implement EntityServiceDefaultInterface");
			BaseEntityInterface linkedEntity = ((EntityServiceDefaultInterface) serviceInterface).findById(linkedEntityId);

			String relationshipCardinality = ReflectionUtil.getRelationshipCardinality(entity.getClass(), linkedEntityClass);
			if (Objects.isNull(relationshipCardinality))
				throw new InternalAssertionException("Field that holds linked entity not found");


			switch (relationshipCardinality) {
				case "ONE-TO-ONE":
					String oneToOneLinkField = ReflectionUtil.getFieldNameWithTypeAndAnnotation(entity.getClass(), linkedEntityClass, OneToOne.class);
					ReflectionUtil.setFieldValue(entity, oneToOneLinkField, linkedEntity);
					break;
				case "MANY-TO-ONE":
					String manyToOneLinkField = ReflectionUtil.getFieldNameWithTypeAndAnnotation(entity.getClass(), linkedEntityClass, ManyToOne.class);
					ReflectionUtil.setFieldValue(entity, manyToOneLinkField, linkedEntity);
					break;
				case "ONE-TO-MANY":
					//get parent entity' field with One to many annotation
					String oneToManyLinkField = ReflectionUtil.getFieldNameOfCollectionsTypeAndAnnotation(linkedEntity.getClass(), entity.getClass(), OneToMany.class);

					//add child entity to the field that represents set/list in parent field
					boolean success = ReflectionUtil.addFieldValue(linkedEntity, oneToManyLinkField, entity);
					if (!success)
						throw new InternalAssertionException("Child Entity Not Added");

					Map<String, Object> changedAttrs = new HashMap<>();
					changedAttrs.put(oneToManyLinkField, ReflectionUtil.getFieldValue(linkedEntity, oneToManyLinkField));
					break;
				case "BI-DIRECTIONAL":
					//add parent entity to the side that represent many-to-one
					String manyToOneLinkField1 = ReflectionUtil.getFieldNameWithTypeAndAnnotation(entity.getClass(), linkedEntityClass, ManyToOne.class);
					ReflectionUtil.setFieldValue(entity, manyToOneLinkField1, linkedEntity);

					//save child entity
					BaseEntityInterface result2 = null;
					if (Objects.isNull(result.get())) {
						result2 = (BaseEntityInterface) jpaRepository.save(entity);
						result.set(result2);
					}

					//get parent entity' field with One to many annotation
					String oneToManyLinkField1 = ReflectionUtil.getFieldNameOfCollectionsTypeAndAnnotation(linkedEntity.getClass(), entity.getClass(), OneToMany.class);

					//add child entity to the field that represents set/list in parent field
					boolean success1 = ReflectionUtil.addFieldValue(linkedEntity, oneToManyLinkField1, result2);
					if (!success1)
						throw new InternalAssertionException("Child Entity Not Added");

					Map<String, Object> changedAttrs1 = new HashMap<>();
					changedAttrs1.put(oneToManyLinkField1, ReflectionUtil.getFieldValue(linkedEntity, oneToManyLinkField1));
					break;
				default:
					throw new InternalAssertionException("Relationship Cardinality Not Supported");
			}
		});

		//save child entity
		if (Objects.isNull(result.get()))
			result.set((BaseEntityInterface) jpaRepository.save(entity));
		return result.get();
	}

	default AggregateInterface createRootAggregate(AggregateInterface aggregate) {
		BaseEntityInterface primaryEntity;
		List<BaseEntityInterface> createdEntities = new ArrayList<>();
		// get field with annotation aggregate root
		Object entity = ReflectionUtil.getFieldValue(aggregate,
				ReflectionUtil.getFieldNamesWithAnnotation(aggregate.getClass(), AggregateRoot.class).get(0));
		// create aggregate root
		EntityServiceDefaultInterface entityServiceDefaultInterface = ApplicationContextHolder.getApplicationContext().getBean(this.getClass());
		primaryEntity = entityServiceDefaultInterface.create((BaseEntityInterface) entity);
		createdEntities.add(primaryEntity);
		// add root objects / links to remaining
		createEntitiesAndAddLinks(aggregate, createdEntities);
		return aggregate;
	}

	default AggregateInterface updateAggregate(Long id, JsonNode aggregateNode, Class aggregateClass) throws JsonProcessingException {
		String fieldName = null;
		Class entityClass = null;
		BaseEntityInterface entityIns = null;
		ArrayList<Long> linkToIds = new ArrayList<>();
		linkToIds.add(id);
		AggregateInterface aggregate = (AggregateInterface) ObjectMapperUtil.getObjectMapper().convertValue(aggregateNode, aggregateClass);
		if (ReflectionUtil.getFieldNamesWithAnnotation(aggregateClass, AggregateRoot.class).size() > 0) {
			fieldName = ReflectionUtil.getFieldNamesWithAnnotation(aggregateClass, AggregateRoot.class).get(0);
			entityClass = ReflectionUtil.getDatatypeOfFieldInClass(aggregateClass, fieldName);
		}
		else {
			fieldName = Objects.requireNonNull(ReflectionUtil.getFieldNamesWithAnnotation(aggregateClass, LinkTo.class)).get(0);
			entityClass = ReflectionUtil.getDatatypeOfFieldInClass(aggregateClass, fieldName);
		}
		String fieldNameInJson = fieldName;
		if (ObjectMapperUtil.getObjectMapper().getPropertyNamingStrategy().equals(PropertyNamingStrategy.SNAKE_CASE)) {
			fieldNameInJson = CaseUtil.convertCamelToSnakeCase(fieldName);
		}
		if (aggregateNode.get(fieldNameInJson) != null) {
			Map<String, Object> newInsChangedAttrs = ObjectMapperUtil.getChangedAttributes(aggregateNode.get(fieldNameInJson));
			entityIns = (BaseEntityInterface) ObjectMapperUtil.getObjectMapper().treeToValue(aggregateNode.get(fieldNameInJson), entityClass);
			ServiceInterface serviceInterface = ApplicationContextHolder.getService(Objects.requireNonNull(AnnotationUtils.findAnnotation(entityClass, ServiceClass.class)).value());
			if (!EntityServiceDefaultInterface.class.isAssignableFrom(serviceInterface.getClass()))
				throw new InternalAssertionException("Service must implement EntityServiceDefaultInterface");
			EntityServiceDefaultInterface entityServiceDefaultInterface = (EntityServiceDefaultInterface) serviceInterface;
			if (ServiceHelper.isEntityLinkToCorrectId(aggregate, fieldName, entityServiceDefaultInterface.getEntityById(entityIns.getId()), linkToIds)) {
				BaseEntityInterface updatedObject = entityServiceDefaultInterface.update(entityIns, newInsChangedAttrs);
				((ObjectNode) aggregateNode).set(fieldNameInJson, ObjectMapperUtil.getObjectMapper().convertValue(updatedObject, JsonNode.class));
			}
			else {
				throw new EntityLinkMismatchException();
			}
		}
		if (ReflectionUtil.getFieldNamesWithAnnotation(aggregate.getClass(), Aggregate.class).size() > 0) {
			String fieldNameWithAggregate = ReflectionUtil.getFieldNamesWithAnnotation(aggregate.getClass(), Aggregate.class).get(0);
			String fieldNameWithAggregateInJson = fieldNameWithAggregate;
			if (ObjectMapperUtil.getObjectMapper().getPropertyNamingStrategy().equals(PropertyNamingStrategy.SNAKE_CASE)) {
				fieldNameWithAggregateInJson = CaseUtil.convertCamelToSnakeCase(fieldNameWithAggregate);
			}
			Set<Object> aggregateSet = (Set<Object>) ReflectionUtil.getFieldValue(aggregate, fieldNameWithAggregate);
			Class aggregateItemClass = ReflectionUtil.getDatatypeOfFieldInClass(aggregate.getClass(), Objects.requireNonNull(ReflectionUtil.getFieldNamesWithAnnotation(aggregate.getClass(), Aggregate.class)).get(0));
			if (aggregateSet != null) {
				aggregateNode.get(fieldNameWithAggregateInJson).forEach(aggregateItemNode -> {
					try {
						updateAggregate(id, aggregateItemNode, aggregateItemClass);
					}
					catch (JsonProcessingException e) {
						Logger log = LoggerFactory.getLogger(EntityServiceDefaultInterface.class);
						log.error("update aggregate failed. ", e.getStackTrace());
					}
				});
			}
		}
		return (AggregateInterface) ObjectMapperUtil.getObjectMapper().convertValue(aggregateNode, aggregateClass);
	}


	default Object createEntitiesAndAddLinks(Object o, List<BaseEntityInterface> createdEntities) {
		EntityServiceDefaultInterface entityServiceDefaultInterface = ApplicationContextHolder.getApplicationContext().getBean(this.getClass());
		List<String> fieldsWithLinkTo = ReflectionUtil.getFieldNamesWithAnnotation(o.getClass(), LinkTo.class);
		BaseEntityInterface createdEntity = null;
		for (String fieldName : fieldsWithLinkTo) {
			Object fieldValue = ReflectionUtil.getFieldValue(o, fieldName);
			if (fieldValue instanceof BaseEntityInterface) {
				Class[] linkClasses = (Class[]) ReflectionUtil.getAnnotationValueByFieldName(o, fieldName, LinkTo.class);
				createdEntity = entityServiceDefaultInterface.createLinkedEntityWithLinks((BaseEntityInterface) fieldValue, ServiceHelper.getEntityLinkSet(linkClasses, createdEntities));
				createdEntities.add(createdEntity);
			}
		}
		List<String> fieldsWithAggregate = ReflectionUtil.getFieldNamesWithAnnotation(o.getClass(), Aggregate.class);
		for (String fieldName : fieldsWithAggregate) {
			Object fieldValue = ReflectionUtil.getFieldValue(o, fieldName);
			if (fieldValue != null && Collection.class.isAssignableFrom(fieldValue.getClass())) {
				for (Object itemInCollection : (HashSet) fieldValue) {
					this.createEntitiesAndAddLinks(itemInCollection, createdEntities);
				}
			}
			else if (fieldValue instanceof BaseEntityInterface || fieldValue instanceof AggregateInterface) {
				this.createEntitiesAndAddLinks(fieldValue, createdEntities);
			}
		}
		// to ensure only those objects which are in LinkTo property present in the list
		// eg, if B has LinkTo(A), A is available in createdEntities and B is removed
		createdEntities.remove(createdEntity);
		return o;
	}


	default AggregateInterface createAggregate(AggregateInterface entity, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
		List<BaseEntityInterface> linkObjects = new ArrayList<>();
		entityLinkSet.forEach(entityLink -> {
			try {
				BaseEntityInterface linkObject = (BaseEntityInterface) Class.forName(entityLink.getEClass().getName()).getConstructor().newInstance();
				linkObject.setId(entityLink.getId());
				linkObjects.add(linkObject);
			}
			catch (Exception e) {
				Logger log = LoggerFactory.getLogger(EntityServiceDefaultInterface.class);
				log.warn(e.getMessage());
			}
		});
		return (AggregateInterface) this.createEntitiesAndAddLinks(entity, linkObjects);
	}

	default BaseEntityInterface update(BaseEntityInterface entity, Map<String, Object> newChangedAttrs) {
		JpaRepository jpaRepository = ApplicationContextHolder.getJpaRepository(entity.getClass());
		BaseEntityInterface readInst = getEntityById(entity.getId());
		Map<String, Object> readInstMap = ObjectMapperUtil.getObjectMapper().convertValue(readInst, Map.class);
		Map<String, Object> entityMap = ObjectMapperUtil.getObjectMapper().convertValue(entity, Map.class);
		newChangedAttrs.entrySet().forEach(entry -> {
			String fieldName = entry.getKey();
			readInstMap.put(fieldName, entityMap.get(fieldName));
		});

		BaseEntityInterface updatedInst = ObjectMapperUtil.getObjectMapper().convertValue(readInstMap, entity.getClass());
		ServiceHelper.updateJSONIgnoreFields(readInst, updatedInst);
		BaseEntityInterface savedInst = (BaseEntityInterface) jpaRepository.save(updatedInst);
		return DaoUtility.initializeAndUnproxy(savedInst);
	}

	default BaseEntityInterface update(BaseEntityInterface entity) {
		JpaRepository jpaRepository = ApplicationContextHolder.getJpaRepository(entity.getClass());
		BaseEntityInterface savedInst = (BaseEntityInterface) jpaRepository.save(entity);
		return DaoUtility.initializeAndUnproxy(savedInst);
	}


	CaseStatus performCaseActionAndGetCaseStatus(Long id, String action);

	AggregateInterface performCaseActionAndGetCaseStatusAggregate(Long id, String action, Class<AggregateInterface> aggregateInterfaceClass);


	default BaseEntityInterface merge(BaseEntityInterface newInst, BaseEntityInterface oldInst,
			List<String> oldInsChangedAttributeNames, List<String> newInsChangedAttributeNames) {
		JpaRepository jpaRepository = ApplicationContextHolder.getJpaRepository(newInst.getClass());
		BaseEntityInterface readInst = (BaseEntityInterface) jpaRepository.getOne(newInst.getId());
		readInst = DaoUtility.initializeAndUnproxy(readInst);
		Map<String, Object> readInstMap = ObjectMapperUtil.getObjectMapper().convertValue(DaoUtility.initializeAndUnproxy(readInst), Map.class);
		Map<String, Object> oldInstMap = ObjectMapperUtil.getObjectMapper().convertValue(oldInst, Map.class);
		Map<String, Object> newInstMap = ObjectMapperUtil.getObjectMapper().convertValue(newInst, Map.class);

		List<String> fieldNamesPresentInOld = new ArrayList<>();
		oldInsChangedAttributeNames.stream().forEach(fieldName -> {
			Object value = oldInstMap.get(fieldName);
			boolean oldSameAsRead = Objects.deepEquals(value, readInstMap.get(fieldName));
			if (oldSameAsRead) {
				readInstMap.put(fieldName, newInstMap.get(fieldName));//check if new does not contain the property at all, i.e. attempting to nullify
				fieldNamesPresentInOld.add(fieldName);
			}
			else {
				throw new ParallelUpdateException();
			}
		});

		newInsChangedAttributeNames.stream().filter(field -> !fieldNamesPresentInOld.contains(field)).forEach(fieldName -> {
			Object value = newInstMap.get(fieldName);
			boolean oldSameAsRead = Objects.deepEquals(null, readInstMap.get(fieldName));
			if (oldSameAsRead) {
				readInstMap.put(fieldName, value);
			}
			else {
				throw new ParallelUpdateException();
			}
		});
		BaseEntityInterface updatedInst = ObjectMapperUtil.getObjectMapper().convertValue(readInstMap, newInst.getClass());
		//Adding JSON ignore fields to the object, as serialization and deserialization removes from object
		ServiceHelper.updateJSONIgnoreFields(readInst, updatedInst);
		BaseEntityInterface savedInst = (BaseEntityInterface) jpaRepository.save(updatedInst);
		return DaoUtility.initializeAndUnproxy(savedInst);
	}

	default BaseEntityInterface overwrite(BaseEntityInterface newInst, List<String> oldInsChangedAttributeNames, List<String> newInsChangedAttributeNames) {
		JpaRepository jpaRepository = ApplicationContextHolder.getJpaRepository(newInst.getClass());
		BaseEntityInterface readInst = (BaseEntityInterface)jpaRepository.getOne(newInst.getId());
		readInst = DaoUtility.initializeAndUnproxy(readInst);
		Map<String, Object> readInstMap = (Map) ObjectMapperUtil.getObjectMapper().convertValue(DaoUtility.initializeAndUnproxy(readInst), Map.class);
		Map<String, Object> newInstMap = (Map)ObjectMapperUtil.getObjectMapper().convertValue(newInst, Map.class);
		List<String> fieldNamesPresentInOld = new ArrayList();
		oldInsChangedAttributeNames.stream()
				.forEach((fieldName) -> {
					readInstMap.put(fieldName, newInstMap.get(fieldName));
					fieldNamesPresentInOld.add(fieldName);
				});
		newInsChangedAttributeNames.stream()
				.filter(field -> !fieldNamesPresentInOld.contains(field))
				.forEach((fieldName) -> {
					Object value = newInstMap.get(fieldName);
					readInstMap.put(fieldName, value);
				});
		BaseEntityInterface updatedInst = ObjectMapperUtil.getObjectMapper().convertValue(readInstMap, newInst.getClass());
		ServiceHelper.updateJSONIgnoreFields(readInst, updatedInst);
		BaseEntityInterface savedInst = (BaseEntityInterface)jpaRepository.save(updatedInst);
		return DaoUtility.initializeAndUnproxy(savedInst);
	}

	default BaseEntityList filter(String criteria, Optional<String> viewCriteria, Optional<Class> viewClass, Pageable pageable) {
		JpaRepository jpaRepository = ApplicationContextHolder.getJpaRepository(getEntityClass());
		Slice<Id> filteredIdList = ((BaseRepository<BaseEntityInterface>) jpaRepository).getIds(criteria, viewCriteria, pageable,
				(Class<BaseEntityInterface>) getEntityClass(), Optional.of("results"), viewClass);
		List<BaseEntityInterface> listOfEntities = filteredIdList.stream()
				.map(filteredId -> {
					BaseEntityInterface entity = null;

					entity = getEntityById(filteredId.getValue());
					return entity;
				})
				.collect(Collectors.toList());
		return new BaseEntityList(filteredIdList, listOfEntities);
	}

	default BaseEntityList filter(String criteria, Pageable pageable) {
		return this.filter(criteria, Optional.empty(), Optional.empty(), pageable);
	}

	default Slice<Id> filterIds(String criteria, Pageable pageable) {
		JpaRepository jpaRepository = ApplicationContextHolder.getJpaRepository(getEntityClass());
		return ((BaseRepository<BaseEntityInterface>) jpaRepository).getIds(criteria, Optional.empty(), pageable,
				(Class<BaseEntityInterface>) getEntityClass(), java.util.Optional.empty(), Optional.empty());
	}

	default Slice<Id> filterIds(String criteria, String viewCriteria, Class viewClass, Pageable pageable) {
		JpaRepository jpaRepository = ApplicationContextHolder.getJpaRepository(getEntityClass());
		return ((BaseRepository<BaseEntityInterface>) jpaRepository).getIds(criteria, Optional.of(viewCriteria), pageable,
				(Class<BaseEntityInterface>) getEntityClass(), java.util.Optional.empty(), Optional.of(viewClass));
	}

	default Slice<Id> filterIds(String viewCriteria, Class viewClass, Optional<String> optionalSliceSelect, Pageable pageable) {
		JpaRepository jpaRepository = ApplicationContextHolder.getJpaRepository(getEntityClass());
		return ((BaseRepository<BaseEntityInterface>) jpaRepository).getIds(viewCriteria, pageable, optionalSliceSelect, viewClass);
	}

	@Cacheable(key = "#root.target.getEntityClass().getCanonicalName() + \"-\" + #root.args[0]", condition = "@entityConfigSpringCacheConfiguration !=null &&  !(@entityConfigSpringCacheConfiguration.getType().equalsIgnoreCase(\"NONE\"))")
	default BaseEntityInterface findById(Long id) {
		JpaRepository jpaRepository = ApplicationContextHolder.getJpaRepository(getEntityClass());
		Optional<BaseEntityInterface> readInst = jpaRepository.findById(id);
		try {
			if (readInst.get() instanceof HibernateProxy) {
				return DaoUtility.initializeAndUnproxy(readInst.get());
			}
		}
		catch (Exception e) {
			throw new EntityIdNotFoundException(getEntityClass().getSimpleName());
		}
		return readInst.get();
	}

	default BaseEntityInterface getEntityById(Long entityId) {
		EntityServiceDefaultInterface entityServiceDefaultInterface = ApplicationContextHolder.getApplicationContext().getBean(this.getClass());
		BaseEntityInterface entity = null;
		try {
			entity = entityServiceDefaultInterface.findById(entityId);
			if (entity instanceof HibernateProxy) {
				return DaoUtility.initializeAndUnproxy(entity);
			}
		}
		catch (Exception e) {
			// fetch the entity and cache the result
			entity = this.findById(entityId);
			if (entity instanceof HibernateProxy) {
				entity = DaoUtility.initializeAndUnproxy(entity);
			}
			entity = entityServiceDefaultInterface.cacheEntity(entity);
		}
		return entity;
	}

	default BaseEntityInterface cacheEntity(BaseEntityInterface entity) {
		return entity;
	}

	default BaseEntityInterface getById(Long id) {
		JpaRepository jpaRepository = ApplicationContextHolder.getJpaRepository(getEntityClass());
		BaseEntityInterface readInst = (BaseEntityInterface) jpaRepository.getOne(id);
		return DaoUtility.initializeAndUnproxy(readInst);
	}

	@Cacheable(key = "#root.target.getEntityClass().getCanonicalName() + \"-\" + #root.args[0]", condition = "@entityConfigSpringCacheConfiguration !=null &&  !(@entityConfigSpringCacheConfiguration.getType().equalsIgnoreCase(\"NONE\"))")
	default BaseEntityInterface get(Long id) {
		JpaRepository jpaRepository = ApplicationContextHolder.getJpaRepository(getEntityClass());
		BaseEntityInterface readInst = (BaseEntityInterface) jpaRepository.getOne(id);
		return DaoUtility.initializeAndUnproxy(readInst);
	}

	default void delete(Long id) {
		JpaRepository jpaRepository = ApplicationContextHolder.getJpaRepository(getEntityClass());
		jpaRepository.delete(getEntityById(id));
	}

	default void deleteWithoutApplicationEventPublish(Long id) {
		JpaRepository jpaRepository = ApplicationContextHolder.getJpaRepository(getEntityClass());
		jpaRepository.delete(getEntityById(id));
	}

	default BaseEntityList getAllByParent(Long parentId, Class linkedEntityClass, String criteria, Pageable pageable) {
		if (Objects.isNull(linkedEntityClass)) {
			return null;
		}
		String fieldName = ReflectionUtil.getFieldNameWithAssignableType(getEntityClass(), linkedEntityClass);
		criteria = (criteria != null && criteria.length() > 0) ? criteria + " and " : "";
		criteria += "(" + fieldName + ".id:" + parentId + ")";
		return filter(criteria, pageable);
	}


	default Slice<BaseView> getEntitiesFromView(String criteria, String viewCriteria, Pageable pageable, Optional<String> optionalSliceSelect, Class viewClass) {
		BaseRepository jpaRepository = (BaseRepository) ApplicationContextHolder.getJpaRepository(this.getEntityClass());
		return jpaRepository.getEntitiesFromView(criteria, viewCriteria, pageable, this.getEntityClass(), optionalSliceSelect, viewClass);
	}

	default Slice<BaseView> getEntitiesFromViewFilterOnEntity(String criteria, Pageable pageable,
												Optional<String> optionalSliceSelect, Class viewClass) {
		return this.getEntitiesFromView(criteria, "", pageable, optionalSliceSelect, viewClass);
	}

	default Slice<BaseView> getEntitiesFromView(String viewCriteria, Pageable pageable, Optional<String> optionalSliceSelect, Class viewClass) {
		BaseRepository jpaRepository = (BaseRepository) ApplicationContextHolder.getJpaRepository(this.getEntityClass());
		return jpaRepository.getEntitiesFromView(viewCriteria, pageable,  optionalSliceSelect, viewClass);
	}


	default StatusOverview getStatusOverview(String criteria, String viewCriteria, StatusInterface[] statuses, Optional<Class> viewClass) {
		BaseRepository jpaRepository = (BaseRepository) ApplicationContextHolder.getJpaRepository(this.getEntityClass());
		return jpaRepository.getStatusOverview(criteria, viewCriteria, this.getEntityClass(), statuses, viewClass);
	}

	default StatusOverview getStatusOverview(String viewCriteria, StatusInterface[] statuses,Class viewClass) {
		BaseRepository jpaRepository = (BaseRepository) ApplicationContextHolder.getJpaRepository(this.getEntityClass());
		return jpaRepository.getStatusOverview(viewCriteria, statuses, viewClass);
	}

	default List<StatusCountOverview> getStatusCountOverview(String criteria, String viewCriteria, StatusInterface[] statuses, Optional<Class> viewClass) {

		StatusOverview statusOverview = getStatusOverview(criteria, viewCriteria, statuses, viewClass);

		List<StatusCountOverview> statusCountOverviewList = new ArrayList<>();
		List<StatusOverview.StatusCount> statusCountsList = statusOverview.getStatusCounts();
		if (statusCountsList != null) {
			statusCountsList.stream().forEach(statusCount -> {
				StatusCountOverview statusCountOverview = new StatusCountOverview();
				statusCountOverview.setName(statusCount.getStatus());
				statusCountOverview.setLabel(statusCount.getStatusLabel());
				statusCountOverview.setValue(statusCount.getCount());
				StatusCountOverview.Series series = new StatusCountOverview.Series();
				series.setName("STATUS");
				series.setValue(statusCount.getCount());
				List<StatusCountOverview.Series> seriesList = new ArrayList<>();
				seriesList.add(series);
				statusCountOverview.setSeries(seriesList);
				statusCountOverviewList.add(statusCountOverview);
			});
		}
		return statusCountOverviewList;
	}


	default AggregateInterface getAggregate(long id, Class<AggregateInterface> aggregateInterfaceClass) {
		EntityServiceDefaultInterface entityServiceDefaultInterface = ApplicationContextHolder.getApplicationContext().getBean(this.getClass());

		BaseEntityInterface baseEntityInterface = entityServiceDefaultInterface.getEntityById(id);

		AggregateInterface aggregateInterface = (AggregateInterface) ReflectionUtil.createInstance(aggregateInterfaceClass);

		AggregateBuilderInterface aggregateBuilderInterface = ApplicationContextHolder.getApplicationContext().getBean(AggregateBuilderInterface.class);
		if (Objects.isNull(aggregateBuilderInterface))
			throw new InternalAssertionException("AggregateBuilder Bean not found");

		aggregateBuilderInterface.buildAggregate(aggregateInterface, baseEntityInterface, AggregateType.ENTITY);

		return aggregateInterface;
	}

	default List<BaseEntityInterface> mergeMultiple(ArrayNode arrayNode) {
		List<BaseEntityInterface> updatedEntities = new ArrayList<>();
		arrayNode.forEach(jsonNode -> {
			BaseEntityInterface oldIns = null;
			BaseEntityInterface newIns = null;
			try {
				oldIns = ObjectMapperUtil.getObjectMapper().treeToValue(jsonNode.get("oldIns"), getEntityClass());
				newIns = ObjectMapperUtil.getObjectMapper().treeToValue(jsonNode.get("newIns"), getEntityClass());
			}
			catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			oldIns.setId(Long.parseLong(String.valueOf(jsonNode.get("id"))));
			newIns.setId(Long.parseLong(String.valueOf(jsonNode.get("id"))));
			List<String> oldInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("oldIns"));
			List<String> newInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("newIns"));
			ServiceInterface serviceInterface = ApplicationContextHolder.getService(AnnotationUtils.findAnnotation(getEntityClass(), ServiceClass.class).value());
			if (!EntityServiceDefaultInterface.class.isAssignableFrom(serviceInterface.getClass()))
				throw new InternalAssertionException("Service must implement EntityServiceDefaultInterface");
			EntityServiceDefaultInterface entityServiceDefaultInterface = (EntityServiceDefaultInterface) serviceInterface;

			BaseEntityInterface entity = entityServiceDefaultInterface.merge(newIns, oldIns, oldInsChangedAttributeNames, newInsChangedAttributeNames);
			updatedEntities.add(entity);
		});
		return updatedEntities;
	}

	default LinkedItems getLinkedItems(Long id, List<LinkedItems.LinkCategory> linkCategories) {
		ServiceInterface serviceInterface = ApplicationContextHolder.getService(AnnotationUtils.findAnnotation(getEntityClass(), ServiceClass.class).value());
		if (!EntityServiceDefaultInterface.class.isAssignableFrom(serviceInterface.getClass()))
			throw new InternalAssertionException("Service must implement EntityServiceDefaultInterface");
		EntityServiceDefaultInterface entityServiceDefaultInterface = (EntityServiceDefaultInterface) serviceInterface;
		BaseEntityInterface baseEntity = entityServiceDefaultInterface.getEntityById(id);
		LinkedItems linkedItems = createLinkedItems(linkCategories);
		List<LinkedItems.LinkCategory> categories = linkedItems.getCategories();
		List<ContextInterface> contexts = baseEntity.getContextsAsContextInterface();
		if (Objects.isNull(contexts)) {
			contexts = new ArrayList<>();
		}
		contexts.forEach(context -> {
			LinkedItems.LinkItem linkItem = new LinkedItems.LinkItem();
			linkItem.setId(context.getContextId());
			linkItem.setTitle(context.getName());
			linkItem.setType(context.getType());
			Optional<LinkedItems.LinkCategory> linkCategory = categories.stream().filter(category -> category.getName().toUpperCase().equals(context.getType())).findFirst();
			if (linkCategory.isPresent()) {
				Optional<LinkedItems.LinkCategory> finalLinkCategory = linkCategory;
				linkCategory.get().getSubCategories().stream().forEach(subCategory -> {
					subCategory.getItems().add(linkItem);
					finalLinkCategory.get().setTotalItems(finalLinkCategory.get().getTotalItems() + 1);
				});
			}
		});
		linkedItems.setCategories(categories);
		return linkedItems;
	}

	default LinkedItems createLinkedItems(List<LinkedItems.LinkCategory> linkCategories) {
		LinkedItems linkedItems = new LinkedItems();
		List<LinkedItems.LinkCategory> categories = new ArrayList<>();
		int categoryIndex = 0;
		for (LinkedItems.LinkCategory linkCategory : linkCategories) {
			categories.add(categoryIndex, new LinkedItems.LinkCategory(linkCategory.getName(), linkCategory.getLabel()));
			categoryIndex++;
		}
		linkedItems.setCategories(categories);
		return linkedItems;
	}

	default LinkedItems getLinkedItems(Long id) {
		ServiceInterface serviceInterface = ApplicationContextHolder.getService(AnnotationUtils.findAnnotation(getEntityClass(), ServiceClass.class).value());
		if (!EntityServiceDefaultInterface.class.isAssignableFrom(serviceInterface.getClass()))
			throw new InternalAssertionException("Service must implement EntityServiceDefaultInterface");
		EntityServiceDefaultInterface entityServiceDefaultInterface = (EntityServiceDefaultInterface) serviceInterface;
		BaseEntityInterface baseEntity = entityServiceDefaultInterface.getEntityById(id);
		LinkedItems linkedItems = new LinkedItems();
		List<LinkedItems.LinkCategory> categories = new ArrayList<>();
		List<String> contextTypes = new ArrayList<>();
		baseEntity.getContextsAsContextInterface().forEach(context -> {
			LinkedItems.LinkItem linkItem = new LinkedItems.LinkItem();
			linkItem.setId(context.getContextId());
			linkItem.setTitle(context.getName());
			linkItem.setType(context.getType());
			if (contextTypes.contains(context.getType())) {
				Optional<LinkedItems.LinkCategory> linkCategory = categories.stream().filter(category -> category.getName().equals(context.getType())).findFirst();
				if (linkCategory.isPresent()) {
					Optional<LinkedItems.LinkCategory> finalLinkCategory = linkCategory;
					linkCategory.get().getSubCategories().stream().forEach(subCategory -> {
						subCategory.getItems().add(linkItem);
						finalLinkCategory.get().setTotalItems(finalLinkCategory.get().getTotalItems() + 1);
					});
				}
			}
			else {
				LinkedItems.LinkCategory linkCategory = new LinkedItems.LinkCategory();
				LinkedItems.LinkSubCategory linkSubCategory = new LinkedItems.LinkSubCategory();
				List<LinkedItems.LinkSubCategory> subCategories = new ArrayList<>();
				linkCategory.setName(context.getType());
				linkSubCategory.setItems(new ArrayList<>());
				linkSubCategory.getItems().add(linkItem);
				linkCategory.setTotalItems(1);
				subCategories.add(linkSubCategory);
				linkCategory.setSubCategories(subCategories);
				categories.add(linkCategory);
			}
			contextTypes.add(context.getType());
		});
		linkedItems.setCategories(categories);
		return linkedItems;
	}
}
