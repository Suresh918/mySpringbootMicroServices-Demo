package com.example.mirai.libraries.security.core.aspect;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.example.mirai.libraries.core.annotation.*;
import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.exception.EntityIdNotFoundException;
import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.core.exception.UnauthorizedException;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.CaseAction;
import com.example.mirai.libraries.core.model.EntityLink;
import com.example.mirai.libraries.core.service.EntityResolverInterface;
import com.example.mirai.libraries.core.service.ServiceInterface;
import com.example.mirai.libraries.security.core.service.SecurityServiceDefaultInterface;
import com.example.mirai.libraries.util.ReflectionUtil;
import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

@Aspect
@Component
@AllArgsConstructor
public class PolicyEnforcementPoint {

	private final EntityResolverInterface entityResolverInterfaceImpl;

	@Around("@annotation(securePropertyRead)")
	public Object processAfterSecurePropertyRead(ProceedingJoinPoint point, SecurePropertyRead securePropertyRead) throws Throwable {
		Object retval = point.proceed();

		if (!(retval instanceof BaseEntityList) && !Collection.class.isAssignableFrom(retval.getClass()) && !AggregateInterface.class.isAssignableFrom(retval.getClass())) {
			ServiceInterface serviceInterface = entityResolverInterfaceImpl.getService(retval.getClass());
			Long entityId = getEntityId(point, Optional.ofNullable(retval));
			if (!(SecurityServiceDefaultInterface.class.isAssignableFrom(serviceInterface.getClass())))
				throw new InternalAssertionException("CANNOT USE SecurePropertyRead ANNOTATION ON CLASS THAT DOES NOT IMPLEMENT SecurityAwareServiceInterface");
			SecurityServiceDefaultInterface securityAwareService = (SecurityServiceDefaultInterface) serviceInterface;
			AclReferenceEntity aclReferenceEntity = AnnotationUtils.findAnnotation(retval.getClass(), AclReferenceEntity.class);
			if (aclReferenceEntity != null) {
				BaseEntityInterface referenceEntity = getAclReferenceEntity((BaseEntityInterface) retval, aclReferenceEntity.value());
				entityId = referenceEntity.getId();
				securityAwareService = (SecurityServiceDefaultInterface) entityResolverInterfaceImpl.getService(referenceEntity.getClass());
			}
			HashSet<String> unreadablePropertiesRegexps = securityAwareService.getPropertiesRegexps(entityId, "UNREADABLE");
			ReflectionUtil.nullifyFieldsMatchingRegexps(retval, unreadablePropertiesRegexps);
		}
		else if (retval instanceof BaseEntityList) {
			Collection collectionOfEntities = ((BaseEntityList) retval).getResults();
			for (Object entity : collectionOfEntities) {
				ServiceInterface serviceInterface = entityResolverInterfaceImpl.getService(entity.getClass());
				if (!(SecurityServiceDefaultInterface.class.isAssignableFrom(serviceInterface.getClass())))
					throw new InternalAssertionException("CANNOT USE SecurePropertyRead ANNOTATION ON CLASS THAT DOES NOT IMPLEMENT SecurityAwareServiceInterface");
				BaseEntityInterface baseEntity = (BaseEntityInterface) entity;
				final Long entityId = baseEntity.getId();
				SecurityServiceDefaultInterface securityAwareService = (SecurityServiceDefaultInterface) serviceInterface;
				HashSet<String> unreadablePropertiesRegexps = securityAwareService.getPropertiesRegexps(entityId, "UNREADABLE");
				ReflectionUtil.nullifyFieldsMatchingRegexps(baseEntity, unreadablePropertiesRegexps);
			}
		}
		else if (!(retval instanceof BaseEntityList) && Collection.class.isAssignableFrom(retval.getClass())) {
			Collection collectionOfEntities = (Collection) retval;
			for (Object entity : collectionOfEntities) {
				this.handleEntity(entity);
			}
		}
		else if (retval instanceof AggregateInterface) {
			String aggregateRootFieldName = ReflectionUtil.getSoleFieldNameWithAnnotation(retval.getClass(), AggregateRoot.class);
			BaseEntityInterface aggregateRootObject = (BaseEntityInterface) ReflectionUtil.getFieldValue(retval, aggregateRootFieldName);
			if (Objects.nonNull(aggregateRootObject)) {
				this.handleEntity(aggregateRootObject);
				this.handleAggregates(retval, aggregateRootObject);
			}
		}
		return retval;
	}

