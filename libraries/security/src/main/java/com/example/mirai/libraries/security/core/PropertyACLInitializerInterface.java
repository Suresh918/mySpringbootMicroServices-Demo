package com.example.mirai.libraries.security.core;

import java.util.Map;
import java.util.Set;

import com.example.mirai.libraries.security.core.model.PropertyAccessRule;

public interface PropertyACLInitializerInterface {
	Map<String, Set<PropertyAccessRule>> getPropertyACL();
}
