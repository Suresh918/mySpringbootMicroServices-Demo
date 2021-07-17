package com.example.mirai.projectname.services.configuration.link.models;

import java.net.URL;

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
public class Link {
	@Id
	@NotEmpty
	private String name;

	@NotNull
	private URL url;

	private String label;

	private String hint;
}
