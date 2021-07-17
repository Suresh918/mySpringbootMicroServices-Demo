package com.example.mirai.projectname.notificationservice.engine.processor.review.role;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.notificationservice.engine.processor.review.ReviewRole;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReviewExecutorRole extends ReviewRole {
	User executor;

	public ReviewExecutorRole(Event event, String role, User executor, String category, Long entityId, Long id) {
		super(event, role, category, entityId, id);
		this.executor = executor;
		this.recipient = executor;
	}

	public User getExecutor() {
		return executor;
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
			case "ReviewCreated":
				return "Review Started RP " + rpId + " (" + ecn + ")";
			case "ReviewValidationStarted":
				return "Validate RP " + rpId + " (" + ecn + ")";
			case "ReviewCompleted":
				return "Review Completed RP " + rpId + " (" + ecn + ")";
			case "ReviewTaskCompleted":
				return "Review Task Completed RP " + rpId + " (" + ecn + ")";
		}

		return null;
	}

}