	//also check if service implements SecurityAwareServiceInterface
	@Before("@annotation(securePropertyMerge)")
	public void processBeforeSecurePropertyMerge(JoinPoint point, SecurePropertyMerge securePropertyMerge) throws Throwable {
		Long entityId = getEntityId(point, Optional.empty());
		final Class serviceClass = point.getTarget().getClass();
		SecurityServiceDefaultInterface securityAwareService = (SecurityServiceDefaultInterface) ApplicationContextHolder.getApplicationContext().getBean(serviceClass);
		EntityClass entityClassAnnotation = AnnotationUtils.findAnnotation(serviceClass, EntityClass.class);
		Class entityClass = Objects.requireNonNull(entityClassAnnotation).value();
		AclReferenceEntity aclReferenceEntity = AnnotationUtils.findAnnotation(entityClass, AclReferenceEntity.class);
		if (aclReferenceEntity != null) {
			BaseEntityInterface entity = ((ServiceInterface) point.getTarget()).getEntityById(entityId);
			BaseEntityInterface referenceEntity = getAclReferenceEntity(entity, aclReferenceEntity.value());
			if (Objects.nonNull(referenceEntity)) {
				entityId = referenceEntity.getId();
				securityAwareService = (SecurityServiceDefaultInterface) entityResolverInterfaceImpl.getService(referenceEntity.getClass());
			}
		}
		HashSet<String> unupdateablePropertiesRegexps = securityAwareService.getPropertiesRegexps(entityId, "UNUPDATEABLE");
		if (point.getArgs().length > 2 && point.getArgs()[2] instanceof ArrayList) {
			ArrayList<Object> oldChangedAttrs = (ArrayList<Object>) point.getArgs()[2];

			oldChangedAttrs.stream().forEach(field -> unupdateablePropertiesRegexps.forEach(unupdateablePropertiesRegexp -> {
				Pattern pattern = Pattern.compile(unupdateablePropertiesRegexp);
				Matcher matcher = pattern.matcher((String) field);
				if (matcher.find()) {

					throw new UnauthorizedException();
				}
			}));
		}
	}


	@Before("@annotation(secureLinkedEntityCaseAction)")
	public void processBeforeSecureLinkedEntityCaseAction(JoinPoint point, SecureLinkedEntityCaseAction secureLinkedEntityCaseAction) {
		final Set<EntityLink<BaseEntityInterface>> entityLinks = getLinkedEntities(point, secureLinkedEntityCaseAction.links());
		this.checkForSecureLinkedEntityCaseAction(entityLinks, secureLinkedEntityCaseAction.caseAction());
	}

	public void checkForSecureLinkedEntityCaseAction(Set<EntityLink<BaseEntityInterface>> entityLinks, String operation) {
		entityLinks.forEach(entityLink -> {
			Class linkedEntityClass = entityLink.getEClass();
			Long linkedEntityId = entityLink.getId();
			ServiceClass serviceClass = AnnotationUtils.findAnnotation(linkedEntityClass, ServiceClass.class);
			Class linkedEntityServiceClass = serviceClass.value();
			SecurityServiceDefaultInterface linkedSecurityAwareService = (SecurityServiceDefaultInterface) ApplicationContextHolder.getApplicationContext().getBean(linkedEntityServiceClass);
			Set<CaseAction> caseActions = linkedSecurityAwareService.getCaseActions(linkedEntityId);
			Optional<CaseAction> caseActionAvailable = caseActions.stream().filter(caseAction -> caseAction.getCaseAction().equals(operation)).findFirst();
			if (!caseActionAvailable.isPresent() || !caseActionAvailable.get().getIsAllowed())
				throw new UnauthorizedException();
		});
	}

