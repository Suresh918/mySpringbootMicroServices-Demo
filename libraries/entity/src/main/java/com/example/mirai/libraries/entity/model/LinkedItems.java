package com.example.mirai.libraries.entity.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LinkedItems {
	private List<LinkCategory> categories;

	@Getter
	@Setter
	@NoArgsConstructor
	public static class LinkCategory {
		private String name;

		private Integer totalItems;

		private String label;

		private List<LinkSubCategory> subCategories;

		public LinkCategory(String name, String label) {
			this.name = name.toUpperCase();
			this.label = label;
			this.subCategories = new ArrayList<>();
			this.subCategories.add(new LinkSubCategory());
			this.totalItems = 0;
		}
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class LinkItem {
		private String id;

		private String type;

		private String title;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class LinkSubCategory {
		private List<LinkItem> items;

		public LinkSubCategory() {
			this.items = new ArrayList<>();
		}
	}
}
