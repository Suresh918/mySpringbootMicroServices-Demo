package com.example.mirai.projectname.services.configuration.util;

public class Util {
	private Util() {
	}

	public static String generateIdFromString(String str) {
		return str
				.replace(" ", "-")
				.replaceAll("[^a-zA-Z0-9\\-]", "-")
				.toLowerCase();
	}
}
