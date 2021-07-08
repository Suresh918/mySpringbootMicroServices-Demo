package com.example.mirai.libraries.core.model;

import java.util.List;

/**
 * Interface to be Implemented by All Entities
 *
 * @author ptummala
 * @since 1.0.0
 */
public interface BaseEntityInterface {
	Long getId();

	void setId(Long id);

	Integer getStatus();

	void setStatus(Integer status);

	default String generateObjectId() {
		return getClass().getCanonicalName() + "-" + getId();
	}

	List<ContextInterface> getContextsAsContextInterface();

}
