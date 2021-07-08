package com.example.mirai.libraries.core.model;

import com.example.mirai.libraries.core.annotation.AclImpactedEntities;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityLink<E extends BaseEntityInterface> {
	private Long id;

	private Class<E> eClass;

	private String relationshipCardinality;

	public EntityLink(Long id, Class<E> eClass) {
		this.id = id;
		this.eClass = eClass;
	}
}
