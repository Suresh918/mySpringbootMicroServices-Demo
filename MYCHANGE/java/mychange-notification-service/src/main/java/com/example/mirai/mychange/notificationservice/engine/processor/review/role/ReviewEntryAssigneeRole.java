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
public class ReviewEntryAssigneeRole extends ReviewRole {
	User assignee;

	public ReviewEntryAssigneeRole(Event event, String role, User assignee, String category, Long id, Long entityId) throws IOException {
		super(event, role, category, entityId, id);
		this.assignee = assignee;
		this.recipient = assignee;
	}

	public String getAssignee() {
		return formatUserName(assignee);
	}

	public String getAssigneePhotoUrl() {
		return getPhotoUrl(assignee.getUserId());
	}

	public String getReviewEntryStatus() {
		Object reviewEntry = ReviewPropertyExtractorUtil.getReviewEntryFromReviewAggregate((Map) getEvent().getData(), id);
		Integer status = (Integer) ReviewPropertyExtractorUtil.getReviewEntryPropertyFromReviewEntry((Map) reviewEntry, "status");
		return Statuses.ReviewEntryStatus.getLabelByCode(status);
	}

	public String getReviewEntryCreator() {
		Object reviewEntry = ReviewPropertyExtractorUtil.getReviewEntryFromReviewAggregate((Map) getEvent().getData(), id);
		Object creator = ReviewPropertyExtractorUtil.getReviewEntryPropertyFromReviewEntry((Map) reviewEntry, "creator");
		// Integer status = (Integer) PropertyExtractorUtil.getReviewPropertyFromReviewAggregate((Map) getEvent().getData(), "status");
		return formatUserName(getObjectMapper().convertValue(creator, User.class));
	}

	public String getReviewEntryCreatorPhotoUrl() {
		Object reviewEntry = ReviewPropertyExtractorUtil.getReviewEntryFromReviewAggregate((Map) getEvent().getData(), id);
		Object creator = ReviewPropertyExtractorUtil.getReviewEntryPropertyFromReviewEntry((Map) reviewEntry, "creator");
		// Integer status = (Integer) PropertyExtractorUtil.getReviewPropertyFromReviewAggregate((Map) getEvent().getData(), "status");
		return getPhotoUrl(getObjectMapper().convertValue(creator, User.class).getUserId());
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
			case "ReviewEntryCompleted":
				return "Defect Done RP " + rpId + " (" + ecn + ")";
			case "ReviewEntryCreated":
			case "ReviewEntryAssigned":
				return "Defect Assigned RP " + rpId + " (" + ecn + ")";
			case "ReviewEntryUnassigned":
				return "Defect Unassigned RP " + rpId + " (" + ecn + ")";
			default:
				break;
		}

		return null;
	}

}
