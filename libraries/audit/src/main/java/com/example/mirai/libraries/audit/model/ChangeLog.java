package com.example.mirai.libraries.audit.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.util.CaseUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.RevisionType;

@Getter
@Setter
public class ChangeLog {
	List<Entry> entries;

	public ChangeLog() {
		entries = new ArrayList<>();
	}

	public void addEntry(User updater, Date updatedOn, Integer revisionNumber, RevisionType revisionType, Object value, Long entityId) {
		Entry entry = new Entry(updater, updatedOn, revisionNumber, revisionType, value, entityId);
		entries.add(entry);
	}

	public void addEntry(User updater, Date updatedOn, Integer revisionNumber, RevisionType revisionType, String property, Object value, Object oldValue, Long entityId) {
		property = CaseUtil.convertCamelToSnakeCase(property);
		Entry entry = new Entry(updater, updatedOn, revisionNumber, revisionType, property, value, oldValue, entityId);
		entries.add(entry);
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public class Entry {
		User updater;

		Date updatedOn;

		Integer revision;

		RevisionType revisionType;

		String property;

		Object value;

		Object oldValue;

		Long id;

		public Entry(User updater, Date updatedOn, Integer revision, RevisionType revisionType, Object value, Long entityId) {
			this.updater = updater;
			this.updatedOn = updatedOn;
			this.revision = revision;
			this.revisionType = revisionType;
			this.value = value;
			this.id = entityId;
		}
	}
}
