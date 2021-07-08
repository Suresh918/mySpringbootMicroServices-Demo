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
public class DelegatorRole extends BaseRole {
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
