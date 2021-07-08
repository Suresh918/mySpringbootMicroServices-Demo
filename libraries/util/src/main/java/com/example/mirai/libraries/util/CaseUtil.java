package com.example.mirai.libraries.util;

import com.google.common.base.CaseFormat;


public class CaseUtil {

	public static String convertCamelToSnakeCase(String key) {
		return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, key);
	}

	public static String convertSnakeToCamelCase(String key) {
		return key.contains("_") ? CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, key) : key;
	}
}
