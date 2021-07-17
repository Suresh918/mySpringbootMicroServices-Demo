package com.example.mirai.projectname.notificationservice.engine.processor.review.extractor;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import com.example.mirai.libraries.notification.engine.processor.RoleExtractorInterface;
import com.example.mirai.projectname.notificationservice.engine.processor.review.ReviewPropertyExtractorUtil;
import com.example.mirai.projectname.notificationservice.engine.processor.review.role.ReviewExecutorRole;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component("ReviewTaskCompleted")
public class ReviewTaskCompletedRoleExtractor implements RoleExtractorInterface {
	@Override
	public Boolean isStatusChangeEvent() {
		return true;
	}
	@Override
	public Set<BaseRole> getProcessors(Event event) throws IOException {
		Set<BaseRole> baseRoles = new HashSet();
		Map data = (Map) event.getData();

		Map executorProperties = ReviewPropertyExtractorUtil.getReviewExecutorFromReviewAggregate(data);
		User reviewExecutor = (User) getObjectMapper().convertValue(executorProperties, User.class);
		Long entityId = Long.parseLong("" + ReviewPropertyExtractorUtil.getReviewIdFromReviewAggregate(data));

		List<Map> reviewTasks = ReviewPropertyExtractorUtil.getReviewTasksFromReviewAggregate(data);
		//there will be exactly 1 reviewTask in reviewTask completed event
		Map reviewTaskProperties = reviewTasks.get(0);
		Object id = ReviewPropertyExtractorUtil.getReviewTaskIdFromReviewTask(reviewTaskProperties);
		baseRoles.add(new ReviewExecutorRole(event, "ReviewExecutor", reviewExecutor, "ReviewTaskCompleted", entityId, Long.parseLong(id.toString())));
		return baseRoles;
	}
}
