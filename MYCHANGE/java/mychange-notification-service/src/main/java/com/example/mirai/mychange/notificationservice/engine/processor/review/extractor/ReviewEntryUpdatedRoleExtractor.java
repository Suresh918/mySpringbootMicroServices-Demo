package com.example.mirai.projectname.notificationservice.engine.processor.review.extractor;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import com.example.mirai.libraries.notification.engine.processor.RoleExtractorInterface;
import com.example.mirai.projectname.notificationservice.engine.processor.review.ReviewPropertyExtractorUtil;
import com.example.mirai.projectname.notificationservice.engine.processor.review.role.ReviewEntryAssigneeRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component("ReviewEntryUpdated")
@Slf4j
public class ReviewEntryUpdatedRoleExtractor implements RoleExtractorInterface {
	@Override
	public Set<BaseRole> getProcessors(Event event) throws IOException {
		Set<BaseRole> baseRoles = new HashSet();
		Map data = (Map) event.getData();
		log.info("ReviewEntryUpdated ChangedAttributes " + event.getChangedAttributes());
		Long entityId = Long.parseLong("" + ReviewPropertyExtractorUtil.getReviewIdFromReviewAggregate(data));
		if (Objects.nonNull(event.getChangedAttributes()) && event.getChangedAttributes().containsKey("assignee")) {
			List<Map> reviewEntries = ReviewPropertyExtractorUtil.getReviewEntriesFromReviewAggregate(data);
			log.info("ReviewEntryUpdated reviewEntries " + reviewEntries);
			Map reviewEntryProperties = reviewEntries.get(0);
			Object id = ReviewPropertyExtractorUtil.getReviewEntryIdFromReviewEntry(reviewEntryProperties);
			User newAssignee = getObjectMapper().convertValue(event.getChangedAttributes().get("assignee").get("newValue"), User.class);
			User oldAssignee = getObjectMapper().convertValue(event.getChangedAttributes().get("assignee").get("oldValue"), User.class);
			baseRoles.add(new ReviewEntryAssigneeRole(event, "ReviewEntryAssignee", newAssignee, "ReviewEntryAssigned", Long.parseLong(id.toString()), entityId));
			baseRoles.add(new ReviewEntryAssigneeRole(event, "ReviewEntryAssignee", oldAssignee, "ReviewEntryUnassigned", Long.parseLong(id.toString()), entityId));
		}
		log.info("ReviewEntryUpdated baseRoles " + baseRoles);
		return baseRoles;
	}
}
