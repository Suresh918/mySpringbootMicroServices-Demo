package com.example.mirai.projectname.services.configuration.ruleset.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class RuleSet {
	@NotEmpty
	String label;

	@Id
	private String name;

	@Column(length = 1024)
	private String help;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private Rule[] rules;
}
