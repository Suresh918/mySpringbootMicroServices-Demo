package com.example.mirai.libraries.entity.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CompoundSelection;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.example.mirai.libraries.core.annotation.FieldName;
import com.example.mirai.libraries.core.annotation.JoinKey;
import com.example.mirai.libraries.core.annotation.ViewMapper;
import com.example.mirai.libraries.core.model.BaseView;
import com.example.mirai.libraries.core.model.StatusInterface;
import com.example.mirai.libraries.entity.model.CustomPageRequest;
import com.example.mirai.libraries.entity.model.Id;
import com.example.mirai.libraries.entity.model.StatusOverview;
import com.example.mirai.libraries.entity.service.helper.filter.GenericSpecificationsBuilder;
import com.example.mirai.libraries.util.ReflectionUtil;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;

@Repository
public class BaseRepositoryImpl<E> implements BaseRepository<E> {
	@PersistenceContext
	EntityManager entityManager;

	//TODO refactor to include jpaentityinformation and extract entity name from there instead of passing here
	@Override
	public Slice<Id> getIds(String criteria, Optional<String> viewCriteria, Pageable pageable, Class<E> entityClass, Optional<String> optionalSliceSelect, Optional<Class> viewClass) {
		GenericSpecificationsBuilder specBuilder = new GenericSpecificationsBuilder<>();
		Specification<E> specification = specBuilder.generateSpecification(criteria);
		Specification<E> viewSpecification = null;
		if (viewCriteria.isPresent()) {
			viewSpecification = specBuilder.generateSpecification(viewCriteria.get());
		}

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(entityClass);
		Root<E> fromEntity = criteriaQuery.from(entityClass);
		Root<E> fromView = null;
		String viewJoinKeyName = null;
		Predicate conditionPredicate = null;
		Predicate viewConditionPredicate = null;
		Predicate compoundPredicate = null;

		// creating new pageable to convert keys from snake case to camel case
		pageable = CustomPageRequest.getCustomPageRequest(pageable);
		if (viewClass.isPresent()) {
			fromView = criteriaQuery.from(viewClass.get());
			List<String> joinKeys = ReflectionUtil.getFieldNamesWithAnnotation(viewClass.get(), JoinKey.class);
			viewJoinKeyName = joinKeys.get(0);

			Predicate joinPredicate = criteriaBuilder.equal(fromEntity.get("id"), fromView.get(viewJoinKeyName));
			if (specification != null) {
				conditionPredicate = specification.toPredicate(fromEntity, criteriaQuery, criteriaBuilder);
				compoundPredicate = criteriaBuilder.and(joinPredicate, conditionPredicate);
			}
			else {
				compoundPredicate = joinPredicate;
			}
			if (viewSpecification != null) {
				viewConditionPredicate = viewSpecification.toPredicate(fromView, criteriaQuery, criteriaBuilder);
				compoundPredicate = criteriaBuilder.and(compoundPredicate, viewConditionPredicate);
			}
		}
		else if (specification != null) {
			compoundPredicate = specification.toPredicate(fromEntity, criteriaQuery, criteriaBuilder);
		}

		CompoundSelection<Id> construct = criteriaBuilder.construct(Id.class, fromEntity.get("id"));

		CriteriaQuery<Id> select = (CriteriaQuery<Id>) criteriaQuery.select((Selection<? extends E>) construct);

		if (compoundPredicate != null)
			select.where(compoundPredicate);

		String sliceSelect = optionalSliceSelect.orElse("");
		boolean selectResult = ("," + sliceSelect + ",").toUpperCase().contains(",RESULTS,");
		boolean selectTotalResults = ("," + sliceSelect + ",").toUpperCase().contains(",TOTAL-RESULTS,");

		int firstResult = pageable.getPageNumber() <= 0 ? 0 : pageable.getPageSize() * pageable.getPageNumber();
		int maxResults = pageable.getPageSize() + 1; //adding 1 to peek into the next page to populate hasNext of slice

		List<Id> listOfIds = new ArrayList<>();
		boolean sliceSelectExists = sliceSelect.length() > 0;
		if (selectResult || !sliceSelectExists) {
			select.orderBy(QueryUtils.toOrders(pageable.getSort(), fromEntity, criteriaBuilder));
			TypedQuery typedQuery = entityManager.createQuery(select);
			typedQuery.setFirstResult(firstResult);
			typedQuery.setMaxResults(maxResults);

			listOfIds = typedQuery.getResultList();
		}

		Long totalResults = null;
		if (selectTotalResults || !sliceSelectExists) {
			CriteriaQuery<Long> countCriteriaQuery = criteriaBuilder.createQuery(Long.class);
			Root<E> fromEntityForCount = countCriteriaQuery.from(entityClass);
			CriteriaQuery<Long> selectCount = null;
			if (viewClass.isEmpty()) {
				Predicate conditionPredicate1 = null;
				if (specification != null) {
					conditionPredicate1 = specification.toPredicate(fromEntityForCount, countCriteriaQuery, criteriaBuilder);

				}
				selectCount = countCriteriaQuery.select(criteriaBuilder.count(fromEntityForCount));
				if (conditionPredicate1 != null)
					selectCount.where(conditionPredicate1);
			}
			else {

				Root<E> fromViewForCount = countCriteriaQuery.from(viewClass.get());

				Predicate joinPredicateForCount = criteriaBuilder.equal(fromEntityForCount.get("id"), fromViewForCount.get(viewJoinKeyName));
				Predicate conditionPredicateForCount = null;
				Predicate viewConditionPredicateForCount = null;
				Predicate compoundPredicateForCount = null;
				if (specification != null) {
					conditionPredicateForCount = specification.toPredicate(fromEntityForCount, countCriteriaQuery, criteriaBuilder);
					compoundPredicateForCount = criteriaBuilder.and(joinPredicateForCount, conditionPredicateForCount);
				}
				else {
					compoundPredicateForCount = joinPredicateForCount;
				}
				if (viewSpecification != null) {
					viewConditionPredicateForCount = viewSpecification.toPredicate(fromViewForCount, countCriteriaQuery, criteriaBuilder);
					compoundPredicateForCount = criteriaBuilder.and(compoundPredicateForCount, viewConditionPredicateForCount);
				}
				selectCount = countCriteriaQuery.select(criteriaBuilder.count(fromEntityForCount));
				selectCount.where(compoundPredicateForCount);
			}
			totalResults = entityManager.createQuery(selectCount).getSingleResult();
		}

		int maxApplicableResult = listOfIds.size() == maxResults ? maxResults - 1 : listOfIds.size();

		if (!selectResult && selectTotalResults) {
			return new PageImpl<>(listOfIds, pageable, totalResults);
		}
		else if (selectResult && !selectTotalResults) {

			return new SliceImpl<>(listOfIds.subList(0, maxApplicableResult), pageable, listOfIds.size() > pageable.getPageSize());
		}
		else {
			return new PageImpl<>(listOfIds.subList(0, maxApplicableResult), pageable, totalResults);
		}
	}


