package com.example.mirai.libraries.backgroundable.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobId implements Serializable {
	private String id;

	private String name;
}
