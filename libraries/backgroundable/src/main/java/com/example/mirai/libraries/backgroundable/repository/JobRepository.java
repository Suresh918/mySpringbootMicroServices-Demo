package com.example.mirai.libraries.backgroundable.repository;

import java.sql.Timestamp;
import java.util.List;

import com.example.mirai.libraries.backgroundable.model.Job;
import com.example.mirai.libraries.backgroundable.model.JobId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JobRepository extends JpaRepository<Job, JobId> {
	@Query("SELECT j FROM Job j JOIN j.groups g WHERE g IN ?1 and j.parentName = ?2")
	List<Job> getAllByGroupsInAndParentName(List<String> groups, String parentName);
	@Query("SELECT j FROM Job j JOIN j.groups g WHERE g IN ?1 and j.parentName IN ?2")
	List<Job> getAllByGroupsInAndParentNameIn(List<String> groups, List<String> parentNames);
	
	Job findFirstJobByParentNameEqualsAndParentIdEqualsOrderByScheduledOnDesc(String parentName, String sciaId);

	void deleteJobsByScheduledOnBeforeAndStatus(Timestamp olderThanDays, Integer statusCode);
}
