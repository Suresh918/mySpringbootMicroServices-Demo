package com.example.mirai.libraries.core.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EntityUpdate {
	BaseEntityInterface entity;

	Map<String, Object> changedAttrs;

	public void addToChangedAttrs(String key, Object value) {
		this.changedAttrs.put(key, value);
	}

	public void removeFromChangedAttrs(String key) {
		this.changedAttrs.remove(key);
	}
}
