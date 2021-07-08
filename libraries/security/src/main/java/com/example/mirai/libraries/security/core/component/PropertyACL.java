package com.example.mirai.libraries.security.core.component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.mirai.libraries.core.annotation.SpELEvaluationContext;
import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.core.exception.UnauthorizedException;
import com.example.mirai.libraries.core.model.BaseEvaluationContext;
import com.example.mirai.libraries.core.model.CaseProperty;
import com.example.mirai.libraries.security.core.PropertyACLInitializerInterface;
import com.example.mirai.libraries.security.core.model.PropertyAccessRule;
import com.example.mirai.libraries.util.ReflectionUtil;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "mirai.libraries.security.acl.property-acl")
public class PropertyACL {
	private final Map<String, Set<PropertyAccessRule>> map;

	public PropertyACL(PropertyACLInitializerInterface propertyACLInitializerInterface) {
		this.map = propertyACLInitializerInterface.getPropertyACL();
	}

	public static String generateKey(String role, Class objectClass) {
		if (Objects.isNull(role) || Objects.isNull(objectClass))
			return null;
		return objectClass.getCanonicalName() + role;
	}

	private Set<PropertyAccessRule> getAccessRules(String role, Class entityClass) {
		if (Objects.isNull(role) || Objects.isNull(entityClass))
			return null;

		return map.get(generateKey(role, entityClass));
	}

	private Set<PropertyAccessRule> getAccessRules(Set<String> roles, Class entityClass) {
		HashSet<PropertyAccessRule> result = new HashSet<>();
		if (Objects.isNull(roles) || roles.isEmpty() || Objects.isNull(entityClass))
			return result;
		List<Set<PropertyAccessRule>> accessRuleLists =
				roles.stream().map(role -> getAccessRules(role, entityClass)).filter(role -> Objects.nonNull(role)).collect(Collectors.toList());

		if (Objects.isNull(accessRuleLists) || accessRuleLists.isEmpty())
			return result;

		accessRuleLists.stream().forEach(accessRuleList -> result.addAll(accessRuleList));
		return result;
	}

	private Set<PropertyAccessRule> getApplicableAccessRules(Set<PropertyAccessRule> propertyAccessRules, Object object) {
		HashSet<PropertyAccessRule> result = new HashSet<>();
		if (Objects.isNull(propertyAccessRules) || Objects.isNull(object))
			return result;
		SpELEvaluationContext spelEvaluationContext = AnnotationUtils.findAnnotation(object.getClass(), SpELEvaluationContext.class);
		BaseEvaluationContext evaluationContext = null;
		if (spelEvaluationContext != null) {
			evaluationContext = (BaseEvaluationContext) ReflectionUtil.createInstance(spelEvaluationContext.value());
			evaluationContext.setContext(object);
		}
		BaseEvaluationContext finalEvaluationContext = evaluationContext;
		List<PropertyAccessRule> applicableRuleList = propertyAccessRules.stream().filter(propertyAccessRule -> evaluateExpression(propertyAccessRule, object, finalEvaluationContext)).collect(Collectors.toList());

		applicableRuleList.forEach(applicableRule -> result.add(applicableRule));
		return result;
	}

	private Boolean evaluateExpression(PropertyAccessRule propertyAccessRule, Object o, BaseEvaluationContext evaluationContext) {
		if (Objects.isNull(propertyAccessRule) || Objects.isNull(o))
			return false;
		if (Objects.isNull(propertyAccessRule.getFilter()))
			return true;

		ExpressionParser expressionParser = new SpelExpressionParser();
		if (evaluationContext != null) {
			return (Boolean) expressionParser.parseExpression(propertyAccessRule.getFilter()).getValue(evaluationContext);
		}
		return (Boolean) expressionParser.parseExpression(propertyAccessRule.getFilter()).getValue(o);
	}

