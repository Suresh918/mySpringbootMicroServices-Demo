package com.example.mirai.libraries.entity.service.helper.filter;

import com.example.mirai.libraries.util.CaseUtil;
import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Value;

@Getter
@Setter
public class FilterCriterion {
	private String key;

	private FilterOperation operation;

	private Object value;

	@Value("${spring.jackson.property-naming-strategy}")
	private String namingStrategy;

	public FilterCriterion(String key, String operation, String prefix, String value, String suffix) {
		FilterOperation op = FilterOperation.getSimpleOperation(operation.substring(0, 1));
		if (op != null && op == FilterOperation.EQUALITY) { // the operation may be complex operation
			final boolean startWithAsterisk = prefix != null && prefix.contains(FilterOperation.ZERO_OR_MORE_REGEX);
			final boolean endWithAsterisk = suffix != null && suffix.contains(FilterOperation.ZERO_OR_MORE_REGEX);

			if (startWithAsterisk && endWithAsterisk) {
				op = FilterOperation.CONTAINS;
			}
			else if (startWithAsterisk) {
				op = FilterOperation.ENDS_WITH;
			}
			else if (endWithAsterisk) {
				op = FilterOperation.STARTS_WITH;
			}
		}
		this.key = (namingStrategy != null && namingStrategy.equalsIgnoreCase("SNAKE_CASE")) ? CaseUtil.convertSnakeToCamelCase(key) : key;
		this.operation = op;
		this.value = value;
	}

	public String toString() {
		return "[key=" + key + ", operation=" + operation + ", value=" + value + "]";
	}
}
