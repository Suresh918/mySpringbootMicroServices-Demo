package com.example.mirai.libraries.security.model;

import java.util.Set;

import com.example.mirai.libraries.core.model.CaseAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntityCaseActions {
	private Long entityId;

	private Set<CaseAction> caseActions;
}
