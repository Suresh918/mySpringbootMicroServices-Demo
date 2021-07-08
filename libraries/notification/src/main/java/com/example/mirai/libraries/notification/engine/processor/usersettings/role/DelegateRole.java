package com.example.mirai.libraries.notification.engine.processor.usersettings.role;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class DelegateRole extends BaseRole {
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