	@Before("@annotation(secureCaseAction)")
	public void processBeforeCaseAction(JoinPoint point, SecureCaseAction secureCaseAction) {
		final Class serviceClass = point.getTarget().getClass();
		EntityClass entityClassAnnotation = AnnotationUtils.findAnnotation(serviceClass, EntityClass.class);
		Class entityClass = Objects.requireNonNull(entityClassAnnotation).value();
		AclReferenceEntity aclReferenceEntity = AnnotationUtils.findAnnotation(entityClass, AclReferenceEntity.class);
		Long entityId = getEntityId(point, Optional.empty());
		SecurityServiceDefaultInterface securityAwareService = (SecurityServiceDefaultInterface) ApplicationContextHolder.getApplicationContext().getBean(serviceClass);
		if (aclReferenceEntity != null) {
			BaseEntityInterface entity = ((ServiceInterface) point.getTarget()).getEntityById(entityId);
			BaseEntityInterface referenceEntity = getAclReferenceEntity(entity, aclReferenceEntity.value());
			entityId = referenceEntity.getId();
			securityAwareService = (SecurityServiceDefaultInterface) entityResolverInterfaceImpl.getService(referenceEntity.getClass());
		}
		String operation = secureCaseAction.value();
		if (Objects.nonNull(entityId)) {
			Set<CaseAction> caseActions = securityAwareService.getCaseActions(entityId);
			Optional<CaseAction> caseActionAvailable = caseActions.stream().filter(caseAction -> caseAction.getCaseAction().equals(operation)).findFirst();
			if (!caseActionAvailable.isPresent() || !caseActionAvailable.get().getIsAllowed())
				throw new UnauthorizedException();
		}
		else {
			Set<CaseAction> authorizedCaseActions = securityAwareService.getCaseActions(entityClass);
			Optional<CaseAction> caseActionAvailable = authorizedCaseActions.stream().filter(caseAction -> caseAction.getCaseAction().equals(operation)).findFirst();
			if (!caseActionAvailable.isPresent() || !caseActionAvailable.get().getIsAllowed())
				throw new UnauthorizedException();

		}
	}

