package com.example.mirai.libraries.entity.model;

import java.util.ArrayList;
import java.util.Iterator;

import com.example.mirai.libraries.util.CaseUtil;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;

public class CustomPageRequest extends PageRequest {
	private final Sort sort;

	public CustomPageRequest(int page, int size, Sort sort) {
		super(page, size, sort);
		this.sort = sort;
	}

	public static CustomPageRequest getCustomPageRequest(Pageable pageable) {
		return new CustomPageRequest(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
	}

	@Override
	public Sort getSort() {
		Iterator sortIterator = this.sort.iterator();
		ArrayList updatedSortOrders = new ArrayList();
		while (sortIterator.hasNext()) {
			Object sortDef = sortIterator.next();
			String updatedKey = CaseUtil.convertSnakeToCamelCase(((Sort.Order) sortDef).getProperty());
			Sort.Order sortOrder = new Sort.Order(((Sort.Order) sortDef).getDirection(), updatedKey);
			updatedSortOrders.add(sortOrder);
		}
		return Sort.by(updatedSortOrders);
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (this == obj) {
			return true;
		}
		else if (!(obj instanceof PageRequest)) {
			return false;
		}
		else {
			PageRequest pageRequest = (PageRequest) obj;
			return super.equals(pageRequest) && this.sort.equals(pageRequest.getSort());
		}
	}
}
