package com.example.mirai.projectname.notificationservice.engine.processor.usersettings.role;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.notificationservice.engine.processor.shared.role.MychangeBaseRole;

public class DelegatorRole extends MychangeBaseRole {
	User assignee;
	User delegate;
	public DelegatorRole(Event event, String role, User assignee, User delegate, String category, Long entityId, Long id) {
		super(event, role, category, entityId, id);
		this.recipient = assignee;
		this.delegate = delegate;
	}

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public boolean ignoreRecipientCheck(){
		return true;
	}

	public String getDelegateName(){
		 return formatUserName(delegate);
	}

}
