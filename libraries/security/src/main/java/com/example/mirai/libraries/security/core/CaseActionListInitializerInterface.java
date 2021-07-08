package com.example.mirai.libraries.security.core;

import java.util.HashSet;
import java.util.Map;

import com.example.mirai.libraries.core.model.CaseAction;

public interface CaseActionListInitializerInterface {
	Map<String, HashSet<CaseAction>> getCaseActionList();
}
