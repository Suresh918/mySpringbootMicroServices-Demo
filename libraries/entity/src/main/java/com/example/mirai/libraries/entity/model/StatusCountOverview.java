package com.example.mirai.libraries.entity.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusCountOverview {
	List<Series> series;

	private Integer name;

	private String label;

	private Long value;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Series {

		private String name;

		private Long value;

	}
}