	@Around("@annotation(secureFetchAction)")
	public Object processBeforeSecureFetch(ProceedingJoinPoint point, SecureFetchAction secureFetchAction) throws Throwable {
		final Class serviceClass = point.getTarget().getClass();
		ServiceInterface serviceInterface = (ServiceInterface) ApplicationContextHolder.getApplicationContext().getBean(serviceClass);
		Class entityClass = entityResolverInterfaceImpl.getEntityClass(serviceClass);

		if (!(SecurityServiceDefaultInterface.class.isAssignableFrom(serviceInterface.getClass())))
			throw new InternalAssertionException("CANNOT USE SecurePropertyRead ANNOTATION ON CLASS THAT DOES NOT IMPLEMENT SecurityAwareServiceInterface");
		SecurityServiceDefaultInterface securityAwareService = (SecurityServiceDefaultInterface) serviceInterface;

		Set<String> fetchRules = securityAwareService.getFetchRulesByAuthorizedCaseAction(entityClass, "FETCH");
		Set<String> fetchViewRules = securityAwareService.getFetchViewRulesByAuthorizedCaseAction(entityClass, "FETCH");

		if (fetchRules.isEmpty() && fetchViewRules.isEmpty()) {
			throw new UnauthorizedException();
		}
		List<String> roles = securityAwareService.getRoles(securityAwareService.getPrincipal()).stream().collect(Collectors.toList());

		String generatedCriteria = fetchRules.stream().filter(Objects::nonNull).map(fetchRule -> {
			String rule = "( " + fetchRule.replace("${loggedInUser}", securityAwareService.getPrincipal()) + " )";
			return rule.replace("${loggedInUserGroups}", String.join("|", roles));
		}).collect(Collectors.joining(" OR "));
		String generatedViewCriteria = fetchViewRules.stream().filter(Objects::nonNull).map(fetchRule -> {
			String rule = "( " + fetchRule.replace("${loggedInUser}", securityAwareService.getPrincipal()) + " )";
			return rule.replace("${loggedInUserGroups}", String.join("|", roles));
		}).collect(Collectors.joining(" OR "));

		if ((Objects.isNull(generatedCriteria) || generatedCriteria.length() == 0) && (Objects.isNull(generatedViewCriteria) || generatedViewCriteria.length() == 0)) {
			return point.proceed();
		}
		MethodSignature signature = (MethodSignature) point.getSignature();
		String methodName = signature.getMethod().getName();
		Class<?>[] parameterTypes = signature.getMethod().getParameterTypes();
		Annotation[][] annotations = point.getTarget().getClass().getMethod(methodName, parameterTypes).getParameterAnnotations();
		Set<Integer> criteriaIndices = IntStream.range(0, annotations.length)
				.filter(i -> {
					Optional<Annotation> foundSecureFetchCriteria = Arrays.stream(annotations[i]).filter(annotation -> annotation.annotationType().equals(SecureFetchCriteria.class)).findFirst();
					return foundSecureFetchCriteria.isPresent();
				})
				.mapToObj(i -> i)
				.collect(Collectors.toSet());

		Set<Integer> viewCriteriaIndices = IntStream.range(0, annotations.length).filter(i -> Arrays.stream(annotations[i])
				.filter(annotation -> annotation.annotationType().equals(SecureFetchViewCriteria.class)).findFirst().isPresent()).mapToObj(i -> i)
				.collect(Collectors.toSet());
		if (criteriaIndices.size() > 1) {
			throw new InternalAssertionException("METHOD ANNOTATED WITH SecureFetchAction CAN HAVE AT MOST 1 PARAMETER ANNOTATED WITH SecureFetchCriteria");
		}
		if (viewCriteriaIndices.size() > 1) {
			throw new InternalAssertionException("METHOD ANNOTATED WITH SecureFetchAction CAN HAVE AT MOST 1 PARAMETER ANNOTATED WITH SecureFetchViewCriteria");
		}
		Optional<Integer> foundCriteriaIndex = criteriaIndices.stream().findFirst();
		Object criteria =null;
		Object[] args = point.getArgs();
		if (!foundCriteriaIndex.isEmpty()) {
			criteria = args[foundCriteriaIndex.get().intValue()];
			if (!(criteria instanceof String)) {
				throw new InternalAssertionException("SecureFetchCriteria CAN ONLY BE USED ON STRING TYPE PARAMETER");
			}
		}
		Optional<Integer> foundViewCriteriaIndex = null;
		if (!viewCriteriaIndices.isEmpty()) {
			foundViewCriteriaIndex = viewCriteriaIndices.stream().findFirst();
			Object viewCriteria = args[foundViewCriteriaIndex.get().intValue()];
			if (!(viewCriteria instanceof String)) {
				throw new InternalAssertionException("SecureFetchViewCriteria CAN ONLY BE USED ON STRING TYPE PARAMETER");
			}
		}

		if (Objects.isNull(criteria) || ((String) criteria).length() == 0) {
			if (foundCriteriaIndex.isPresent())
				args[foundCriteriaIndex.get().intValue()] = generatedCriteria;
			addFetchViewCriteria(args, foundViewCriteriaIndex, generatedViewCriteria);
			return point.proceed(args);
		}
		else if (Objects.nonNull(criteria) && ((String) criteria).length() > 0) {
			if (Objects.nonNull(generatedCriteria) && generatedCriteria.length() > 0)
				args[foundCriteriaIndex.get().intValue()] = "( " + criteria + " )" + " AND " + "( " + generatedCriteria + " )";
			addFetchViewCriteria(args, foundViewCriteriaIndex, generatedViewCriteria);
			return point.proceed(args);
		}
		else {
			addFetchViewCriteria(args, foundViewCriteriaIndex, generatedViewCriteria);
			return point.proceed();
		}
	}

	private void addFetchViewCriteria(Object[] args, Optional<Integer> foundViewCriteriaIndex, String generatedViewCriteria) {
		if (Objects.isNull(foundViewCriteriaIndex))
			return;
		String viewCriteria = (String) args[foundViewCriteriaIndex.get().intValue()];
		if (Objects.isNull(viewCriteria) || viewCriteria.length() == 0) {
			args[foundViewCriteriaIndex.get().intValue()] = generatedViewCriteria;
		}
		else if (Objects.nonNull(viewCriteria) && viewCriteria.length() > 0 && Objects.nonNull(generatedViewCriteria) && generatedViewCriteria.length() > 0) {
			args[foundViewCriteriaIndex.get().intValue()] = "( " + viewCriteria + " )" + " AND " + "( " + generatedViewCriteria + " )";
		}
	}