	//TODO refactor to include jpaentityinformation and extract entity name from there instead of passing here
	public Slice<BaseView> getEntitiesFromView(String criteria, String viewCriteria, Pageable pageable, Class<E> entityClass,
			Optional<String> optionalSliceSelect, Class viewClass) {

		List<String> joinKeys = ReflectionUtil.getFieldNamesWithAnnotation(viewClass, JoinKey.class);

		String viewJoinKeyName = joinKeys.get(0);

		GenericSpecificationsBuilder specBuilder = new GenericSpecificationsBuilder<>();
		Specification<E> specification = specBuilder.generateSpecification(criteria);
		Specification<E> viewSpecification = specBuilder.generateSpecification(viewCriteria);

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(entityClass);
		Root<E> fromEntity = criteriaQuery.from(entityClass);
		Root<E> fromView = criteriaQuery.from(viewClass);

		Predicate joinPredicate = criteriaBuilder.equal(fromEntity.get("id"), fromView.get(viewJoinKeyName));
		Predicate conditionPredicate = null;
		Predicate viewConditionPredicate = null;
		Predicate compoundPredicate = null;
		if (specification != null) {
			conditionPredicate = specification.toPredicate(fromEntity, criteriaQuery, criteriaBuilder);
			compoundPredicate = criteriaBuilder.and(joinPredicate, conditionPredicate);
		}
		else {
			compoundPredicate = joinPredicate;
		}
		if (viewSpecification != null) {
			viewConditionPredicate = viewSpecification.toPredicate(fromView, criteriaQuery, criteriaBuilder);
			compoundPredicate = criteriaBuilder.and(compoundPredicate, viewConditionPredicate);
		}

		List<Path> selectedAttributes = getViewParameters(viewClass).stream().map(fromView::get).collect(Collectors.toList());
		Path[] x = new Path[selectedAttributes.size()];
		selectedAttributes.toArray(x);
		CompoundSelection<BaseView> construct = criteriaBuilder.construct(viewClass, x);
		// creating new pageable to convert keys from snake case to camel case
		pageable = CustomPageRequest.getCustomPageRequest(pageable);
		CriteriaQuery<E> select = criteriaQuery.select((Selection<? extends E>) construct);

		select.where(compoundPredicate);

		String sliceSelect = optionalSliceSelect.isPresent() ? optionalSliceSelect.get() : "";
		boolean selectResult = ("," + sliceSelect + ",").toUpperCase().contains(",RESULTS,");
		boolean selectTotalResults = ("," + sliceSelect + ",").toUpperCase().contains(",TOTAL-RESULTS,");

		int firstResult = pageable.getPageNumber() <= 0 ? 0 : pageable.getPageSize() * pageable.getPageNumber();
		int maxResults = pageable.getPageSize() + 1; //adding 1 to peek into the next page to populate hasNext of slice

		List<BaseView> listOfResults = new ArrayList<>();
		boolean sliceSelectExists = sliceSelect.length() > 0;
		if (selectResult || !sliceSelectExists) {
			select.orderBy(QueryUtils.toOrders(pageable.getSort(), fromEntity, criteriaBuilder));
			TypedQuery typedQuery = entityManager.createQuery(select);
			typedQuery.setFirstResult(firstResult);
			typedQuery.setMaxResults(maxResults);

			listOfResults = typedQuery.getResultList();
		}

		Long totalResults = null;
		if (selectTotalResults || !sliceSelectExists) {
			CriteriaBuilder criteriaBuilderForCount = entityManager.getCriteriaBuilder();
			CriteriaQuery<Long> criteriaQueryForCount = criteriaBuilderForCount.createQuery(Long.class);
			Root<E> fromEntityForCount = criteriaQueryForCount.from(entityClass);
			Root<E> fromViewForCount = criteriaQueryForCount.from(viewClass);

			Predicate joinPredicateForCount = criteriaBuilderForCount.equal(fromEntityForCount.get("id"), fromViewForCount.get(viewJoinKeyName));
			Predicate conditionPredicateForCount = null;
			Predicate viewConditionPredicateForCount = null;
			Predicate compoundPredicateForCount = null;
			if (specification != null) {
				conditionPredicateForCount = specification.toPredicate(fromEntityForCount, criteriaQueryForCount, criteriaBuilderForCount);
				compoundPredicateForCount = criteriaBuilderForCount.and(joinPredicateForCount, conditionPredicateForCount);
			}
			else {
				compoundPredicateForCount = joinPredicateForCount;
			}
			if (viewSpecification != null) {
				viewConditionPredicateForCount = viewSpecification.toPredicate(fromViewForCount, criteriaQueryForCount, criteriaBuilderForCount);
				compoundPredicateForCount = criteriaBuilder.and(compoundPredicateForCount, viewConditionPredicateForCount);
			}
			CriteriaQuery<Long> selectCount = criteriaQueryForCount.select(criteriaBuilderForCount.count(fromEntityForCount));
			selectCount.where(compoundPredicateForCount);
			totalResults = entityManager.createQuery(selectCount).getSingleResult();
		}


		int maxApplicableResult = listOfResults.size() == maxResults ? maxResults - 1 : listOfResults.size();

		if (!selectResult && selectTotalResults) {
			return new PageImpl<>(listOfResults, pageable, totalResults);
		}
		else if (selectResult && !selectTotalResults) {

			return new SliceImpl<>(listOfResults.subList(0, maxApplicableResult), pageable, listOfResults.size() > pageable.getPageSize());
		}
		else {
			return new PageImpl<>(listOfResults.subList(0, maxApplicableResult), pageable, totalResults);
		}
	}

