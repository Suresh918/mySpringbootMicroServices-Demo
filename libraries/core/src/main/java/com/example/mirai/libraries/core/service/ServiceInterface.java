package com.example.mirai.libraries.core.service;


import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.BaseEntityList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

/**
 * Interface to be implemented by Service annotated classes
 *
 * <p>It exposes common methods of service classes that are needed across libraries
 *
 * @author ptummala
 * @since 1.0.0
 */
public interface ServiceInterface {
    BaseEntityInterface getEntityById(Long entityId);

    AggregateInterface getAggregate(long id, Class<AggregateInterface> aggregateInterfaceClass);

    Class<? extends BaseEntityInterface> getEntityClass();

    BaseEntityList filter(String criteria, Pageable pageable);

    Slice filterIds(String criteria, Pageable pageable);

    BaseEntityInterface create(BaseEntityInterface entity);

}
