package com.example.mirai.libraries.entity.repository;

import java.util.Optional;

import com.example.mirai.libraries.core.model.BaseView;
import com.example.mirai.libraries.core.model.StatusInterface;
import com.example.mirai.libraries.entity.model.Id;
import com.example.mirai.libraries.entity.model.StatusOverview;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface BaseRepository<E> {
	Slice<Id> getIds(String criteria, Optional<String> viewCriteria, Pageable pageable, Class<E> clazz, Optional<String> sliceSelect, Optional<Class> viewClass);

	Slice<Id> getIds(String viewCriteria, Pageable pageable, Optional<String> optionalSliceSelect, Class viewClass);

	Slice<BaseView> getEntitiesFromView(String criteria, String viewCriteria, Pageable pageable, Class<E> entityClass,
			Optional<String> optionalSliceSelect, Class viewClass);


	Slice<BaseView> getEntitiesFromView(String viewCriteria, Pageable pageable, Optional<String> optionalSliceSelect, Class viewClass);

	StatusOverview getStatusOverview(String criteria, String viewCriteria, Class<E> entityClass, StatusInterface[] statuses, Optional<Class> viewClass);

	StatusOverview getStatusOverview(String viewCriteria, StatusInterface[] statuses, Class viewClass);
}
