package com.example.mirai.libraries.core.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CaseAction {
	Set<String> mandatoryPropertiesRegexps;

	String caseAction;

	Boolean isAllowed;

	String mandatoryPropertiesSpel;

	String filter;

	public CaseAction(String caseActionName) {
		this.caseAction = caseActionName;
	}

	public CaseAction(CaseAction caseAction) {
		this.caseAction = caseAction.caseAction;
		this.isAllowed = caseAction.isAllowed != null ? (caseAction.isAllowed) : null;
		this.mandatoryPropertiesRegexps = new HashSet<>();
		caseAction.mandatoryPropertiesRegexps.stream().forEach(item -> this.mandatoryPropertiesRegexps.add(item));
		this.mandatoryPropertiesSpel = caseAction.getMandatoryPropertiesSpel();
		this.filter = caseAction.getFilter();
	}
}
