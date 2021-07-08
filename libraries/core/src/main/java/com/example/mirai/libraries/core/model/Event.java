package com.example.mirai.libraries.core.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class Event {
	private String type;

	private String status;

	private String entity;

	private String payload;

	private User actor;

	private Object data;

	private Map<String, Map<String, Object>> changedAttributes;

	private Long timestamp;
}
