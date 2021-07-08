package com.example.mirai.libraries.notification.engine.processor;

import java.io.IOException;
import java.util.Set;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.Event;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface RoleExtractorInterface {
	Set<BaseRole> getProcessors(Event event) throws IOException;

	default Boolean isStatusChangeEvent() {
		return false;
	}

	default ObjectMapper getObjectMapper() { return ApplicationContextHolder.getApplicationContext().getBean(ObjectMapper.class);}
}
