package com.example.mirai.projectname.services.configuration.form.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Option implements Serializable {
	private String value;

	private String label;

	private int sequence;
}
