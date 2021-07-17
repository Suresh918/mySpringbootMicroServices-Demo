package com.example.mirai.projectname.notificationservice.engine.processor.changerequest.extractor;


import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import com.example.mirai.projectname.notificationservice.engine.processor.changerequest.ChangeRequestPropertyExtractorUtil;
import com.example.mirai.projectname.notificationservice.engine.processor.changerequest.role.ChangeSpecialist1Role;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component("ChangeRequestSubmitted")
public class ChangeRequestSubmittedRoleExtractor extends ChangeRequestRoleExtractor {

	@Override
	public Set<BaseRole> getProcessors(Event event) throws IOException {
		this.category = "ChangeRequestSubmitted";
		Set<BaseRole> baseRoles = super.getProcessors(event);
		User changeSpecialist1 = getChangeSpecialist1((Map) event.getData());
		if(Objects.nonNull(changeSpecialist1)) {
			//remove CS1 as myteammember role and add as ChangeSpecialist1 role
			Optional<BaseRole> changeSpecialist1MemberRole = baseRoles.stream().filter(baseRole -> baseRole.getRecipient().getUserId().equals(changeSpecialist1.getUserId())).findFirst();
			if (changeSpecialist1MemberRole.isPresent()) {
				baseRoles.remove(changeSpecialist1MemberRole.get());
				baseRoles.add(new ChangeSpecialist1Role(event, "ChangeSpecialist1", changeSpecialist1, this.category, this.entityId, this.entityId));
			}
		}
		return baseRoles;
	}

	private User getChangeSpecialist1(Map data) {
		Map changeSpecialist1 = ChangeRequestPropertyExtractorUtil.getChangeSpecialist1FromChangeRequestAggregate(data);
		if (Objects.nonNull(changeSpecialist1)) {
			return getObjectMapper().convertValue(changeSpecialist1, User.class);
		}
		return null;
	}
}
