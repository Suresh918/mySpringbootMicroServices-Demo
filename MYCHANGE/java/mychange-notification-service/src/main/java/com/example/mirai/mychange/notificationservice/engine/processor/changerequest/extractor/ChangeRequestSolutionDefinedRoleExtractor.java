package com.example.mirai.projectname.notificationservice.engine.processor.changerequest.extractor;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component("ChangeRequestSolutionDefined")
public class ChangeRequestSolutionDefinedRoleExtractor extends ChangeRequestRoleExtractor {

	@Override
	public Set<BaseRole> getProcessors(Event event) throws IOException {
		this.category = "ChangeRequestSolutionDefined";
		return super.getProcessors(event);
	}
}
