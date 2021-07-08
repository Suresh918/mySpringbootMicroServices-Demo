package com.example.mirai.libraries.entity.service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.example.mirai.libraries.core.annotation.SpELEvaluationContext;
import com.example.mirai.libraries.core.exception.CaseActionNotFoundException;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.BaseEvaluationContext;
import com.example.mirai.libraries.core.model.CaseAction;
import com.example.mirai.libraries.util.ReflectionUtil;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public interface EntityStateMachineDefaultInterface {
	default boolean isCaseActionPerformable(BaseEntityInterface entity, CaseAction caseAction) {
		SpELEvaluationContext spelEvaluationContext = AnnotationUtils.findAnnotation(entity.getClass(), SpELEvaluationContext.class);
		BaseEvaluationContext<BaseEntityInterface> evaluationContext = null;
		if (Objects.nonNull(spelEvaluationContext)) {
			evaluationContext = (BaseEvaluationContext<BaseEntityInterface>) ReflectionUtil.createInstance(spelEvaluationContext.value());
			assert evaluationContext != null;
			evaluationContext.setContext(entity);
		}
		if (Objects.isNull(caseAction))
			throw new CaseActionNotFoundException();
		Set<String> mandatoryPropertiesRegexp = caseAction.getMandatoryPropertiesRegexps();
		if (Objects.isNull(mandatoryPropertiesRegexp) || mandatoryPropertiesRegexp.isEmpty())
			return true;
		Set<String> mandatoryPropertiesWithNullValue = ReflectionUtil.getFieldsMatchingRegexpsIfValueIsNull(entity, mandatoryPropertiesRegexp);
		Set<String> mandatoryPropertiesOfCollectionTypeWithEmptyValue = ReflectionUtil.getFieldsMatchingRegexpsIfValueIsCollectionTypeAndEmpty(entity, mandatoryPropertiesRegexp);
		Boolean mandatoryPropertiesSpelEvaluationResult = Objects.isNull(caseAction.getMandatoryPropertiesSpel());
		if (Objects.nonNull(evaluationContext) && Objects.nonNull(caseAction.getMandatoryPropertiesSpel())) {
			ExpressionParser expressionParser = new SpelExpressionParser();
			mandatoryPropertiesSpelEvaluationResult = (Boolean) expressionParser.parseExpression(caseAction.getMandatoryPropertiesSpel()).getValue(evaluationContext);
		}
		return mandatoryPropertiesWithNullValue.isEmpty() && mandatoryPropertiesOfCollectionTypeWithEmptyValue.isEmpty() && mandatoryPropertiesSpelEvaluationResult;
	}

	default boolean isCaseActionPerformable(BaseEntityInterface entity, List<CaseAction> caseActions) {
		if (caseActions.size() == 1) {
			return isCaseActionPerformable(entity, caseActions.get(0));
		}
		SpELEvaluationContext spelEvaluationContext = AnnotationUtils.findAnnotation(entity.getClass(), SpELEvaluationContext.class);
		BaseEvaluationContext<BaseEntityInterface> evaluationContext = null;
		if (Objects.nonNull(spelEvaluationContext)) {
			evaluationContext = (BaseEvaluationContext<BaseEntityInterface>) ReflectionUtil.createInstance(spelEvaluationContext.value());
			assert evaluationContext != null;
			evaluationContext.setContext(entity);
			ExpressionParser expressionParser = new SpelExpressionParser();
			for (CaseAction caseAction : caseActions) {
				if (evaluationContext != null && Objects.nonNull(caseAction.getFilter())
						&& (Boolean) expressionParser.parseExpression(caseAction.getFilter()).getValue(evaluationContext)) {
					return isCaseActionPerformable(entity, caseAction);
				}
			}
		}
		return false;
	}
}
