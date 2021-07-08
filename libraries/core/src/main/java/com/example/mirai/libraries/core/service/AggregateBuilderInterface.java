package com.example.mirai.libraries.core.service;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.AggregateType;
import com.example.mirai.libraries.core.model.BaseEntityInterface;

/**
 * Should be implemented by AggregateBuilder class, which is used to read the aggregates
 * It is used to build the aggregate for entity, audit, case permissions
 */
public interface AggregateBuilderInterface {
	void buildAggregate(AggregateInterface aggregate, BaseEntityInterface baseEntityInterface, AggregateType aggregateType);

	AggregateInterface addDeletedEntities(AggregateInterface aggregate, BaseEntityInterface baseEntity);
}
