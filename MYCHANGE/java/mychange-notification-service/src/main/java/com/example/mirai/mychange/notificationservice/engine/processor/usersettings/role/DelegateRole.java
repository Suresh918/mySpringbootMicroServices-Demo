package com.example.mirai.projectname.notificationservice.engine.processor.usersettings.role;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.notificationservice.engine.processor.shared.role.MychangeBaseRole;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class DelegateRole extends MychangeBaseRole {
	User assignee;

	public DelegateRole(Event event, String role, User assignee, String category, Long entityId, Long id) {
		super(event, role, category, entityId, id);
		this.recipient = assignee;
	}

	@Override
	public String getTitle() {
		return null;
	}

	public String getDelegatorName(){
		return formatUserName(event.getActor());
	}
}
