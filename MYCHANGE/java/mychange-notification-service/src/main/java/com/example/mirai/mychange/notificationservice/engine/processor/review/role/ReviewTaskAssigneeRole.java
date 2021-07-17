package com.example.mirai.projectname.notificationservice.engine.processor.review.role;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.notificationservice.engine.processor.review.ReviewPropertyExtractorUtil;
import com.example.mirai.projectname.notificationservice.engine.processor.review.ReviewRole;
import com.example.mirai.projectname.notificationservice.engine.processor.review.Statuses;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.Map;

@Setter
@Getter
public class ReviewTaskAssigneeRole extends ReviewRole {
	User assignee;

	public ReviewTaskAssigneeRole(Event event, String role, User assignee, String category, Long id, Long entityId) throws IOException {
		super(event, role, category, entityId, id);
		this.assignee = assignee;
		this.recipient = assignee;
	}

	public String getReviewTaskStatus() {
		Integer status = (Integer) ReviewPropertyExtractorUtil.getReviewTaskPropertyFromReviewAggregate((Map) getEvent().getData(), "status", assignee.getUserId());
		return Statuses.ReviewTaskStatus.getLabelByCode(status);
	}

	public String getReviewTaskDueDate() {
		String dueDate = (String) ReviewPropertyExtractorUtil.getReviewTaskPropertyFromReviewAggregate((Map) getEvent().getData(), "due_date", assignee.getUserId());
		return dueDate != null ? formatDate(dueDate) : "";
	}

	public String getReviewStatus() {
		Integer status = (Integer) ReviewPropertyExtractorUtil.getReviewPropertyFromReviewAggregate((Map) getEvent().getData(), "status");
		return Statuses.ReviewStatus.getLabelByCode(status);
	}

	public String getReviewCreator() {
		Map creator = (Map) ReviewPropertyExtractorUtil.getReviewPropertyFromReviewAggregate((Map) getEvent().getData(), "creator");
		return formatUserName(creator);
	}

	public String getReviewCreatorPhotoUrl() {
		Map creator = (Map) ReviewPropertyExtractorUtil.getReviewPropertyFromReviewAggregate((Map) getEvent().getData(), "creator");
		return getPhotoUrl((String) creator.get("user_id"));
	}

	public String getReviewTaskCreator() {
		Object creator = ReviewPropertyExtractorUtil.getReviewTaskPropertyFromReviewAggregate((Map) getEvent().getData(), "creator", assignee.getUserId());
		return formatUserName(getObjectMapper().convertValue(creator, User.class));
	}

	public String getReviewTaskCreatorPhotoUrl() {
		Object creator = ReviewPropertyExtractorUtil.getReviewTaskPropertyFromReviewAggregate((Map) getEvent().getData(), "creator", assignee.getUserId());
		return getPhotoUrl(getObjectMapper().convertValue(creator, User.class).getUserId());
	}

	public String getAssignee() {
		return formatUserName(assignee);
	}

	public String getAssigneePhotoUrl() {
		return getPhotoUrl(assignee.getUserId());
	}

	@Override
	public boolean hasMandatoryProperties() {
		return (getRecipient() != null && getId() != null);
	}

	@Override
	public String getTitle() {
		String rpId = getReleasePackageId();
		String ecn = getECN();

		switch (category) {
			case "ReviewValidationStarted":
				return "Validate RP " + rpId + " (" + ecn + ")";
			case "ReviewCompleted":
				return "Review Completed RP " + rpId + " (" + ecn + ")";
			case "ReviewCreated":
			case "ReviewTaskCreated":
				return "Reviewer Assigned RP " + rpId + " (" + ecn + ")";
			case "ReviewTaskUpdated":
				return "Reviewer Updated RP " + rpId + " (" + ecn + ")";
			case "ReviewTaskDeleted":
				return "Reviewer Removed RP " + rpId + " (" + ecn + ")";
			case "ReviewTaskDueDateSoon":
				return "Reviewer Due Date Soon RP " + rpId + " (" + ecn + ")";
			case "ReviewTaskDueDateExpired":
				return "Reviewer Due Date Expired RP " + rpId + " (" + ecn + ")";
		}

		return null;
	}

}
