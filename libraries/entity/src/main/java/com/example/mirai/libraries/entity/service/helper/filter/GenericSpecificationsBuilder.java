package com.example.mirai.libraries.entity.service.helper.filter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.ElementCollection;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.criteria.*;

import com.example.mirai.libraries.util.CaseUtil;
import com.example.mirai.libraries.util.Constants;
import com.example.mirai.libraries.util.ReflectionUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.jpa.domain.Specification;

@Slf4j
public class GenericSpecificationsBuilder<E> {
	private static Object convertValue(String key, String value) {
		if (key.equals("id"))
			return Long.parseLong(value);
		else
			return value;
	}

	private static Object convertValue(String key, String value, Class entityClass) throws ParseException {
		Class datatypeClass = ReflectionUtil.getDatatypeOfFieldInClass(entityClass, key);
		if (datatypeClass != null) {
			if (datatypeClass.isAssignableFrom(Long.class)) {
				try {
					return Long.parseLong(value);
				}
				catch (NumberFormatException e) {
					return value;
				}
			}
			else if (datatypeClass.isAssignableFrom(Date.class))
				return new SimpleDateFormat(Constants.DATE_TIME_FORMAT).parse(value);
			else if (datatypeClass.isAssignableFrom(LocalDate.class)) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
				return LocalDate.parse(value, formatter);
			}
			else if (datatypeClass.isAssignableFrom(Integer.class))
				return Integer.parseInt(value);
			else if (datatypeClass.isAssignableFrom(Boolean.class))
				return Boolean.valueOf(value);
			else
				return getValueBetweenQuotes(value);
		}
		return getValueBetweenQuotes(value);
	}

	private static Object getValueBetweenQuotes(String value) {
		String filterValue = "";
		if (value.contains("\"")) {
			Pattern pattern = Pattern.compile("\"(.*?)\"", Pattern.DOTALL);
			Matcher matcher = pattern.matcher(value);
			while (matcher.find()) {
				filterValue = matcher.group(1);
			}
			return filterValue;
		}
		return value;
	}

	private static Predicate getBetweenPredicate(Root root, CriteriaBuilder builder, FilterCriterion criteria) throws ParseException {
		String criteriaKey = CaseUtil.convertSnakeToCamelCase(criteria.getKey());
		Object fromValue = convertValue(criteriaKey, criteria.getValue().toString().split(",")[0], root.getModel().getJavaType());
		Object toValue = convertValue(criteriaKey, criteria.getValue().toString().split(",")[1], root.getModel().getJavaType());
		if (fromValue instanceof Long) {
			return builder.between(getPath(root, null, criteriaKey), (Long) fromValue, (Long) toValue);
		}
		else if (fromValue instanceof Date) {
			return builder.between(getPath(root, null, criteriaKey), (Date) fromValue, (Date) toValue);
		}
		else if (fromValue instanceof Integer) {
			return builder.between(getPath(root, null, criteriaKey), (Integer) fromValue, (Integer) toValue);
		}
		return null;
	}

	private static Path getPath(Root root, Path path, String key) {
		if (!key.contains(".") && path == null)              //property belongs to the entity being filtered on
			return root.get(key);
		else if (key.contains(".") && path == null) {             //property belongs to an element and path construction not started
			String startKey = key.substring(0, key.indexOf("."));
			String leftOverKey = key.substring(key.indexOf(".") + 1);
			Path startPath = null;
			boolean s1 = ReflectionUtil.isAnnotationAppliedOnField(root.getJavaType(), startKey, ElementCollection.class);
			boolean s2 = ReflectionUtil.isAnnotationAppliedOnField(root.getJavaType(), startKey, ManyToOne.class);
			boolean s3 = ReflectionUtil.isAnnotationAppliedOnField(root.getJavaType(), startKey, OneToMany.class);
			if (s1) {
				startPath = root.join(startKey, JoinType.LEFT);
			}
			else if (s2 || s3) {
				startPath = root.join(startKey);
			}
			else {
				startPath = root.get(startKey);
			}
			return getPath(root, startPath, leftOverKey);
		}
		else if (key.split("\\.").length > 1 && path != null) {
			String subKey = key.substring(0, key.indexOf("."));
			String leftOverKey = key.substring(key.indexOf(".") + 1);
			Path subPath = null;
			boolean s1 = ReflectionUtil.isAnnotationAppliedOnField(root.getJavaType(), subKey, ElementCollection.class);
			boolean s2 = ReflectionUtil.isAnnotationAppliedOnField(root.getJavaType(), subKey, ManyToOne.class);
			boolean s3 = ReflectionUtil.isAnnotationAppliedOnField(root.getJavaType(), subKey, OneToMany.class);
			if (s1) {
				subPath = ((Join) path).join(subKey, JoinType.LEFT);
				//subPath = path.get(subKey);
			}
			else if (s2 || s3) {
				subPath = ((Join) path).join(subKey);
			}
			else {
				subPath = path.get(subKey);
			}

			return getPath(root, subPath, leftOverKey);
		}
		else {
			return path.get(key);
		}
	}

	public Specification<E> generateSpecification(String criteria) {
		GenericSpecificationsBuilder specBuilder = new GenericSpecificationsBuilder<>();
		return specBuilder.build(CriteriaParser.parse(criteria));
	}

	private Specification<E> build(Deque<?> postFixedExprStack) {


		Deque<Specification<E>> specStack = new LinkedList<>();

		Collections.reverse((List<?>) postFixedExprStack);

		while (!postFixedExprStack.isEmpty()) {
			Object mayBeOperand = postFixedExprStack.pop();

			if (!(mayBeOperand instanceof String)) {
				specStack.push(getSpecification((FilterCriterion) mayBeOperand));
			}
			else {
				Specification<E> operand1 = specStack.pop();
				Specification<E> operand2 = specStack.pop();
				if (mayBeOperand.equals(FilterOperation.AND_OPERATOR))
					specStack.push(Specification.where(operand1)
							.and(operand2));
				else if (mayBeOperand.equals(FilterOperation.OR_OPERATOR))
					specStack.push(Specification.where(operand1)
							.or(operand2));
			}

		}
		return specStack.isEmpty() ? null : specStack.pop();
	}

	private Specification<E> getSpecification(FilterCriterion criteria) {
		return (root, query, builder) -> {

			String criteriaKey = CaseUtil.convertSnakeToCamelCase(criteria.getKey());
			switch (criteria.getOperation()) {
				case EQUALITY:
					//return builder.equal(root.get(criteriaKey).get("id1"), criteria.getValue());
					try {
						return builder.equal(getPath(root, null, criteriaKey), convertValue(criteriaKey, (String) criteria.getValue(), root.getModel().getJavaType()));
					}
					catch (ParseException | NumberFormatException e) {
						log.warn(e.getMessage());
						return null;
					}
				case NEGATION:
					return builder.notEqual(getPath(root, null, criteriaKey), criteria.getValue());
				case GREATER_THAN:
					try {
						return builder.greaterThan(getPath(root, null, criteriaKey), (Date) convertValue(criteriaKey, (String) criteria.getValue(), root.getModel().getJavaType()));
					}
					catch (ParseException | NumberFormatException e) {
						log.warn(e.getMessage());
						return null;
					}
				case GREATER_THAN_OR_EQUAL:
					try {
						return builder.greaterThanOrEqualTo(getPath(root, null, criteriaKey), (Date) convertValue(criteriaKey, (String) criteria.getValue(), root.getModel().getJavaType()));
					}
					catch (ParseException | NumberFormatException e) {
						log.warn(e.getMessage());
						return null;
					}
				case LESS_THAN:
					try {
						return builder.lessThan(getPath(root, null, criteriaKey), (Date) convertValue(criteriaKey, (String) criteria.getValue(), root.getModel().getJavaType()));
					}
					catch (ParseException | NumberFormatException e) {
						log.warn(e.getMessage());
						return null;
					}
				case LESS_THAN_OR_EQUAL:
					try {
						return builder.lessThanOrEqualTo(getPath(root, null, criteriaKey), (Date) convertValue(criteriaKey, (String) criteria.getValue(), root.getModel().getJavaType()));
					}
					catch (ParseException | NumberFormatException e) {
						log.warn(e.getMessage());
						return null;
					}
				case BETWEEN:
					Predicate between = builder.between(getPath(root, null, criteriaKey), criteria.getValue().toString().split(",")[0], criteria.getValue().toString().split(",")[1]);
					try {
						Predicate predicate = getBetweenPredicate(root, builder, criteria);
						return (predicate == null) ? between : predicate;
					}
					catch (ParseException | NumberFormatException e) {
						log.warn(e.getMessage());
						return null;
					}
				case REGEXP:
					try {
						Expression<String> regExpr = builder.literal((String) criteria.getValue());
						Expression<Boolean> regExprInstr = builder.function("regexp", Boolean.class, getPath(root, null, criteriaKey), regExpr);
						return builder.equal(regExprInstr, true);
					} catch(Exception e) {
						log.error("Error evaluating regular expression");
					}
				case LIKE:
					return builder.like(getPath(root, null, criteriaKey), criteria.getValue().toString());
				case STARTS_WITH:
					return builder.like(getPath(root, null, criteriaKey), criteria.getValue() + "%");
				case ENDS_WITH:
					return builder.like(getPath(root, null, criteriaKey), "%" + criteria.getValue());
				case CONTAINS:
					return builder.like(getPath(root, null, criteriaKey), "%" + criteria.getValue() + "%");
				case HAS:
					return builder.isMember(criteria.getValue(), getPath(root, null, criteriaKey));
				case IN:
					CriteriaBuilder.In in = builder.in(getPath(root, null, criteriaKey));
					Arrays.stream((criteria.getValue() + ",").split(",")).forEach(val -> {
						try {
							in.value(convertValue(criteriaKey, val, root.getModel().getJavaType()));
						}
						catch (ParseException | NumberFormatException e) {
							log.warn(e.getMessage());
						}
					});
					return in;
				default:
					return null;
			}
		};
	}
}
