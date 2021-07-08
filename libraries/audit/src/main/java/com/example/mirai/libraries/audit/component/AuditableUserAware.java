package com.example.mirai.libraries.audit.component;

import java.util.Optional;

import com.example.mirai.libraries.audit.AuditableUserExtractorInterface;
import com.example.mirai.libraries.audit.model.AuditableCreator;
import com.example.mirai.libraries.audit.model.AuditableUpdater;
import com.example.mirai.libraries.core.model.User;
import org.hibernate.envers.RevisionListener;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
public class AuditableUserAware implements AuditorAware<AuditableCreator>, RevisionListener {
	private final AuditableUserExtractorInterface auditableUserExtractor;

	public AuditableUserAware(AuditableUserExtractorInterface auditableUserExtractor) {
		this.auditableUserExtractor = auditableUserExtractor;
	}

	@Override
	public Optional<AuditableCreator> getCurrentAuditor() {
		User user = this.auditableUserExtractor.getAuditableUser();
		if (user != null && user.getUserId() != null) {
			return Optional.of(new AuditableCreator(user));
		}
		if (AuditableUserHolder.user().get() != null) {
			User auditorForSystemUpdate = AuditableUserHolder.user().get();
			AuditableUserHolder.user().remove();
			return Optional.of(new AuditableCreator(auditorForSystemUpdate));
		}
		return Optional.empty();
	}

	@Override
	public void newRevision(Object revisionEntity) {
		AuditableUpdater auditableUpdater = (AuditableUpdater) revisionEntity;
		User user = this.auditableUserExtractor.getAuditableUser();
		if (user != null && user.getUserId() != null) {
			auditableUpdater.initializeUser(user);
		}
		else if (AuditableUserHolder.user().get() != null) {
			auditableUpdater.initializeUser(AuditableUserHolder.user().get());
			AuditableUserHolder.user().remove();
		}

	}

	public static class AuditableUserHolder {
		private static final ThreadLocal<User> user = new ThreadLocal<>();

		private AuditableUserHolder() {
		}

		public static ThreadLocal<User> user() {
			return user;
		}
	}
}