	private List<String> getViewParameters(Class viewClass) {
		return ReflectionUtil.getParametersOfConstructorWithAnnotation(viewClass, ViewMapper.class, FieldName.class);
	}

	public Slice<BaseView> getEntitiesFromView(String viewCriteria, Pageable pageable,
												   Optional<String> optionalSliceSelect, Class viewClass) {
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();

		//Convert DQL into Specification
		GenericSpecificationsBuilder specBuilder = new GenericSpecificationsBuilder();
		Specification<BaseView> specification = specBuilder.generateSpecification(viewCriteria);

		//Generate Limit Clause
		pageable = CustomPageRequest.getCustomPageRequest(pageable);
		int firstResult = pageable.getPageNumber() <= 0 ? 0 : pageable.getPageSize() * pageable.getPageNumber();
		int maxResults = pageable.getPageSize() + 1; //adding 1 to peek into the next page to populate hasNext of slice

		String sliceSelect = optionalSliceSelect.isPresent() ? optionalSliceSelect.get() : "";
		boolean sliceSelectExists = sliceSelect.length() > 0;
		boolean selectResult = ("," + sliceSelect + ",").toUpperCase().contains(",RESULTS,");
		boolean selectTotalResults = ("," + sliceSelect + ",").toUpperCase().contains(",TOTAL-RESULTS,");

		List<BaseView> listOfResults = new ArrayList<>();
		if (selectResult || !sliceSelectExists) {
			//Initialize Query
			CriteriaQuery<BaseView> criteriaQuery = criteriaBuilder.createQuery(viewClass);

			//Generate From Clause
			Root<BaseView> fromEntity = criteriaQuery.from(viewClass);

			//Generate Select Clause
			List<String> params = ReflectionUtil.getParametersOfConstructorWithAnnotation(viewClass, ViewMapper.class, FieldName.class);
			Path[] selectedAttributes = params.stream().map(fromEntity::get).collect(Collectors.toList()).toArray(new Path[params.size()]);
			CompoundSelection<BaseView> construct = criteriaBuilder.construct(viewClass, selectedAttributes);

			//Generate Where Clause
			Predicate predicate = null;
			if(Objects.nonNull(specification))
				predicate = specification.toPredicate(fromEntity, criteriaQuery, criteriaBuilder);

			//Combine Select/From/Where Clauses
			CriteriaQuery<BaseView> selectRowsCriteriaQuery = criteriaQuery.select(construct);
			if(Objects.nonNull(predicate))
				selectRowsCriteriaQuery.where(predicate);

			//Add Order By Clause
			selectRowsCriteriaQuery.orderBy(QueryUtils.toOrders(pageable.getSort(), fromEntity, criteriaBuilder));

			//Add Limit Clause
			TypedQuery<BaseView> typedQuery = this.entityManager.createQuery(selectRowsCriteriaQuery);
			typedQuery.setFirstResult(firstResult);
			typedQuery.setMaxResults(maxResults);

			//Get Results
			listOfResults = typedQuery.getResultList();
		}

		Long totalResults = null;
		if (selectTotalResults || !sliceSelectExists) {
			//Initialize Query
			CriteriaQuery<Long> selectCountCriteriaQuery = criteriaBuilder.createQuery(Long.class);

			//Generate From Clause
			Root<BaseView> fromEntity = selectCountCriteriaQuery.from(viewClass);

			//Generate Select Clause
			selectCountCriteriaQuery.select(criteriaBuilder.count(fromEntity));

			//Generate Where Clause
			Predicate predicate = null;
			if(Objects.nonNull(specification))
				predicate = specification.toPredicate(fromEntity, selectCountCriteriaQuery, criteriaBuilder);

			//Combine Select Count/From/Where Clauses
			if(Objects.nonNull(predicate))
				selectCountCriteriaQuery.where(predicate);

			//Get Results
			totalResults = this.entityManager.createQuery(selectCountCriteriaQuery).getSingleResult();
		}

		int maxApplicableResult = listOfResults.size() == maxResults ? maxResults - 1 : listOfResults.size();
		if (!selectResult && selectTotalResults) {
			return new PageImpl(listOfResults, pageable, totalResults);
		} else {
			return (selectResult && !selectTotalResults ? new SliceImpl(listOfResults.subList(0, maxApplicableResult), pageable, listOfResults.size() > pageable.getPageSize()) : new PageImpl(listOfResults.subList(0, maxApplicableResult), pageable, totalResults));
		}
	}

