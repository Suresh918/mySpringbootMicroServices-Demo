package com.example.mirai.projectname.notificationservice.engine.processor.myteam.extractor;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import com.example.mirai.projectname.notificationservice.engine.processor.myteam.role.MyTeamMemberRole;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component("ChangeRequestMyTeamMemberAdded")
public class ChangeRequestMyTeamMemberAddedRoleExtractor extends MyTeamMemberRoleExtractor {


	public Set<BaseRole> getProcessors(Event event) throws IOException {
		Set<BaseRole> baseRoles = super.getProcessors(event);
		baseRoles.add(new MyTeamMemberRole(event, "MyTeamMember", myTeamMemberUser, "ChangeRequestMyTeamMemberAdded", myTeamMemberId, getEntityId(event)));
		return baseRoles;
	}
}
