package com.example.mirai.projectname.services.configuration.form.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Properties implements Serializable {
	private String label;

	private String hint;

	private String placeholder;

	private String group;

	private Help help;

	private Validators validators;
}
