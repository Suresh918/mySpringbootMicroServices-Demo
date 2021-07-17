package com.example.mirai.projectname.notificationservice.engine.processor.review.extractor;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import com.example.mirai.libraries.notification.engine.processor.RoleExtractorInterface;
import com.example.mirai.projectname.notificationservice.engine.processor.review.ReviewPropertyExtractorUtil;
import com.example.mirai.projectname.notificationservice.engine.processor.review.role.ReviewTaskAssigneeRole;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component("ReviewTaskUpdated")
public class ReviewTaskUpdatedRoleExtractor implements RoleExtractorInterface {
	@Override
	public Set<BaseRole> getProcessors(Event event) throws IOException {
		Set<BaseRole> baseRoles = new HashSet();
		Map data = (Map) event.getData();

		Map executorProperties = ReviewPropertyExtractorUtil.getReviewExecutorFromReviewAggregate(data);
		User executor = (User) getObjectMapper().convertValue(executorProperties, User.class);
		Long entityId = Long.parseLong("" + ReviewPropertyExtractorUtil.getReviewIdFromReviewAggregate(data));

		List<Map> reviewTasks = ReviewPropertyExtractorUtil.getReviewTasksFromReviewAggregate(data);
		if (reviewTasks != null) {
			for (Map reviewTaskProperties : reviewTasks) {
				Map reviewTaskAssigneeProperties = ReviewPropertyExtractorUtil.getReviewTaskAssigneeFromReviewTask(reviewTaskProperties);
				Object id = ReviewPropertyExtractorUtil.getReviewTaskIdFromReviewTask(reviewTaskProperties);
				User reviewTaskAssignee = (User) getObjectMapper().convertValue(reviewTaskAssigneeProperties, User.class);
				baseRoles.add(new ReviewTaskAssigneeRole(event, "ReviewTaskAssignee", reviewTaskAssignee, "ReviewTaskUpdated", Long.parseLong(id.toString()), entityId));
			}
		}
		return baseRoles;
	}
}
