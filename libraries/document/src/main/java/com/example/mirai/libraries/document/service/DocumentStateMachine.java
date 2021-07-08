package com.example.mirai.libraries.document.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.example.mirai.libraries.core.exception.CaseActionNotFoundException;
import com.example.mirai.libraries.core.exception.MandatoryFieldViolationException;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.CaseAction;
import com.example.mirai.libraries.core.model.EntityUpdate;
import com.example.mirai.libraries.document.model.Document;
import com.example.mirai.libraries.entity.service.EntityStateMachineDefaultInterface;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import lombok.Data;

import org.springframework.stereotype.Component;

@Component
@Data
public class DocumentStateMachine implements EntityStateMachineDefaultInterface {
	private final CaseActionList caseActionList;

	public EntityUpdate remove(BaseEntityInterface entity) {
		Map<String, Object> changedAttrs = new HashMap<>();

		CaseAction caseAction = caseActionList.getCaseAction(entity.getClass(), "REMOVE");
		if (Objects.isNull(caseAction))
			throw new CaseActionNotFoundException();

		if (!EntityStateMachineDefaultInterface.super.isCaseActionPerformable(entity, caseAction))
			throw new MandatoryFieldViolationException(Document.class.getSimpleName());

		Document document = (Document) entity;
		document.setStatus(2);
		changedAttrs.put("status", 2);
		return new EntityUpdate(document, changedAttrs);
	}
}
