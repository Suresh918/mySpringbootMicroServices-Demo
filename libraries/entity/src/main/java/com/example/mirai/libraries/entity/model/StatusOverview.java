package com.example.mirai.libraries.entity.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.example.mirai.libraries.core.model.StatusInterface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Immutable
@Getter
public class StatusOverview {
	List<StatusCount> statusCounts;

	public StatusOverview(List<Object[]> list, StatusInterface[] statuses) {

		statusCounts = new ArrayList<>();

		Iterator iter = list.iterator();

		while (iter.hasNext()) {
			Object[] object = (Object[]) iter.next();
			statusCounts.add(new StatusCount(object[0], object[1], getStatusLabel(statuses, (Integer) object[0])));
		}
		Arrays.stream(statuses).forEach(statusItem -> {
			if (statusCounts.stream().filter(statusCountItem -> statusCountItem.status == statusItem.getStatusCode()).collect(Collectors.toList()).size() == 0) {
				statusCounts.add(new StatusCount(statusItem.getStatusCode(), 0L, statusItem.getStatusLabel()));
			}
		});
		statusCounts.sort(Comparator.comparing(StatusCount::getStatus));
	}

	String getStatusLabel(StatusInterface[] statuses, Integer code) {
		AtomicReference<String> statusLabel = new AtomicReference<>();
		Arrays.stream(statuses).forEach(status -> {
			if (status.getStatusCode() == code) {
				statusLabel.set(status.getStatusLabel());
			}
		});
		return statusLabel.get();
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class StatusCount {
		private Integer status;

		private Long count;

		private String statusLabel;

		public StatusCount(Object status, Object count) {
			this.status = (Integer) status;
			this.count = (Long) count;
		}

		public StatusCount(Integer status, Long count) {
			this.status = status;
			this.count = count;
		}

		public StatusCount(Object status, Object count, String label) {
			this.status = (Integer) status;
			this.count = (Long) count;
			this.statusLabel = label;
		}

	}
}
