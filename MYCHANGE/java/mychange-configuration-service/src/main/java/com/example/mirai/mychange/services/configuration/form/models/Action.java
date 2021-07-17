package com.example.mirai.projectname.services.configuration.form.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Action implements Serializable {
	private String name;

	private String label;

	private String tooltip;

	private String tooltipWhenDisabled;

	private String notApplicableHandle;

	private String confirmationMessage;
}
