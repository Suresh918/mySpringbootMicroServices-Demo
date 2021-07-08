package com.example.mirai.libraries.security.acl.component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

import com.example.mirai.libraries.security.acl.CommonInitializer;
import com.example.mirai.libraries.security.acl.config.AclConfigurationProperties;
import com.example.mirai.libraries.security.core.EntityACLInitializerInterface;
import com.example.mirai.libraries.security.core.component.EntityACL;
import com.example.mirai.libraries.security.core.model.EntityAccessRule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "mirai.libraries.security.acl.entity-acl")
public class EntityAclInitializerImpl extends CommonInitializer implements EntityACLInitializerInterface {
	AclConfigurationProperties aclConfigurationProperties;

	Map<String, HashSet<EntityAccessRule>> map;

	@Autowired
	public EntityAclInitializerImpl(AclConfigurationProperties aclConfigurationProperties) {
		super();
		map = new HashMap<>();
		this.aclConfigurationProperties = aclConfigurationProperties;
	}

	@Override
	protected String getConfigurationFilePath() {
		return aclConfigurationProperties.getEntityAcl();
	}

	@Override
	protected void processKey(Class entityClass) throws JsonProcessingException {
		JsonNode jsonNode = readTree();
		JsonNode entityClassJsonNode = jsonNode.get(entityClass.getCanonicalName());
		if (Objects.isNull(entityClassJsonNode))
			return;
		entityClassJsonNode.fields().forEachRemaining(stringJsonNodeEntry -> {
			String role = stringJsonNodeEntry.getKey();
			JsonNode accessRules = stringJsonNodeEntry.getValue();

			HashSet<EntityAccessRule> entityAccessRules = new HashSet<>();
			for (JsonNode accessRule : accessRules) {
				EntityAccessRule entityAccessRule = null;
				try {
					entityAccessRule = objectMapper.treeToValue(accessRule, EntityAccessRule.class);
					//if fetch rule is specified then filter cannot be used
					if (Objects.nonNull(entityAccessRule.getFetchRule()) && entityAccessRule.getFetchRule().length() > 0)
						entityAccessRule.setFilter(null);
				}
				catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				entityAccessRules.add(entityAccessRule);
			}
			map.put(EntityACL.generateKey(role, entityClass), entityAccessRules);
		});
	}

	@Override
	public String toString() {
		return map.toString();
	}

	@Override
	public Map<String, HashSet<EntityAccessRule>> getEntityACL() {
		return map;
	}

}