	@Override
	public StatusOverview getStatusOverview(String criteria, String viewCriteria, Class<E> entityClass, StatusInterface[] statuses, Optional<Class> relatedViewClass) {
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		String statusField = "status";
		Class viewClass = null;
		if (relatedViewClass.isPresent()) {
			viewClass = relatedViewClass.get();
		}
		CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery(Object[].class);
		Root<E> fromEntity = criteriaQuery.from(entityClass);
		GenericSpecificationsBuilder specBuilder = new GenericSpecificationsBuilder();
		Predicate joinPredicate = null;
		Specification<E> viewSpecification = null;
		Root<E> fromView = null;
		criteriaQuery.multiselect(fromEntity.get(statusField), criteriaBuilder.count(fromEntity.get(statusField)));
		if (viewClass != null) {
			fromView = criteriaQuery.from(viewClass);
			List<String> joinKeys = ReflectionUtil.getFieldNamesWithAnnotation(viewClass, JoinKey.class);
			String viewJoinKeyName = joinKeys.get(0);
			joinPredicate = criteriaBuilder.equal(fromEntity.get("id"), fromView.get(viewJoinKeyName));
			criteriaQuery.multiselect(fromView.get(statusField), criteriaBuilder.count(fromView.get(statusField)));
			viewSpecification = specBuilder.generateSpecification(viewCriteria);
		}
		Specification<E> specification = specBuilder.generateSpecification(criteria);
		Predicate conditionPredicate = null;
		Predicate viewConditionPredicate = null;
		Predicate compoundPredicate = null;
		if (specification != null) {
			conditionPredicate = specification.toPredicate(fromEntity, criteriaQuery, criteriaBuilder);
			compoundPredicate = criteriaBuilder.and(joinPredicate, conditionPredicate);
		}
		else {
			compoundPredicate = joinPredicate;
		}
		if (viewSpecification != null) {
			viewConditionPredicate = viewSpecification.toPredicate(fromView, criteriaQuery, criteriaBuilder);
			compoundPredicate = criteriaBuilder.and(compoundPredicate, viewConditionPredicate);
		}
		if (compoundPredicate != null && viewClass != null) {
			criteriaQuery.where(compoundPredicate);
		}
		else if (conditionPredicate != null) {
			criteriaQuery.where(conditionPredicate);
		}

		criteriaQuery.groupBy(fromEntity.get(statusField));
		if (viewClass != null) {
			criteriaQuery.groupBy(fromView.get(statusField));
		}

		List<Object[]> list = this.entityManager.createQuery(criteriaQuery).getResultList();

		return new StatusOverview(list, statuses);
	}


