package com.example.mirai.libraries.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseEvaluationContext<T> {
	protected T context;

	protected User auditor;
}
