package com.example.mirai.libraries.notification.engine.processor.usersettings.extractor;


import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import com.example.mirai.libraries.notification.engine.processor.RoleExtractorInterface;
import com.example.mirai.libraries.notification.engine.processor.usersettings.role.LastLoggedInReportRole;
import com.jayway.jsonpath.JsonPath;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component("LastLoggedInReport")
public class LastLoggedInReportRoleExtractor implements RoleExtractorInterface {

	@Override
	public Boolean isStatusChangeEvent() {
		return false;
	}

	@Override
	 public Set<BaseRole> getProcessors(Event event) throws IOException {
		Set<BaseRole> baseRoles = new HashSet();
		LinkedHashMap<List,List> data = (LinkedHashMap) event.getData();
			List<Map> receipeintList = JsonPath.parse(data).read("$.recepient_users_list");

		receipeintList.stream().forEach(item -> {
			User recepientuser = new User();
			recepientuser.setUserId(item.get("user_id").toString());
			recepientuser.setEmail(item.get("email").toString());
			recepientuser.setFullName(item.get("full_name").toString());
			//User recepientuser = getObjectMapper().convertValue(item, User.class);
			baseRoles.add(new LastLoggedInReportRole(event, "LastLoggedInReportRole", recepientuser, "LastLoggedInReport", null, null));

		});
		return baseRoles;
	  }


  }

