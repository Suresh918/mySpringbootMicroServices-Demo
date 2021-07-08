package com.example.mirai.libraries.security.acl.component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

import com.example.mirai.libraries.core.model.CaseAction;
import com.example.mirai.libraries.security.acl.CommonInitializer;
import com.example.mirai.libraries.security.acl.config.AclConfigurationProperties;
import com.example.mirai.libraries.security.core.CaseActionListInitializerInterface;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "mirai.libraries.security.acl.case-action-list")
public class CaseActionListInitializerImpl extends CommonInitializer implements CaseActionListInitializerInterface {
	AclConfigurationProperties aclConfigurationProperties;

	Map<String, HashSet<CaseAction>> map;

	@Autowired
	public CaseActionListInitializerImpl(AclConfigurationProperties aclConfigurationProperties) {
		super();
		map = new HashMap<>();
		this.aclConfigurationProperties = aclConfigurationProperties;
	}

	@Override
	protected String getConfigurationFilePath() {
		return aclConfigurationProperties.getCaseActionList();
	}

	@Override
	protected void processKey(Class entityClass) throws JsonProcessingException {
		HashSet<CaseAction> caseActionsForEntity = new HashSet<>();

		JsonNode jsonNode = readTree();
		ArrayNode caseActionJsonNodes = (ArrayNode) jsonNode.get(entityClass.getCanonicalName());
		if (Objects.isNull(caseActionJsonNodes))
			return;

		for (JsonNode caseActionJsonNode : caseActionJsonNodes) {
			CaseAction caseAction = objectMapper.treeToValue(caseActionJsonNode, CaseAction.class);
			caseActionsForEntity.add(caseAction);
		}
		map.put(CaseActionList.generateKey(entityClass), caseActionsForEntity);
	}

	@Override
	public Map<String, HashSet<CaseAction>> getCaseActionList() {
		return map;
	}
}
