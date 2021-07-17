package com.example.mirai.projectname.notificationservice.engine.processor.review;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.notificationservice.engine.processor.shared.role.MychangeBaseRole;

import java.util.Map;

public abstract class ReviewRole extends MychangeBaseRole {
	public ReviewRole(Event event, String role, String category, Long entityId, Long id) {
		super(event, role, category, entityId, id);
	}

	public String getReleasePackageId() {
		String rpId = (String) ReviewPropertyExtractorUtil.getReleasePackageIdFromReviewAggregate((Map) getEvent().getData());
		rpId = rpId == null ? "" : rpId;
		return rpId;
	}

	public String getTeamCenterId() {
		String teamCenterId = (String) ReviewPropertyExtractorUtil.getTeamCenterIdFromReviewAggregate((Map) getEvent().getData());
		return teamCenterId == null ? "" : teamCenterId;
	}

	public String getECN() {
		String ecn = (String) ReviewPropertyExtractorUtil.getECNFromReviewAggregate((Map) getEvent().getData());
		ecn = ecn == null ? "" : ecn;
		return ecn;
	}

	public String getECNReviewLink() {
		String ecnReviewLink = getMychangeLinkConfigurationProperties().getEcn();
		return ecnReviewLink.replace("{TEAMCENTER-ID}", getTeamCenterId());
	}

	public String getDIALink() {
		String diaLink = getMychangeLinkConfigurationProperties().getDiaByChangeNoticeId();
		String cnId = getReleasePackageId().split("-").length > 1 ? getReleasePackageId().split("-")[0] : "";
		return diaLink.replace("{ID}", cnId);
	}

	public String getCaseLink() {
		String baseUrl = getMychangeLinkConfigurationProperties().getMychange().getBaseUrl();
		String reviewDetailsUrl = getMychangeLinkConfigurationProperties().getMychange().getReview();
		return baseUrl + reviewDetailsUrl.replace("{ID}", getReviewId());
	}

	public String getReviewId() {
		Integer id = (Integer) ReviewPropertyExtractorUtil.getReviewPropertyFromReviewAggregate((Map) getEvent().getData(), "id");
		return id == null ? "" : id.toString();
	}

	@Override
	public String getTitle() {
		String title = (String) ReviewPropertyExtractorUtil.getReviewPropertyFromReviewAggregate((Map) getEvent().getData(), "title");
		return title == null ? "" : title;
	}

	public String getReviewDeadline() {
		String completionDate = (String) ReviewPropertyExtractorUtil.getReviewPropertyFromReviewAggregate((Map) getEvent().getData(), "completion_date");
		return completionDate != null ? formatDate(completionDate) : "";
	}

	public String getReviewStatus() {
		Integer status = (Integer) ReviewPropertyExtractorUtil.getReviewPropertyFromReviewAggregate((Map) getEvent().getData(), "status");
		return Statuses.ReviewStatus.getLabelByCode(status);
	}

	public String getReviewCreator() {
		Map creator = (Map) ReviewPropertyExtractorUtil.getReviewPropertyFromReviewAggregate((Map) getEvent().getData(), "creator");
		return formatUserName(creator);
	}

	public String getReviewExecutor() {
		Map executor = (Map) ReviewPropertyExtractorUtil.getReviewPropertyFromReviewAggregate((Map) getEvent().getData(), "executor");
		return formatUserName(executor);
	}

	public String getReviewCreatorPhotoUrl() {
		Map creator = (Map) ReviewPropertyExtractorUtil.getReviewPropertyFromReviewAggregate((Map) getEvent().getData(), "creator");
		return getPhotoUrl((String) creator.get("user_id"));
	}

	public String getExecutorPhotoUrl() {
		Map executor = (Map) ReviewPropertyExtractorUtil.getReviewPropertyFromReviewAggregate((Map) getEvent().getData(), "executor");
		return getPhotoUrl((String) executor.get("user_id"));
	}

	public String getReviewCreatedOn() {
		String createdOnDate = (String) ReviewPropertyExtractorUtil.getReviewPropertyFromReviewAggregate((Map) getEvent().getData(), "created_on");
		return createdOnDate != null ? formatDate(createdOnDate) : "";
	}

	public String getReleasePackageTitle() {
		String rpTitle = (String) ReviewPropertyExtractorUtil.getReleasePackageTitleFromReviewAggregate((Map) getEvent().getData());
		return rpTitle == null ? "" : rpTitle;
	}

	public String getReleasePackageLink() {
		String baseUrl = getMychangeLinkConfigurationProperties().getMychange().getBaseUrl();
		String releasePackageDetailsUrl = getMychangeLinkConfigurationProperties().getMychange().getReleasePackage();
		String url = baseUrl + releasePackageDetailsUrl.replace("{ID}", getReleasePackageId());
		return url;
	}

	public String getSoleReviewTaskAssigneePhotoUrl() {
		Object creator = ReviewPropertyExtractorUtil.getSoleReviewTaskAssigneeFromReviewAggregate((Map) getEvent().getData());
		User assignee = getObjectMapper().convertValue(creator, User.class);
		return getPhotoUrl((String) assignee.getUserId());
	}
	public String getSoleReviewTaskAssignee() {
		Object creator = ReviewPropertyExtractorUtil.getSoleReviewTaskAssigneeFromReviewAggregate((Map) getEvent().getData());
		return formatUserName(getObjectMapper().convertValue(creator, User.class));
	}
}
