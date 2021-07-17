package com.example.mirai.projectname.notificationservice.engine.processor.review.extractor;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import com.example.mirai.libraries.notification.engine.processor.RoleExtractorInterface;
import com.example.mirai.projectname.notificationservice.engine.processor.review.ReviewPropertyExtractorUtil;
import com.example.mirai.projectname.notificationservice.engine.processor.review.role.ReviewEntryAssigneeRole;
import com.example.mirai.projectname.notificationservice.engine.processor.review.role.ReviewExecutorRole;
import com.example.mirai.projectname.notificationservice.engine.processor.review.role.ReviewTaskAssigneeRole;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component("ReviewValidationStarted")
public class ReviewValidationStartedRoleExtractor implements RoleExtractorInterface {
	@Override
	public Boolean isStatusChangeEvent() {
		return true;
	}
	@Override
	public Set<BaseRole> getProcessors(Event event) throws IOException {
		Set<BaseRole> baseRoles = new HashSet();
		Map data = (Map) event.getData();

		//track the recipients to avoid duplicate emails
		List<User> recipients = new ArrayList<>();
		Long entityId = Long.parseLong("" + ReviewPropertyExtractorUtil.getReviewIdFromReviewAggregate(data));

		Map executorProperties = ReviewPropertyExtractorUtil.getReviewExecutorFromReviewAggregate(data);
		User executor = (User) getObjectMapper().convertValue(executorProperties, User.class);
		recipients.add(executor);
		baseRoles.add(new ReviewExecutorRole(event, "ReviewExecutor", executor, "ReviewValidationStarted", entityId, entityId));
		List<Map> reviewTasks = ReviewPropertyExtractorUtil.getReviewTasksFromReviewAggregate(data);
		for (Map reviewTaskProperties : reviewTasks) {
			Map reviewTaskAssigneeProperties = ReviewPropertyExtractorUtil.getReviewTaskAssigneeFromReviewTask(reviewTaskProperties);
			User reviewTaskAssignee = (User) getObjectMapper().convertValue(reviewTaskAssigneeProperties, User.class);
			Object id = ReviewPropertyExtractorUtil.getReviewTaskIdFromReviewTask(reviewTaskProperties);
			if (recipients.stream().filter(user -> user.getUserId().equals(reviewTaskAssignee.getUserId())).findFirst().isEmpty()) {
				baseRoles.add(new ReviewTaskAssigneeRole(event, "ReviewTaskAssignee", reviewTaskAssignee, "ReviewValidationStarted", Long.parseLong(id.toString()), entityId));
				recipients.add(reviewTaskAssignee);
			}
		}

		List<Map> reviewEntries = ReviewPropertyExtractorUtil.getReviewEntriesFromReviewAggregate(data);
		for (Map reviewEntryProperties : reviewEntries) {
			//get Review entry Assignee
			Map reviewEntryAssigneeProperties = (Map) ReviewPropertyExtractorUtil.getReviewEntryPropertyFromReviewEntry(reviewEntryProperties, "assignee");
			User reviewEntryAssignee = (User) getObjectMapper().convertValue(reviewEntryAssigneeProperties, User.class);
			Object id = ReviewPropertyExtractorUtil.getReviewEntryPropertyFromReviewEntry(reviewEntryProperties, "id");
			if (recipients.stream().filter(user -> user.getUserId().equals(reviewEntryAssignee.getUserId())).findFirst().isEmpty()) {
				baseRoles.add(new ReviewEntryAssigneeRole(event, "ReviewEntryAssignee", reviewEntryAssignee, "ReviewValidationStarted", Long.parseLong(id.toString()), entityId));
				recipients.add(reviewEntryAssignee);
			}
		}

		return baseRoles;
	}
}
