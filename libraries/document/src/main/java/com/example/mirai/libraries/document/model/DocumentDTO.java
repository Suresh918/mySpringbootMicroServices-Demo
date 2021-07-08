package com.example.mirai.libraries.document.model;

import java.util.Date;
import java.util.List;

import com.example.mirai.libraries.core.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentDTO {
	private Long id;

	private String name;

	private String type;

	private String description;

	private List<String> tags;

	private Integer status;

	private String statusLabel;

	private Date createdOn;

	private User creator;

	private byte[] content;
}
