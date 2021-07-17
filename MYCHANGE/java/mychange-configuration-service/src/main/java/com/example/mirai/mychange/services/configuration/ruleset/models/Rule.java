package com.example.mirai.projectname.services.configuration.ruleset.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rule implements Serializable {
	private String name;

	private String help;
}
