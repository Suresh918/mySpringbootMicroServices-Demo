package com.example.mirai.libraries.event;

public interface EventBuilderInterface {
	default void setResponseClass(Class responseClass) {
	}

	default Object translateResponse(Object obj) {
		return obj;
	}
}
