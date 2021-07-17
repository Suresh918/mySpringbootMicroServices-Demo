package com.example.mirai.projectname.notificationservice.engine.processor.usersettings.role;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.notificationservice.engine.processor.shared.role.MychangeBaseRole;
import com.jayway.jsonpath.JsonPath;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Setter
@Getter
//@Component
public class LastLoggedInReportRole extends MychangeBaseRole {
	User assignee;

	public LastLoggedInReportRole(Event event, String role, User assignee, String category, Long entityId, Long id) {
		super(event, role, category, entityId, id);
		this.recipient = assignee;
	}

	@Override
	public String getTitle() {
		return null;
	}

	public List<String> getLastLoggedUseremailsList(){
		LinkedHashMap<List,List> data = (LinkedHashMap) event.getData();
		List<Map> lastloggedUsersList = JsonPath.parse(data).read("$.user_ids_list");
		List<String> emailsList = new ArrayList<>();
		lastloggedUsersList.stream().forEach(item -> {
			emailsList.add(item.get("email").toString());
		});

		return emailsList;
	}
}
