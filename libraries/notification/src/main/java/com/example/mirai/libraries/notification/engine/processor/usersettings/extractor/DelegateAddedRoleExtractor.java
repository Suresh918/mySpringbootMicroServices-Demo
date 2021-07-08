package com.example.mirai.libraries.notification.engine.processor.usersettings.extractor;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import com.example.mirai.libraries.notification.engine.processor.RoleExtractorInterface;
import com.example.mirai.libraries.notification.engine.processor.usersettings.role.DelegateRole;
import com.example.mirai.libraries.notification.engine.processor.usersettings.role.DelegatorRole;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component("DelegateAdded")
public class DelegateAddedRoleExtractor implements RoleExtractorInterface {

	@Override
	public Boolean isStatusChangeEvent() {
		return false;
	}

	@Override
	 public Set<BaseRole> getProcessors(Event event) throws IOException {
		Set<BaseRole> baseRoles = new HashSet();
		ArrayList<Map> data = (ArrayList<Map>) event.getData();

		data.stream().forEach(item -> {
			User delegate = getObjectMapper().convertValue(item, User.class);
			baseRoles.add(new DelegatorRole(event, "DelegatorRole", event.getActor(), delegate, "Delegator", null, null));
			baseRoles.add(new DelegateRole(event, "DelegateRole", delegate, "DelegateAdded", null, null));
		});
		return baseRoles;
	  }


  }

