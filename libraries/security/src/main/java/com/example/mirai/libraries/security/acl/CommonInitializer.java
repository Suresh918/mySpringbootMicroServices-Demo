package com.example.mirai.libraries.security.acl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.example.mirai.libraries.util.CaseUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

public abstract class CommonInitializer {
	protected ObjectMapper objectMapper;

	public CommonInitializer() {
		this.objectMapper = new ObjectMapper();
		this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				.withGetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withSetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
	}

	protected abstract String getConfigurationFilePath();

	protected abstract void processKey(Class entityClass) throws JsonProcessingException;

	@PostConstruct
	public void init() throws JsonProcessingException {
		JsonNode jsonNode = readTree();
		Set keys = objectMapper.convertValue(jsonNode, Map.class).keySet();
		keys.stream().forEach(key -> {
			try {
				Class entityClass = Class.forName((String) key);
				processKey(entityClass);
			}
			catch (ClassNotFoundException | JsonProcessingException e) {
				e.printStackTrace();
			}
		});
	}

	protected String getResourceContent(String resourceLocation) {
		InputStream inputStream = this.getClass().getResourceAsStream(resourceLocation);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
		return bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
	}

	protected Object convertToSnakeCase(Object o) {
		HashMap map = objectMapper.convertValue(o, HashMap.class);
		Map clonedMap = (HashMap) map.clone();
		map.forEach((key, value) -> {
			String snakeCaseKey = CaseUtil.convertCamelToSnakeCase((String) key);
			clonedMap.remove(key);
			clonedMap.put(snakeCaseKey, value);
		});
		return objectMapper.convertValue(clonedMap, JsonNode.class);
	}

	protected JsonNode readTree() throws JsonProcessingException {
		return objectMapper.readTree(getResourceContent(getConfigurationFilePath()));
	}
}
