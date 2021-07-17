package com.example.mirai.projectname.services.configuration.tag.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Tag {
	@Id
	private String name;

	@NotEmpty
	private String label;

	@NotNull
	private Boolean active = true;
}
