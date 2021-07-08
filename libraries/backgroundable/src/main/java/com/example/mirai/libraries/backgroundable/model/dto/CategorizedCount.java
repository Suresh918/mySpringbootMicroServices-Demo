package com.example.mirai.libraries.backgroundable.model.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CategorizedCount {
	int totalItems;

	List<Category> categories = new ArrayList<>();

	public void addCategory(Category category) {
		this.categories.add(category);
	}

	@Data
	public class Category {
		String name;

		int totalItems;
	}
}