	@Override
	public StatusOverview getStatusOverview(String viewCriteria, StatusInterface[] statuses, Class viewClass) {
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();

		//Convert DQL into Specification
		GenericSpecificationsBuilder specBuilder = new GenericSpecificationsBuilder();
		Specification<BaseView> specification = specBuilder.generateSpecification(viewCriteria);

		//Initialize Query
		//CriteriaQuery<StatusCountView> criteriaQuery = this.criteriaBuilder.createQuery(StatusCountView.class);

		String statusField = "status";
		//Class viewClass = StatusCountView.class;

		//Initialize Query
		CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery(Object[].class);

		//Generate From Clause
		Root<BaseView> fromEntity = criteriaQuery.from(viewClass);

		//Generate Select Clause
		criteriaQuery.multiselect(fromEntity.get(statusField), criteriaBuilder.count(fromEntity.get(statusField)));

		//Generate Where Clause
		Predicate predicate = null;
		if(Objects.nonNull(specification))
			predicate = specification.toPredicate(fromEntity, criteriaQuery, criteriaBuilder);

		//Generate Group By Clause
		criteriaQuery.groupBy(fromEntity.get(statusField));

		//Combine Select/From/Where Clauses
		if(Objects.nonNull(predicate))
			criteriaQuery.where(predicate);

		List<Object[]> list = this.entityManager.createQuery(criteriaQuery).getResultList();
		return new StatusOverview(list, statuses);
	}

