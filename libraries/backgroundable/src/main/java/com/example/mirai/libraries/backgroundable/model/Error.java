package com.example.mirai.libraries.backgroundable.model;


import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Error implements Serializable {
	private String code;

	@Column(length = 1024)
	private String errorClass;

	@Column(
			columnDefinition = "TEXT"
	)
	private String description;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Error)) return false;
		Error context = (Error) o;
		return Objects.equals(errorClass, getErrorClass());
	}

}
