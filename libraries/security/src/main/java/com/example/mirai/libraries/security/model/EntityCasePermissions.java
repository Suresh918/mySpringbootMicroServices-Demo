package com.example.mirai.libraries.security.model;

import com.example.mirai.libraries.core.model.CasePermissions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntityCasePermissions {
	private Long entityId;

	private CasePermissions casePermissions;
}