	@Override
	public Slice<Id> getIds(String viewCriteria, Pageable pageable, Optional<String> optionalSliceSelect, Class viewClass) {
		GenericSpecificationsBuilder specBuilder = new GenericSpecificationsBuilder<>();
		Specification<E> viewSpecification = specBuilder.generateSpecification(viewCriteria);;

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(viewClass);

		// creating new pageable to convert keys from snake case to camel case
		pageable = CustomPageRequest.getCustomPageRequest(pageable);
		Root<E> fromView = criteriaQuery.from(viewClass);
		Predicate viewConditionPredicate = viewSpecification.toPredicate(fromView, criteriaQuery, criteriaBuilder);

		CompoundSelection<Id> construct = criteriaBuilder.construct(Id.class, fromView.get("id"));

		CriteriaQuery<Id> select = (CriteriaQuery<Id>) criteriaQuery.select((Selection<? extends E>) construct);

		select.where(viewConditionPredicate);

		String sliceSelect = optionalSliceSelect.orElse("");
		boolean selectResult = ("," + sliceSelect + ",").toUpperCase().contains(",RESULTS,");
		boolean selectTotalResults = ("," + sliceSelect + ",").toUpperCase().contains(",TOTAL-RESULTS,");

		int firstResult = pageable.getPageNumber() <= 0 ? 0 : pageable.getPageSize() * pageable.getPageNumber();
		int maxResults = pageable.getPageSize() + 1; //adding 1 to peek into the next page to populate hasNext of slice

		List<Id> listOfIds = new ArrayList<>();
		boolean sliceSelectExists = sliceSelect.length() > 0;
		if (selectResult || !sliceSelectExists) {
			select.orderBy(QueryUtils.toOrders(pageable.getSort(), fromView, criteriaBuilder));
			TypedQuery typedQuery = entityManager.createQuery(select);
			typedQuery.setFirstResult(firstResult);
			typedQuery.setMaxResults(maxResults);
			listOfIds = typedQuery.getResultList();
		}

		Long totalResults = null;
		if (selectTotalResults || !sliceSelectExists) {
			CriteriaQuery<Long> countCriteriaQuery = criteriaBuilder.createQuery(Long.class);
			Root<E> fromViewForCount = countCriteriaQuery.from(viewClass);

			Predicate viewConditionPredicateForCount = viewSpecification.toPredicate(fromViewForCount, countCriteriaQuery, criteriaBuilder);
			CriteriaQuery<Long> selectCount = countCriteriaQuery.select(criteriaBuilder.count(fromViewForCount));
			selectCount.where(viewConditionPredicateForCount);
			totalResults = entityManager.createQuery(selectCount).getSingleResult();
		}

		int maxApplicableResult = listOfIds.size() == maxResults ? maxResults - 1 : listOfIds.size();

		if (!selectResult && selectTotalResults) {
			return new PageImpl<>(listOfIds, pageable, totalResults);
		}
		else if (selectResult && !selectTotalResults) {
			return new SliceImpl<>(listOfIds.subList(0, maxApplicableResult), pageable, listOfIds.size() > pageable.getPageSize());
		}
		return new PageImpl<>(listOfIds.subList(0, maxApplicableResult), pageable, totalResults);
	}
}
