package com.example.mirai.projectname.services.configuration.form.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterField implements Serializable {
	private String name;

	private String filterDql;

	private String viewDql;

	private Properties properties;

	private Option[] options;
}
