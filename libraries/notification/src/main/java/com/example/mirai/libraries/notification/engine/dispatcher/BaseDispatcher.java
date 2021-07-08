package com.example.mirai.libraries.notification.engine.dispatcher;

import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import com.fasterxml.jackson.core.JsonProcessingException;

public abstract class BaseDispatcher {
	BaseRole baseRole;

	public BaseDispatcher(BaseRole baseRole) {
		this.baseRole = baseRole;
	}

	abstract public void dispatch() throws JsonProcessingException;
}
