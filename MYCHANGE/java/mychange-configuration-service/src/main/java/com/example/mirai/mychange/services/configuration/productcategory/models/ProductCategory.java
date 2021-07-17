package com.example.mirai.projectname.services.configuration.productcategory.models;

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
public class ProductCategory {
	@Id
	private String name;

	@NotEmpty
	private String label;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private Product[] products;
}
