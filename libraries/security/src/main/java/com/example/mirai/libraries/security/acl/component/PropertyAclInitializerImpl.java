package com.example.mirai.libraries.security.acl.component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.example.mirai.libraries.security.acl.CommonInitializer;
import com.example.mirai.libraries.security.acl.config.AclConfigurationProperties;
import com.example.mirai.libraries.security.core.PropertyACLInitializerInterface;
import com.example.mirai.libraries.security.core.component.PropertyACL;
import com.example.mirai.libraries.security.core.model.PropertyAccessRule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "mirai.libraries.security.acl.property-acl")
public class PropertyAclInitializerImpl extends CommonInitializer implements PropertyACLInitializerInterface {
	AclConfigurationProperties aclConfigurationProperties;

	Map<String, Set<PropertyAccessRule>> map;

	@Autowired
	public PropertyAclInitializerImpl(AclConfigurationProperties aclConfigurationProperties) {
		super();
		map = new HashMap<>();
		this.aclConfigurationProperties = aclConfigurationProperties;
	}

	@Override
	public String getConfigurationFilePath() {
		return aclConfigurationProperties.getPropertyAcl();
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

			HashSet<PropertyAccessRule> propertyAccessRules = new HashSet<>();
			for (JsonNode accessRule : accessRules) {
				PropertyAccessRule propertyAccessRule = null;
				try {
					propertyAccessRule = objectMapper.treeToValue(accessRule, PropertyAccessRule.class);
				}
				catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				propertyAccessRules.add(propertyAccessRule);
			}
			map.put(PropertyACL.generateKey(role, entityClass), propertyAccessRules);
		});
	}

	@Override
	public String toString() {
		return map.toString();
	}

	@Override
	public Map<String, Set<PropertyAccessRule>> getPropertyACL() {
		return map;
	}
}
