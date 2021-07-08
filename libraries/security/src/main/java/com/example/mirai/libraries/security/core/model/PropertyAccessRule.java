package com.example.mirai.libraries.security.core.model;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PropertyAccessRule {
	private String filter;

	private Set<String> publicFields;

	private Set<String> protectedFields;

	private Set<String> privateFields;

	@Override
	public String toString() {
		return "[filter=" + filter
				+ ", publicFields=" + publicFields.toString() + ", protectedFields=" + protectedFields.toString()
				+ ", privateFields=" + privateFields.toString();
	}
}
