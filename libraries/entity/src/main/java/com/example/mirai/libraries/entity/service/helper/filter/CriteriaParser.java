package com.example.mirai.libraries.entity.service.helper.filter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;

public class CriteriaParser {
	private static final Map<String, Operator> ops;

	/**
	 * first-group: key
	 * second-group: operator
	 * third-group: TODO
	 * fourth-group: TODO
	 * fifth-group: TODO
	 * sixth-group: TODO
	 * seventh-group: TODO
	 */
	private static final Pattern SpecCriteriaRegex = Pattern.compile("^([\\w\\[.\\]\\\\]+?)(" + Joiner.on("|").join(FilterOperation.SIMPLE_OPERATION_SET) + ")(\"?)(\\p{Punct}?)((\"?[a-zA-Z0-9_,;~:.$+&\\-| ]+?\"?)+)(\\p{Punct}?)(\"?)$");

	static {
		Map<String, Operator> tempMap = new HashMap<>();
		tempMap.put("AND", Operator.AND);
		tempMap.put("OR", Operator.OR);
		tempMap.put("or", Operator.OR);
		tempMap.put("and", Operator.AND);

		ops = Collections.unmodifiableMap(tempMap);
	}

	private static boolean isHigherPrecedenceOperator(String currOp, String prevOp) {
		return (ops.containsKey(prevOp) && ops.get(prevOp).precedence >= ops.get(currOp).precedence);
	}

	public static Deque<?> parse(String filterParameters) {
		Deque<Object> output = new LinkedList<>();
		Deque<String> stack = new LinkedList<>();
		String[] filters = CriteriaParserUtil.getFilters(filterParameters);
		Arrays.stream(filters).forEach(token -> {

			if (ops.containsKey(token)) {

				while (!stack.isEmpty() && isHigherPrecedenceOperator(token, stack.peek()))
					output.push(stack.pop()
							.equalsIgnoreCase(FilterOperation.OR_OPERATOR) ? FilterOperation.OR_OPERATOR : FilterOperation.AND_OPERATOR);
				stack.push(token.equalsIgnoreCase(FilterOperation.OR_OPERATOR) ? FilterOperation.OR_OPERATOR : FilterOperation.AND_OPERATOR);
			}
			else if (token.equals(FilterOperation.LEFT_PARENTHESIS)) {
				stack.push(FilterOperation.LEFT_PARENTHESIS);
			}
			else if (token.equals(FilterOperation.RIGHT_PARENTHESIS)) {
				while (!stack.peek()
						.equals(FilterOperation.LEFT_PARENTHESIS))
					output.push(stack.pop());
				stack.pop();
			}
			else {
				Matcher matcher = SpecCriteriaRegex.matcher(token);
				while (matcher.find()) {
					output.push(new FilterCriterion(matcher.group(1), matcher.group(2), matcher.group(4), CriteriaParserUtil.removeEndQuotesFromValue(matcher.group(5)), matcher.group(7)));
				}
			}
		});

		while (!stack.isEmpty())
			output.push(stack.pop());

		return output;
	}

	private enum Operator {
		OR(1), AND(2);

		final int precedence;

		Operator(int p) {
			precedence = p;
		}
	}
}
