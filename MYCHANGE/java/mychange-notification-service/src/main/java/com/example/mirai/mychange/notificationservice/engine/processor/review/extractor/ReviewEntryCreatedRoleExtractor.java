package com.example.mirai.projectname.notificationservice.engine.processor.review.extractor;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import com.example.mirai.libraries.notification.engine.processor.RoleExtractorInterface;
import com.example.mirai.projectname.notificationservice.engine.processor.review.ReviewPropertyExtractorUtil;
import com.example.mirai.projectname.notificationservice.engine.processor.review.role.ReviewEntryAssigneeRole;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component("ReviewEntryCreated")
public class ReviewEntryCreatedRoleExtractor implements RoleExtractorInterface {
	@Override
	public Set<BaseRole> getProcessors(Event event) throws IOException {
		Set<BaseRole> baseRoles = new HashSet();
		Map data = (Map) event.getData();

		Long entityId = Long.parseLong("" + ReviewPropertyExtractorUtil.getReviewIdFromReviewAggregate(data));

		List<Map> reviewEntries = ReviewPropertyExtractorUtil.getReviewEntriesFromReviewAggregate(data);
		Map reviewEntryProperties = reviewEntries.get(0);
		Object id = ReviewPropertyExtractorUtil.getReviewEntryIdFromReviewEntry(reviewEntryProperties);
		Map reviewEntryAssigneeProperties = (Map) ReviewPropertyExtractorUtil.getReviewEntryAssigneeFromReviewEntry(reviewEntryProperties);
		User reviewEntryAssignee = (User) getObjectMapper().convertValue(reviewEntryAssigneeProperties, User.class);
		baseRoles.add(new ReviewEntryAssigneeRole(event, "ReviewEntryAssignee", reviewEntryAssignee, "ReviewEntryCreated", Long.parseLong(id.toString()), entityId));
		return baseRoles;
	}
}
