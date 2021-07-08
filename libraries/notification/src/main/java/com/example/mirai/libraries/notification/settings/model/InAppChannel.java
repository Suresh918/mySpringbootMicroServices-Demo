package com.example.mirai.libraries.notification.settings.model;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InAppChannel implements Serializable {
	private Boolean enabled;
}
