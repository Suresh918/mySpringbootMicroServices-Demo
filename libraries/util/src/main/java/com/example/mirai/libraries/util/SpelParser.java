package com.example.mirai.libraries.util;

import java.lang.reflect.Method;

import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpelParser {

	private static final LocalVariableTableParameterNameDiscoverer discoverer;

	private static final ExpressionParser parser;

	static {
		parser = new SpelExpressionParser();
		discoverer = new LocalVariableTableParameterNameDiscoverer();
	}

	public static <T> T evaluateSpel(Method method, Object[] arguments, String spel, Class<T> clazz, T defaultResult) {

		String[] params = discoverer.getParameterNames(method);
		EvaluationContext context = new StandardEvaluationContext();
		for (int len = 0; len < params.length; len++) {
			context.setVariable(params[len], arguments[len]);
		}
		try {
			Expression expression = parser.parseExpression(spel);
			return expression.getValue(context, clazz);
		}
		catch (Exception e) {
			return defaultResult;
		}
	}
}