	private Long getEntityType(JoinPoint point) {
		return null;
	}

	//TODO this method assumes that the first argument will be either id or entity, what if second one is id or entity
	private Long getEntityId(JoinPoint point, Optional<Object> retVal) {
		Long entityId;
		if (point.getArgs()[0] instanceof Long)
			entityId = (Long) point.getArgs()[0];
		else if (point.getArgs()[0] instanceof BaseEntityInterface) {
			entityId = ((BaseEntityInterface) point.getArgs()[0]).getId();
		}
		else if (point.getArgs()[0] instanceof AggregateInterface) {
			entityId = getEntityIdForAggregateInterface((AggregateInterface) point.getArgs()[0]);
		}
		else if (retVal.isPresent() && retVal.get() instanceof BaseEntityInterface) {
			entityId = ((BaseEntityInterface) retVal.get()).getId();
		}
		else if (retVal.isPresent() && retVal.get() instanceof AggregateInterface) {
			entityId = getEntityIdForAggregateInterface((AggregateInterface) retVal.get());
		}
		else {
			throw new EntityIdNotFoundException();
		}
		return entityId;
	}


	private Long getEntityIdForAggregateInterface(AggregateInterface aggregate) {
		String fieldWithAggregateRoot = ReflectionUtil.getSoleFieldNameWithAnnotation(aggregate.getClass(), AggregateRoot.class);
		if (fieldWithAggregateRoot != null) {
			return ((BaseEntityInterface) ReflectionUtil.getFieldValue(aggregate, fieldWithAggregateRoot)).getId();
		}
		String fieldWithLinkTo = ReflectionUtil.getSoleFieldNameWithAnnotation(aggregate.getClass(), LinkTo.class);
		if (fieldWithLinkTo != null) {
			return ((BaseEntityInterface) ReflectionUtil.getFieldValue(aggregate, fieldWithLinkTo)).getId();
		}
		return null;
	}

	//TODO this method assumes that the second argument will be Set<EntityLink<BaseEntityInterface>>, what if second one is id or entity
	private Set<EntityLink<BaseEntityInterface>> getLinkedEntities(JoinPoint point, Class[] links) {
		Set<EntityLink<BaseEntityInterface>> entityLinks;
		if (point.getArgs().length >= 2 && point.getArgs()[1] instanceof Set) {
			Set<Object> tempEntityLinks = (Set<Object>) point.getArgs()[1];
			tempEntityLinks.forEach(entityLink -> {
				if (!(entityLink instanceof EntityLink)) {
					throw new EntityIdNotFoundException();
				}
			});
			entityLinks = (Set<EntityLink<BaseEntityInterface>>) point.getArgs()[1];
			if (links.length > 0) {
				Set<EntityLink<BaseEntityInterface>> definedEntityLinks = new HashSet<>();
				tempEntityLinks.stream().forEach(entity -> {
					if (Arrays.asList(links).contains(((EntityLink) entity).getEClass())) {
						definedEntityLinks.add((EntityLink) entity);
					}
				});
				entityLinks = definedEntityLinks;
			}
		}
		else {
			throw new InternalAssertionException("method arguments do not match the required condition");
		}
		return entityLinks;
	}

	private void handleAggregates(Object retval, Object aggregateRootObject) {
		List<String> aggregateFieldNames = ReflectionUtil.getFieldNamesWithAnnotation(retval.getClass(), Aggregate.class);
		for (String aggregateFieldName : aggregateFieldNames) {
			Object aggregateObject = ReflectionUtil.getFieldValue(retval, aggregateFieldName);
			if (Objects.nonNull(aggregateObject)) {
				if (Collection.class.isAssignableFrom(aggregateObject.getClass())) {
					for (Object aggregateItem : (Collection) aggregateObject) {
						this.handleAggregates(aggregateItem, aggregateRootObject);
					}
				}
				else {
					this.handleAggregates(aggregateObject, aggregateRootObject);
				}
			}
		}
		List<String> linkToFieldNames = ReflectionUtil.getFieldNamesWithAnnotation(retval.getClass(), LinkTo.class);
		for (String linkToFieldName : linkToFieldNames) {
			BaseEntityInterface linkToObject = (BaseEntityInterface) ReflectionUtil.getFieldValue(retval, linkToFieldName);
			this.handleEntity(linkToObject);
		}
	}

