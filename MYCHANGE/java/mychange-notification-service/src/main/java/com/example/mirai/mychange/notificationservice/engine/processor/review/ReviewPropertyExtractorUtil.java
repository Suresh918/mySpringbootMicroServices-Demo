package com.example.mirai.projectname.notificationservice.engine.processor.review;

import com.jayway.jsonpath.JsonPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ReviewPropertyExtractorUtil {
	// Review Properties
	public static Map getReviewFromReviewAggregate(Map data) {
		if (data == null)
			return null;
		return JsonPath.parse(data).read("$.review");
	}

	public static Object getReviewPropertyFromReviewAggregate(Map data, String property) {
		if (data == null || property == null)
			return null;
		Map reviewProperties = getReviewFromReviewAggregate(data);
		return JsonPath.parse(reviewProperties).read(property);
	}

	public static Object getReviewIdFromReviewAggregate(Map data) {
		if (data == null)
			return null;
		return getReviewPropertyFromReviewAggregate(data, "id");
	}

	public static Object getReleasePackageIdFromReviewAggregate(Map data) {
		if (data == null)
			return null;
		List contexts = (List) getReviewPropertyFromReviewAggregate(data, "contexts");
		if (contexts == null)
			return null;
		for (Object context : contexts) {
			Map map = (Map) context;
			if (map == null) continue;
			if (map.get("type").equals("RELEASEPACKAGE"))
				return map.get("context_id");
		}
		return null;
	}

	public static Object getECNFromReviewAggregate(Map data) {
		if (data == null)
			return null;
		List contexts = (List) getReviewPropertyFromReviewAggregate(data, "contexts");
		if (contexts == null)
			return null;
		for (Object context : contexts) {
			Map map = (Map) context;
			if (map == null) continue;
			if (map.get("type").equals("ECN"))
				return map.get("context_id");
		}
		return null;
	}

	public static Object getTeamCenterIdFromReviewAggregate(Map data) {
		if (data == null)
			return null;
		List contexts = (List) getReviewPropertyFromReviewAggregate(data, "contexts");
		if (contexts == null)
			return null;
		for (Object context : contexts) {
			Map map = (Map) context;
			if (map == null) continue;
			if (map.get("type").equals("TEAMCENTER"))
				return map.get("context_id");
		}
		return null;
	}


	public static Object getReviewCreatedOnFromReviewAggregate(Map data) {
		if (data == null)
			return null;
		return getReviewPropertyFromReviewAggregate(data, "createdOn");
	}

	public static Map getReviewExecutorFromReviewAggregate(Map data) {
		if (data == null)
			if (data == null)
				return null;
		return (Map) getReviewPropertyFromReviewAggregate(data, "executor");
	}

	// reviewTask Properties
	public static List<Map> getReviewTaskAggregatesFromReviewAggregate(Map data) {
		if (data == null)
			return null;
		return JsonPath.parse(data).read("$.review_tasks");
	}

	public static List<Map> getReviewTasksFromReviewAggregate(Map data) {
		if (data == null)
			return null;
		List<Map> reviewTasks = new ArrayList<>();
		List<Map> reviewTaskAggregates = getReviewTaskAggregatesFromReviewAggregate(data);
		if (reviewTaskAggregates == null)
			return reviewTasks;

		for (Map reviewTaskAggregate : reviewTaskAggregates) {
			if (reviewTaskAggregate == null)
				continue;
			reviewTasks.add(getReviewTaskFromReviewTaskAggregate(reviewTaskAggregate));
		}
		return reviewTasks;
	}

	public static Map getReviewTaskFromReviewAggregate(Map data, String userId) {
		if (data == null || userId == null)
			return null;

		List<Map> list = getReviewTaskAggregatesFromReviewAggregate(data);
		if (data == null)
			return null;
		for (Map listItem : list) {
			if (listItem == null)
				continue;
			Map reviewTask = (Map) JsonPath.parse(listItem).read( "$.review_task");
			if (reviewTask == null)
				continue;
			Map reviewTaskAssignee = (Map) JsonPath.parse(reviewTask).read( "$.assignee");
			if (reviewTaskAssignee == null)
				continue;
			Object reviewTaskAssigneeUserId = JsonPath.parse(reviewTaskAssignee).read(  "$.user_id");
			if (reviewTaskAssigneeUserId == null)
				continue;
			if (reviewTaskAssigneeUserId.equals(userId))
				return reviewTask;
		}
		return null;
	}

	public static Map getReviewTaskFromReviewTaskAggregate(Map data) {
		if (data == null)
			return null;
		return JsonPath.parse(data).read( "review_task");
	}

	public static Object getReviewTaskPropertyFromReviewTask(Map data, String property) {
		if (data == null || property == null)
			return null;
		return JsonPath.parse(data).read( property);
	}

	public static Map getReviewTaskAssigneeFromReviewTask(Map data) {
		if (data == null)
			return null;
		return (Map) getReviewTaskPropertyFromReviewTask(data, "assignee");
	}

	public static Object getReviewTaskIdFromReviewTask(Map data) {
		if (data == null)
			return null;
		return getReviewTaskPropertyFromReviewTask(data, "id");
	}

	public static Object getReviewTaskPropertyFromReviewAggregate(Map data, String property, String userId) {
		if (data == null || property == null || userId == null)
			return null;
		Map reviewTaskProperties = getReviewTaskFromReviewAggregate(data, userId);
		if (reviewTaskProperties == null)
			return null;
		return JsonPath.parse(reviewTaskProperties).read( property);
	}

	// Review Entry Properties
	public static List<Map> getReviewEntryAggregatesFromReviewTaskAggregate(Map data) {
		if (data == null)
			return null;
		return (List<Map>) JsonPath.parse(data).read( "review_entries");
	}

	public static Map getReviewEntryAssigneeFromReviewEntryAggregate(Map data) {
		if (data == null)
			return null;
		return (Map) JsonPath.parse(data).read( "reviewEntry.assignee");
	}

	public static Map getReviewEntryFromReviewEntryAggregate(Map data) {
		if (data == null)
			return null;
		return (Map) JsonPath.parse(data).read( "review_entry");
	}

	public static List<Map> getReviewEntriesFromReviewAggregate(Map data) {
		if (data == null)
			return null;
		List<Map> reviewEntries = new ArrayList<>();
		List<Map> reviewTaskAggregates = getReviewTaskAggregatesFromReviewAggregate(data);
		if (reviewTaskAggregates == null)
			return reviewEntries;
		for (Map reviewTaskAggregate : reviewTaskAggregates) {
			if (reviewTaskAggregate == null)
				continue;
			List<Map> reviewEntryAggregates = getReviewEntryAggregatesFromReviewTaskAggregate(reviewTaskAggregate);
			if (reviewEntryAggregates == null)
				continue;
			for (Map reviewEntryAggregate : reviewEntryAggregates) {
				if (reviewEntryAggregate == null)
					continue;
				Map reviewEntry = getReviewEntryFromReviewEntryAggregate(reviewEntryAggregate);
				if (reviewEntry == null)
					continue;
				reviewEntries.add(reviewEntry);
			}
		}
		return reviewEntries;
	}

	public static Object getReviewEntryPropertyFromReviewEntry(Map data, String property) {
		if (data == null || property == null)
			return null;
		return JsonPath.parse(data).read( property);
	}

	public static Object getReviewEntryPropertyFromReviewAggregate(Map data, String property, Long id) {
		if (data == null || property == null || id == null)
			return null;
		Map reviewEntry = (Map) getReviewEntryFromReviewAggregate(data, id);
		if (reviewEntry == null)
			return null;
		return getReviewEntryPropertyFromReviewEntry(reviewEntry, property);
	}

	public static Object getReviewEntryFromReviewAggregate(Map data, Long id) {
		if (data == null || id == null)
			return null;
		List<Map> reviewEntries = getReviewEntriesFromReviewAggregate(data);
		if (reviewEntries == null)
			return null;
		for (Map reviewEntry : reviewEntries) {
			if (reviewEntry == null)
				continue;
			Object reviewEntryId = getReviewEntryIdFromReviewEntry(reviewEntry);
			if (reviewEntryId == null)
				continue;
			if (id.equals(Long.parseLong("" + reviewEntryId)))
				return reviewEntry;
		}
		return null;
	}

	public static Object getReviewEntryIdFromReviewEntry(Map data) {
		if (data == null)
			return null;
		return getReviewEntryPropertyFromReviewEntry(data, "id");
	}

	public static Object getReviewEntryAssigneeFromReviewEntry(Map data) {
		if (data == null)
			return null;
		return getReviewEntryPropertyFromReviewEntry(data, "assignee");
	}

	public static Object getReleasePackageTitleFromReviewAggregate(Map data) {
		if (data == null)
			return null;
		List contexts = (List) getReviewPropertyFromReviewAggregate(data, "contexts");
		if (contexts == null)
			return null;
		for (Object context : contexts) {
			Map map = (Map) context;
			if (map == null) continue;
			if (map.get("type").equals("RELEASEPACKAGE"))
				return map.get("name");
		}
		return null;
	}
	public static Map getSoleReviewTaskAssigneeFromReviewAggregate(Map data) {
		if (data == null) {
			return null;
		}
		List<Map> reviewTasks = ReviewPropertyExtractorUtil.getReviewTasksFromReviewAggregate(data);
		Map reviewTask = reviewTasks.get(0);
		if (reviewTask == null)
			return null;
		return (Map) JsonPath.parse(reviewTask).read(  "assignee");
	}

}

