package com.example.mirai.libraries.entity.service.helper.filter;

public enum FilterOperation {
	EQUALITY, NEGATION, GREATER_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL, BETWEEN, LIKE, STARTS_WITH, ENDS_WITH, CONTAINS, HAS, IN, REGEXP;

	public static final String ZERO_OR_MORE_REGEX = "*";

	public static final String OR_OPERATOR = "OR";

	public static final String AND_OPERATOR = "AND";

	public static final String LEFT_PARENTHESIS = "(";

	public static final String RIGHT_PARENTHESIS = ")";

	protected static final String[] SIMPLE_OPERATION_SET = { ":", "!", ">", ">=", "<", "<=", "-", "~", "#", "@", "%" };

	public static FilterOperation getSimpleOperation(final String input) {
		switch (input) {
			case ":":
				return EQUALITY;
			case "!":
				return NEGATION;
			case ">":
				return GREATER_THAN;
			case ">=":
				return GREATER_THAN_OR_EQUAL;
			case "<":
				return LESS_THAN;
			case "<=":
				return LESS_THAN_OR_EQUAL;
			case "-":
				return BETWEEN;
			case "~":
				return LIKE;
			case "#":
				return HAS;
			case "@":
				return IN;
			case "%":
				return REGEXP;
			default:
				return null;
		}
	}
}
