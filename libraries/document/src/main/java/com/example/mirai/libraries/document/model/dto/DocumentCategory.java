package com.example.mirai.libraries.document.model.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentCategory {
	private String category;

	private List<DocumentOverview> documents;

	public DocumentCategory() {
		this.documents = new ArrayList<>();
	}

	public DocumentCategory(String category) {
		this.category = category;
		this.documents = new ArrayList<>();
	}
}
