package com.example.mirai.services.userservice.favorite.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Case implements Serializable {
	String id;

	String name;

	String type;
}
