package com.example.mirai.libraries.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CaseStatus {
	private Integer status;

	private String statusLabel;

	private CasePermissions casePermissions;

	private Long id;
}
