package com.example.mirai.libraries.backgroundable.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.example.mirai.libraries.core.model.User;
import lombok.Data;

@Data
public class CategorizedJob {
	int totalItems;

	List<Category> categories = new ArrayList<>();

	public void addCategory(Category category) {
		this.categories.add(category);
	}

	@Data
	public class Category {
		String name;

		int totalItems;

		List<SubCategory> subCategories = new ArrayList<>();

		public void addSubCategory(SubCategory subCategory) {
			this.subCategories.add(subCategory);
		}

		@Data
		public class SubCategory {
			List<Item> items = new ArrayList<>();

			public void addItem(Item item) {
				this.items.add(item);
			}

			@Data
			public class Item {
				Job job;

				@Data
				public class Job {
					String id;

					String type;

					String title;

					String action;

					User creator;

					Date scheduledOn;

					Target target;

					Context context;

					Error error;

					public Job(com.example.mirai.libraries.backgroundable.model.Job job) {
						this.id = job.getId();
						this.type = job.getParentName();
						this.title = job.getTitle();
						this.action = job.getName();
						this.scheduledOn = job.getScheduledOn();
						this.creator = job.getExecutor();
						this.target = new Target();
						this.target.id = job.getParentId();
						this.target.type = job.getParentName();
						this.target.location = null;
						if (Objects.nonNull(job.getContext())) {
							this.context = new Context();
							this.context.id = job.getContext().getContextId();
							this.context.type = job.getContext().getType();
							this.context.name = job.getContext().getName();
						}
						if (Objects.nonNull(job.getError())) {
							this.error = new Error();
							this.error.code = job.getError().getCode();
							this.error.description = job.getError().getDescription();
						}

					}

					@Data
					public class Target {
						String id;

						String type;

						String location;
					}

					@Data
					public class Context {
						String id;

						String type;

						String name;
					}

					@Data
					public class Error {
						String code;

						String description;
					}
				}
			}
		}
	}

}
