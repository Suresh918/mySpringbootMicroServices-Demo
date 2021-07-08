package com.example.mirai.libraries.core.model;

import java.util.List;

public interface SynchronizationContextInterface extends ContextInterface {
	String getParentId();

	String getTitle();

	List<String> getParentIds();
}
