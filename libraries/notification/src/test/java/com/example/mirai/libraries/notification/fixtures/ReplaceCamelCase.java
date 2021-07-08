package com.example.mirai.libraries.notification.fixtures;

import java.lang.reflect.Method;

import org.junit.jupiter.api.DisplayNameGenerator;

public class ReplaceCamelCase extends DisplayNameGenerator.Standard {
	public ReplaceCamelCase() {
	}

	public String generateDisplayNameForClass(Class<?> testClass) {
		return this.generateName(super.generateDisplayNameForClass(testClass));
	}

	public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
		return this.generateName(super.generateDisplayNameForNestedClass(nestedClass));
	}

	public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
		return this.generateName(testMethod.getName());
	}

	private String generateName(String name) {
		name = name.replaceAll("(IT)$", "");
		name = name.replaceAll("([A-Z])", " $1");
		name = name.replaceAll("([0-9]+)", " $1");
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		return name;
	}
}