	public Set<String> getReadablePropertiesRegexps(Set<String> roles, Object entity) {
		Set<String> readablePropertiesRegexps = new HashSet<>();
		Set<PropertyAccessRule> propertyAccessRules = getAccessRules(roles, entity.getClass());
		Set<PropertyAccessRule> applicablePropertyAccessRules = getApplicableAccessRules(propertyAccessRules, entity);

		if (applicablePropertyAccessRules.size() == 0)
			throw new InternalAssertionException("No Matching property in readable regexps");

		applicablePropertyAccessRules.forEach(applicablePropertyAccessRule -> {
			applicablePropertyAccessRule.getPublicFields().forEach(publicField -> readablePropertiesRegexps.add(publicField));
			applicablePropertyAccessRule.getProtectedFields().forEach(protectedField -> readablePropertiesRegexps.add(protectedField));
		});
		return readablePropertiesRegexps;
	}

	public CaseProperty getAllPropertiesRegExps(Set<String> roles, Object entity) {
		CaseProperty caseProperty = new CaseProperty();
		caseProperty.setReadablePropertyRegexps(getReadablePropertiesRegexps(roles, entity));
		caseProperty.setUnreadablePropertyRegexps(getUnreadablePropertiesRegexps(roles, entity));
		caseProperty.setUnupdatablePropertyRegexps(getUnUpdatablePropertiesRegexps(roles, entity));
		caseProperty.setUpdatablePropertyRegexps(getUpdatablePropertiesRegexps(roles, entity));
		return caseProperty;
	}

	public Set<String> getUpdatablePropertiesRegexps(Set<String> roles, Object entity) {
		Set<String> updateablePropertiesRegexps = new HashSet<>();
		Set<PropertyAccessRule> propertyAccessRules = getAccessRules(roles, entity.getClass());
		Set<PropertyAccessRule> applicablePropertyAccessRules = getApplicableAccessRules(propertyAccessRules, entity);

		if (applicablePropertyAccessRules.size() == 0)
			throw new InternalAssertionException("No Matching property in updatable regexps");

		applicablePropertyAccessRules.forEach(applicablePropertyAccessRule -> applicablePropertyAccessRule.getPublicFields().forEach(publicField -> updateablePropertiesRegexps.add(publicField)));
		return updateablePropertiesRegexps;
	}

	public Set<String> getUnreadablePropertiesRegexps(Set<String> roles, Object entity) {
		Set<String> unreadablePropertiesRegexps = new HashSet<>();
		Set<PropertyAccessRule> propertyAccessRules = getAccessRules(roles, entity.getClass());
		Set<PropertyAccessRule> applicablePropertyAccessRules = getApplicableAccessRules(propertyAccessRules, entity);

		if (propertyAccessRules.size() > 0 && applicablePropertyAccessRules.size() == 0)
			throw new UnauthorizedException();
		Set<String> unreadableProperties = new HashSet<>();
		Integer index = 0;
		for (PropertyAccessRule applicablePropertyAccessRule : applicablePropertyAccessRules) {
			if (index == 0) {
				unreadableProperties.addAll(applicablePropertyAccessRule.getPrivateFields());
			}
			else {
				unreadablePropertiesRegexps.retainAll(unreadableProperties);
			}
			index++;
		}
		applicablePropertyAccessRules.forEach(applicablePropertyAccessRule -> applicablePropertyAccessRule.getPrivateFields().forEach(privateField -> unreadablePropertiesRegexps.add(privateField)));
		return unreadablePropertiesRegexps;
	}

	public Set<String> getUnUpdatablePropertiesRegexps(Set<String> roles, Object entity) {
		Set<String> unUpdatablePropertiesRegexps = new HashSet<>();
		Set<PropertyAccessRule> propertyAccessRules = getAccessRules(roles, entity.getClass());
		Set<PropertyAccessRule> applicablePropertyAccessRules = getApplicableAccessRules(propertyAccessRules, entity);

		if (applicablePropertyAccessRules.size() == 0)
			throw new InternalAssertionException("No Matching property in Unupdatable regexps");
		Integer index = 0;
		for (PropertyAccessRule applicablePropertyAccessRule : applicablePropertyAccessRules) {
			Set<String> unUpdatableFields = applicablePropertyAccessRule.getPrivateFields().stream().collect(Collectors.toSet());
			unUpdatableFields.addAll(applicablePropertyAccessRule.getProtectedFields());
			if (index == 0) {
				unUpdatablePropertiesRegexps.addAll(unUpdatableFields);
			}
			else {
				unUpdatablePropertiesRegexps.retainAll(unUpdatableFields);
			}
			index++;
		}
		return unUpdatablePropertiesRegexps;
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
