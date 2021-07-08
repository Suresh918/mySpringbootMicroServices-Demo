package com.example.mirai.libraries.core.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

/**
 * Used as return type while retrieving the list of entities
 *
 * @author ptummala
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntityList<T> {
	private Long totalElements;

	private Integer totalPages;

	private Boolean hasNext;

	private List<T> results;

	public BaseEntityList(Slice<T> slice) {
		if (slice instanceof Page) {
			this.totalElements = ((Page<T>) slice).getTotalElements();
			this.totalPages = ((Page<T>) slice).getTotalPages();
		}
		else {
			this.hasNext = slice.hasNext();
		}
		this.results = slice.getContent();
	}

	public BaseEntityList(Slice<T> sliceInfo, List<T> content) {
		if (sliceInfo instanceof Page) {
			this.totalElements = ((Page<T>) sliceInfo).getTotalElements();
			this.totalPages = ((Page<T>) sliceInfo).getTotalPages();
			this.hasNext = sliceInfo.hasNext();
		}
		else {
			this.hasNext = sliceInfo.hasNext();
		}
		this.results = content;
	}
}
