package com.example.mirai.projectname.services.configuration.form.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldGroup implements Serializable {

	@Id
	@NotEmpty
	private String name;

	private List<Field> fields = new ArrayList<Field>();

}
