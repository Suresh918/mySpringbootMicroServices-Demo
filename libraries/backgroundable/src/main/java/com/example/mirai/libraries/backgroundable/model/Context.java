package com.example.mirai.libraries.backgroundable.model;


import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;

import com.example.mirai.libraries.backgroundable.service.ContextTypeConverter;
import com.example.mirai.libraries.core.model.ContextInterface;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Context implements Serializable, ContextInterface {
	@Convert(converter = ContextTypeConverter.class)
	private String type;

	private String contextId;

	@Column(length = 256)
	private String name;

	private String status;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Context)) return false;
		Context context = (Context) o;
		return Objects.equals(getContextId(), context.getContextId());
	}

}
