package com.example.mirai.projectname.services.configuration.form.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Help implements Serializable {
	private String title;

	private String thumbnail;

	private String animation;

	private String message;
}