	private void handleEntity(Object entity) {
		ServiceInterface serviceInterface = null;
		AclReferenceEntity aclReferenceEntity = AnnotationUtils.getAnnotation(entity.getClass(), AclReferenceEntity.class);
		BaseEntityInterface referenceEntity = null;
		if (aclReferenceEntity != null) {
			Class referenceEntityClass = aclReferenceEntity.value();
			referenceEntity = getAclReferenceEntity((BaseEntityInterface) entity, referenceEntityClass);
			serviceInterface = entityResolverInterfaceImpl.getService(referenceEntityClass);
		}
		else {
			serviceInterface = entityResolverInterfaceImpl.getService(entity.getClass());
		}
		if (!(SecurityServiceDefaultInterface.class.isAssignableFrom(serviceInterface.getClass())))
			throw new InternalAssertionException("CANNOT USE SecurePropertyRead ANNOTATION ON CLASS THAT DOES NOT IMPLEMENT SecurityAwareServiceInterface");
		BaseEntityInterface baseEntity = referenceEntity != null ? referenceEntity : (BaseEntityInterface) entity;
		// final Long entityId = baseEntity.getId();
		SecurityServiceDefaultInterface securityAwareService = (SecurityServiceDefaultInterface) serviceInterface;
		HashSet<String> unreadablePropertiesRegexps = securityAwareService.getPropertiesRegexps(baseEntity, "UNREADABLE");
		ReflectionUtil.nullifyFieldsMatchingRegexps(entity, unreadablePropertiesRegexps);
	}

	private BaseEntityInterface getAclReferenceEntity(BaseEntityInterface entity, Class referenceEntityClass) {
		String referenceFieldName = ReflectionUtil.getFieldNameWithType(entity.getClass(), referenceEntityClass);
		if (Objects.nonNull(referenceFieldName)) {
			return (BaseEntityInterface) ReflectionUtil.getFieldValue(entity, referenceFieldName);
		}
		return getAclReferenceEntityRecursively(entity, referenceEntityClass);
	}

	private BaseEntityInterface getAclReferenceEntityRecursively(BaseEntityInterface entity, Class referenceEntityClass) {
		List<Class> relatedEntityFieldTypes = new ArrayList<>();
		relatedEntityFieldTypes.addAll(ReflectionUtil.getFieldTypesWithAnnotation(entity.getClass(), OneToMany.class));
		relatedEntityFieldTypes.addAll(ReflectionUtil.getFieldTypesWithAnnotation(entity.getClass(), OneToOne.class));
		relatedEntityFieldTypes.addAll(ReflectionUtil.getFieldTypesWithAnnotation(entity.getClass(), ManyToOne.class));
		for (Class fieldType : relatedEntityFieldTypes) {
			String referenceFieldName = ReflectionUtil.getFieldNameWithType(fieldType, referenceEntityClass);
			if (Objects.nonNull(referenceFieldName)) {
				BaseEntityInterface linkedEntity = (BaseEntityInterface) ReflectionUtil.getFieldValueByType(entity, fieldType);
				Object referenceEntity = ReflectionUtil.getFieldValue(linkedEntity, referenceFieldName);
				if (Objects.nonNull(referenceEntity)) {
					return (BaseEntityInterface) referenceEntity;
				}
			}
		}
		for (Class fieldType : relatedEntityFieldTypes) {
			BaseEntityInterface baseEntityInterface = (BaseEntityInterface) ReflectionUtil.getFieldValueByType(entity, fieldType);
			return getAclReferenceEntityRecursively(baseEntityInterface, referenceEntityClass);
		}
		return null;
	}

}
