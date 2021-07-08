package com.example.mirai.libraries.entity.component;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.StatusInterface;
import com.example.mirai.libraries.entity.service.EntityResolverDefaultInterface;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(name = "entityResolver")
public class DefaultEntityResolverInterfaceImpl implements EntityResolverDefaultInterface {
	@Override
	public Class<? extends BaseEntityInterface> getEntityClass(String type) {
		return null;
	}

	@Override
	public Class<? extends BaseEntityInterface> getEntityClass(String parent, String child) {
		return null;
	}

	@Override
	public Class<? extends AggregateInterface> getAggregateClass(String parent, String child) {
		return null;
	}

	@Override
	public StatusInterface[] getEntityStatuses(String entityType) {
		return new StatusInterface[0];
	}

	@Override
	public StatusInterface[] getEntityStatuses(Class entityClass) {
		return new StatusInterface[0];
	}

	@Override
	public Class getCaseStatusAggregateClass(String s) {
		return null;
	}

	@Override
	public Class getAggregateClass(String s) {
		return null;
	}
}
