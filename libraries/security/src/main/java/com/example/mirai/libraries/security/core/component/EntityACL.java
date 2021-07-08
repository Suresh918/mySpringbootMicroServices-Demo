package com.example.mirai.libraries.security.core.component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.mirai.libraries.core.annotation.SpELEvaluationContext;
import com.example.mirai.libraries.core.model.BaseEvaluationContext;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.security.core.EntityACLInitializerInterface;
import com.example.mirai.libraries.security.core.model.EntityAccessRule;
import com.example.mirai.libraries.util.ReflectionUtil;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "mirai.libraries.security.acl.entity-acl")
public class EntityACL {
	private final Map<String, HashSet<EntityAccessRule>> map;

	public EntityACL(EntityACLInitializerInterface entityACLInitializerInterface) {
		this.map = entityACLInitializerInterface.getEntityACL();
	}

	public static String generateKey(String role, Class objectClass) {
		return objectClass.getCanonicalName() + role;
	}

	private HashSet<EntityAccessRule> getAccessRules(String role, Class entityClass) {

		return map.get(generateKey(role, entityClass));
	}

	private HashSet<EntityAccessRule> getAccessRules(Set<String> roles, Class entityClass) {
		HashSet<EntityAccessRule> result = new HashSet<>();
		if (Objects.isNull(roles) || roles.isEmpty())
			return result;
		List<HashSet<EntityAccessRule>> accessRuleLists =
				roles.stream().map(role -> getAccessRules(role, entityClass)).filter(role -> Objects.nonNull(role)).collect(Collectors.toList());

		if (Objects.isNull(accessRuleLists) || accessRuleLists.isEmpty())
			return result;

		accessRuleLists.stream().forEach(accessRuleList -> result.addAll(accessRuleList));
		return result;
	}

	private HashSet<EntityAccessRule> getApplicableAccessRules(HashSet<EntityAccessRule> entityAccessRules, Object object, User auditor) {
		SpELEvaluationContext spelEvaluationContext = AnnotationUtils.findAnnotation(object.getClass(), SpELEvaluationContext.class);
		BaseEvaluationContext evaluationContext = null;
		if (spelEvaluationContext != null) {
			evaluationContext = (BaseEvaluationContext) ReflectionUtil.createInstance(spelEvaluationContext.value());
			evaluationContext.setContext(object);
			evaluationContext.setAuditor(auditor);
		}
		BaseEvaluationContext finalEvaluationContext = evaluationContext;
		List<EntityAccessRule> applicableRuleList = entityAccessRules.stream().filter(entityAccessRule -> evaluateExpression(entityAccessRule, object, auditor, finalEvaluationContext)).collect(Collectors.toList());
		HashSet<EntityAccessRule> result = new HashSet<>();
		applicableRuleList.forEach(applicableRule -> result.add(applicableRule));
		return result;
	}


	private HashSet<EntityAccessRule> getApplicableAccessRules(HashSet<EntityAccessRule> entityAccessRules) {
		List<EntityAccessRule> applicableRuleList = entityAccessRules.stream().filter(entityAccessRule -> Objects.isNull(entityAccessRule.getFilter())).collect(Collectors.toList());
		HashSet<EntityAccessRule> result = new HashSet<>();
		applicableRuleList.forEach(applicableRule -> result.add(applicableRule));
		return result;
	}

	private Boolean evaluateExpression(EntityAccessRule entityAccessRule, Object o, User auditor, BaseEvaluationContext evaluationContext) {
		if (Objects.isNull(entityAccessRule) || Objects.isNull(o))
			return false;
		if (Objects.isNull(entityAccessRule.getFilter()))
			return true;
		ExpressionParser expressionParser = new SpelExpressionParser();
		if (evaluationContext != null) {
			return (Boolean) expressionParser.parseExpression(entityAccessRule.getFilter()).getValue(evaluationContext);
		}
		return (Boolean) expressionParser.parseExpression(entityAccessRule.getFilter()).getValue(o);
	}

	public Set<String> getAuthorizedCaseActions(Set<String> roles, Object entity, User auditor) {
		HashSet<String> caseActions = new HashSet<>();
		HashSet<EntityAccessRule> entityAccessRules = getAccessRules(roles, entity.getClass());
		HashSet<EntityAccessRule> applicableEntityAccessRules = getApplicableAccessRules(entityAccessRules, entity, auditor);
		applicableEntityAccessRules.forEach(entityAccessRule -> entityAccessRule.getCaseActions().forEach(caseAction -> caseActions.add(caseAction)));
		return caseActions;
	}

	public Set<String> getAuthorizedCaseActions(Set<String> roles, Class entityClass) {
		HashSet<String> caseActions = new HashSet<>();
		HashSet<EntityAccessRule> entityAccessRules = getAccessRules(roles, entityClass);
		HashSet<EntityAccessRule> applicableEntityAccessRules = getApplicableAccessRules(entityAccessRules);
		applicableEntityAccessRules.forEach(entityAccessRule -> entityAccessRule.getCaseActions().forEach(caseAction -> caseActions.add(caseAction)));
		return caseActions;
	}

	public Set<String> getFetchRulesByAuthorizedCaseAction(Set<String> roles, Class entityClass, String caseAction) {

		HashSet<EntityAccessRule> entityAccessRules = getAccessRules(roles, entityClass);
		HashSet<EntityAccessRule> applicableEntityAccessRules = getApplicableAccessRules(entityAccessRules);
		Set<EntityAccessRule> applicableEntityAccessRulesForCaseAction = applicableEntityAccessRules.stream()
				.filter(entityAccessRule -> entityAccessRule.getCaseActions().contains(caseAction)).collect(Collectors.toSet());
		return applicableEntityAccessRulesForCaseAction.stream()
				.map(entityAccessRulesForCaseAction -> entityAccessRulesForCaseAction.getFetchRule())
				.collect(Collectors.toSet());
	}

	public Set<String> getFetchViewRulesByAuthorizedCaseAction(Set<String> roles, Class entityClass, String caseAction) {

		HashSet<EntityAccessRule> entityAccessRules = getAccessRules(roles, entityClass);
		HashSet<EntityAccessRule> applicableEntityAccessRules = getApplicableAccessRules(entityAccessRules);
		Set<EntityAccessRule> applicableEntityAccessRulesForCaseAction = applicableEntityAccessRules.stream()
				.filter(entityAccessRule -> entityAccessRule.getCaseActions().contains(caseAction)).collect(Collectors.toSet());
		return applicableEntityAccessRulesForCaseAction.stream()
				.map(entityAccessRulesForCaseAction -> entityAccessRulesForCaseAction.getFetchViewRule())
				.collect(Collectors.toSet());
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
