package com.example.mirai.libraries.entity.service.helper.filter;

import java.util.ArrayList;
import java.util.Arrays;

public class CriteriaParserUtil {
	private static String[] removeEmptyValues(String[] array) {
		return Arrays.stream(array).map(String::trim).filter(value -> value != null && value.length() > 0)
				.toArray(String[]::new);
	}

	private static String[] prepareFilters(String[] data, String splitBy, String replacingString) {
		ArrayList<String> filters = new ArrayList<>();
		Arrays.stream(data).forEach(item -> {
			int count = 0;
			item = item + " ";
			String[] filterSplit = item.split(splitBy + "(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			ArrayList<String> filterList = new ArrayList<>(Arrays.asList(filterSplit));
			while (count < filterList.size()) {
				if (count % 2 == 0 && count < filterList.size() - 1) {
					filterList.add(count + 1, replacingString);
					count++;
				}
				count++;
			}
			filters.addAll(filterList);
		});
		return filters.toArray(new String[0]);
	}

	public static String[] getFilters(String filterParameters) {
		String[] filters;
		String[] filterParameterValues = new String[] { filterParameters };
		filters = CriteriaParserUtil.prepareFilters(filterParameterValues, "\\sand\\s", "AND");
		filters = CriteriaParserUtil.prepareFilters(filters, "\\sor\\s", "OR");
		filters = CriteriaParserUtil.prepareFilters(filters, "\\(", "(");
		filters = CriteriaParserUtil.prepareFilters(filters, "\\)", ")");
		return removeEmptyValues(filters);
	}

	public static String removeEndQuotesFromValue(String str) {
		str = str.trim();
		if (str.charAt(str.length() - 1) == '"') {
			str = str.replace(str.substring(str.length() - 1), "");
			return str;
		}
		return str;
	}
}
