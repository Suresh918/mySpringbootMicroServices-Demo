package com.example.mirai.projectname.services.configuration.form.models;

import java.io.Serializable;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Validators implements Serializable {
	private int minLength;

	private int maxLength;

	private Pattern pattern;
}
