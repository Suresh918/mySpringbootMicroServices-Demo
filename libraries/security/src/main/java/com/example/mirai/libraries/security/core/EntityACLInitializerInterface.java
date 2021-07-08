package com.example.mirai.libraries.security.core;

import java.util.HashSet;
import java.util.Map;

import com.example.mirai.libraries.security.core.model.EntityAccessRule;

public interface EntityACLInitializerInterface {
	Map<String, HashSet<EntityAccessRule>> getEntityACL();
}
