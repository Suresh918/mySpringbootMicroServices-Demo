package com.example.mirai.libraries.backgroundable.service;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.example.mirai.libraries.backgroundable.annotation.Backgroundable;
import com.example.mirai.libraries.backgroundable.config.BackgroundableSchedulerCleanupConfigurationProperties;
import com.example.mirai.libraries.backgroundable.config.JobOwnerGroupConfigurationProperties;
import com.example.mirai.libraries.backgroundable.model.Context;
import com.example.mirai.libraries.backgroundable.model.Error;
import com.example.mirai.libraries.backgroundable.model.Job;
import com.example.mirai.libraries.backgroundable.model.JobId;
import com.example.mirai.libraries.backgroundable.model.JobStatus;
import com.example.mirai.libraries.backgroundable.model.dto.CategorizedCount;
import com.example.mirai.libraries.backgroundable.model.dto.CategorizedJob;
import com.example.mirai.libraries.backgroundable.model.dto.CategoryName;
import com.example.mirai.libraries.backgroundable.repository.JobRepository;
import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.core.model.ContextInterface;
import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.util.ReflectionUtil;
import com.example.mirai.libraries.util.SpelParser;
import com.example.mirai.libraries.websecurity.PrincipalAwareJwtAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@Service
@Slf4j
public class JobService {
	private final JobRepository jobRepository;

	private final LocalVariableTableParameterNameDiscoverer discoverer;

	private final ApplicationEventPublisher applicationEventPublisher;

	private final JobOwnerGroupConfigurationProperties jobOwnerGroupConfigurationProperties;

	private final BackgroundableSchedulerCleanupConfigurationProperties backgroundableSchedulerCleanupConfigurationProperties;

	public JobService(JobRepository jobRepository, ApplicationEventPublisher applicationEventPublisher,
					  JobOwnerGroupConfigurationProperties jobOwnerGroupConfigurationProperties,
					  BackgroundableSchedulerCleanupConfigurationProperties backgroundableSchedulerCleanupConfigurationProperties) {
		this.jobRepository = jobRepository;
		this.applicationEventPublisher = applicationEventPublisher;
		this.jobOwnerGroupConfigurationProperties = jobOwnerGroupConfigurationProperties;
		this.backgroundableSchedulerCleanupConfigurationProperties = backgroundableSchedulerCleanupConfigurationProperties;
		this.discoverer = new LocalVariableTableParameterNameDiscoverer();
	}

	List<Job> filterJobByCategory(List<Job> jobList, CategoryName categoryCode) {
		return jobList.stream().filter(job -> job.getStatus() == categoryCode.getCategoryCode()).collect(Collectors.toList());
	}

	public CategorizedJob getCategorizedJob(String parentName) {
		log.info("fetching jobs by category and name " + parentName);
		//List<Job> jobList = jobRepository.getAllByGroupsInAndParentName(getGroups(), parentName);
		EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);
		Query query = entityManager.createNativeQuery("SELECT * FROM job j JOIN job_owner_groups jog ON j.id=jog.id WHERE  j.parent_name = ?1 AND jog.groups IN ?2", Job.class);
		query.setParameter(1, parentName);
		query.setParameter(2, getGroups());
		return getCategorizedJobByJobList(query.getResultList());
	}

	public CategorizedJob getCategorizedJob(List<String> parentNames) {
		//List<Job> jobList = jobRepository.getAllByGroupsInAndParentNameIn(getGroups(), parentNames);
		EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);
		Query query = entityManager.createNativeQuery("SELECT * FROM job j JOIN job_owner_groups jog ON j.id=jog.id WHERE  j.parent_name IN ?1 AND jog.groups IN ?2", Job.class);
		query.setParameter(1, parentNames);
		query.setParameter(2, getGroups());
		return getCategorizedJobByJobList(query.getResultList());
	}

	private CategorizedJob getCategorizedJobByJobList(List<Job> jobList) {
		CategorizedJob categorizedJob = new CategorizedJob();
		AtomicInteger backgroundableJobTotalItems = new AtomicInteger();
		List<Job> jobListWithoutDuplicates = new ArrayList(new HashSet(jobList));

		Arrays.stream(CategoryName.values()).forEach(categoryName -> {
			AtomicInteger categoryTotalItems = new AtomicInteger();
			List<Job> jobsOfCategory = filterJobByCategory(jobListWithoutDuplicates, categoryName);
			CategorizedJob.Category category = categorizedJob.new Category();
			categorizedJob.addCategory(category);
			category.setName(categoryName.getCategoryLabel());
			category.setTotalItems(jobsOfCategory.size());
			CategorizedJob.Category.SubCategory subCategory = category.new SubCategory();
			category.addSubCategory(subCategory);
			jobsOfCategory.stream().forEach(job -> {
				CategorizedJob.Category.SubCategory.Item item = subCategory.new Item();
				subCategory.addItem(item);
				CategorizedJob.Category.SubCategory.Item.Job jov = item.new Job(job);
				item.setJob(jov);
				backgroundableJobTotalItems.getAndIncrement();
				categoryTotalItems.getAndIncrement();
			});
			category.setTotalItems(categoryTotalItems.get());
		});

		categorizedJob.setTotalItems(backgroundableJobTotalItems.get());

		return categorizedJob;
	}

	private String getId(ProceedingJoinPoint proceedingJoinPoint, Backgroundable backgroundable) {
		Object[] args = proceedingJoinPoint.getArgs();
		Method method = ReflectionUtil.getMethod(proceedingJoinPoint);
		String spel = backgroundable.idGenerator();
		return SpelParser.evaluateSpel(method, args, spel, String.class, "");
	}

	private String getParentId(ProceedingJoinPoint proceedingJoinPoint, Backgroundable backgroundable) {
		Object[] args = proceedingJoinPoint.getArgs();
		Method method = ReflectionUtil.getMethod(proceedingJoinPoint);
		String spel = backgroundable.parentIdExtractor();
		return SpelParser.evaluateSpel(method, args, spel, String.class, null);
	}

	private String getTitle(ProceedingJoinPoint proceedingJoinPoint, Backgroundable backgroundable) {
		Object[] args = proceedingJoinPoint.getArgs();
		Method method = ReflectionUtil.getMethod(proceedingJoinPoint);
		String spel = backgroundable.title();
		return SpelParser.evaluateSpel(method, args, spel, String.class, null);
	}

	private User getExecutor() {
		PrincipalAwareJwtAuthenticationToken principalAwareJwtAuthenticationToken = (PrincipalAwareJwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		return principalAwareJwtAuthenticationToken.getPrincipal();
	}

	private List<String> getGroups() {
		PrincipalAwareJwtAuthenticationToken principalAwareJwtAuthenticationToken = (PrincipalAwareJwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		List<String> roles = principalAwareJwtAuthenticationToken.getAuthorities().stream().map(authority -> authority.getAuthority().substring(5)).collect(Collectors.toList());
		List<String> primaryGroups = this.jobOwnerGroupConfigurationProperties.getPrimaryGroups();
		if (Objects.nonNull(primaryGroups) && !primaryGroups.isEmpty()) {
			roles.retainAll(primaryGroups);
		}
		return roles;
	}

	private Context getContext(ProceedingJoinPoint proceedingJoinPoint, Backgroundable backgroundable, Object retVal) {
		ContextInterface contextInterface = null;
		if (backgroundable.context().equals("[response]") && retVal instanceof ContextInterface) {
			contextInterface = (ContextInterface) retVal;
		}
		else if (!backgroundable.context().equals("[response]")) {
			Object[] args = proceedingJoinPoint.getArgs();
			Method method = ReflectionUtil.getMethod(proceedingJoinPoint);
			String spel = backgroundable.context();

			contextInterface = (!spel.equals("[response]")) ? SpelParser.evaluateSpel(method, args, spel, ContextInterface.class, null) : null;
		}
		return contextInterface == null ? null : new Context(contextInterface.getType(), contextInterface.getContextId(),
				contextInterface.getName(), contextInterface.getStatus());
	}

	public void setJobStatusToCompleted(ProceedingJoinPoint proceedingJoinPoint, Backgroundable backgroundable, Object retVal) {

		String id = getId(proceedingJoinPoint, backgroundable);
		JobId jobId = new JobId(id, backgroundable.name());
		Optional<Job> optionalJob = jobRepository.findById(jobId);


		if (optionalJob.isPresent()) {
			Job job = optionalJob.get();
			job.setStatus(JobStatus.COMPLETED.getStatusCode());
			job.setScheduledOn(new Date());
			job.setExecutor(getExecutor());
			job.setGroups(getGroups());
			job.setContext(getContext(proceedingJoinPoint, backgroundable, retVal));

			String parentId = getParentId(proceedingJoinPoint, backgroundable);
			job.setParentId(parentId);
			job.setParentName(backgroundable.parentName());
			job.setTitle(getTitle(proceedingJoinPoint, backgroundable));

			jobRepository.save(job);
		}
	}

	public void setJobStatusToError(ProceedingJoinPoint proceedingJoinPoint, Throwable throwable, Backgroundable backgroundable) {
		String id = getId(proceedingJoinPoint, backgroundable);
		JobId jobId = new JobId(id, backgroundable.name());
		Optional<Job> optionalJob = jobRepository.findById(jobId);
		Job job;
		String errorCode = null;
		String errorMessage = null;
		try {
			log.info("extracting error code from throwable");
			errorCode = (String) ReflectionUtil.getFieldValue(throwable, "applicationStatusCode");
			log.info("extracted error code from throwable " + errorCode);
		} catch (Exception e) {
			log.info("Error while retrieving error code from exception in background job error code");
		}
		try {
			log.info("extracting error message from throwable");
			errorMessage = (String) ReflectionUtil.getFieldValue(throwable, "message");
			log.info("extracting error message from throwable " + errorMessage);
		} catch (Exception e) {
			log.info("Error while retrieving error code from exception in background job error message");
		}
		Error error = new Error(errorCode, throwable.getClass().getSimpleName(), errorMessage);
		if (optionalJob.isEmpty()) {
			job = new Job(id, backgroundable.name(), new Date(), getExecutor(), getGroups(),
					JobStatus.FAILED.getStatusCode(), 0,
					getParentId(proceedingJoinPoint, backgroundable), backgroundable.parentName(), getTitle(proceedingJoinPoint, backgroundable), null, error);
			jobRepository.save(job);
		}
		else {
			job = optionalJob.get();
			job.setRetryCount(job.getRetryCount());
			job.setStatus(JobStatus.FAILED.getStatusCode());
			job.setScheduledOn(new Date());
			job.setExecutor(getExecutor());
			job.setGroups(getGroups());
			job.setParentId(getParentId(proceedingJoinPoint, backgroundable));
			job.setParentName(backgroundable.parentName());
			job.setTitle(getTitle(proceedingJoinPoint, backgroundable));
			job.setError(error);

			jobRepository.save(job);
		}

		publishErroredJobDetails(job, proceedingJoinPoint);
	}

	public void publishErroredJobDetails(Job job, ProceedingJoinPoint proceedingJoinPoint) {
		try {
			Long timestamp = new Date().getTime();
			String tempEventType = job.getName();
			Object data = Objects.nonNull(proceedingJoinPoint.getArgs()) && Objects.nonNull(proceedingJoinPoint.getArgs()[0]) ? proceedingJoinPoint.getArgs()[0] : job;
			Event event = new Event(tempEventType, "ERROR", job.getParentName(),
					job.getClass().getName(), getExecutor(), data, null, timestamp);
			applicationEventPublisher.publishEvent(event);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new InternalAssertionException("Not Able To Send Message- publishing event failed");
		}
	}

	@Transactional
	public void cleanUpJobs() {
		Integer completedJobsOlderThanDays = backgroundableSchedulerCleanupConfigurationProperties.getCompleted().getOlderThanDays();
		Integer failedJobsOlderThanDays = backgroundableSchedulerCleanupConfigurationProperties.getFailed().getOlderThanDays();

		Timestamp completedJobsOlderThan = new Timestamp(System.currentTimeMillis() - (completedJobsOlderThanDays * 86400000));
		Timestamp failedJobsOlderThan = new Timestamp(System.currentTimeMillis() - (failedJobsOlderThanDays * 86400000));

		jobRepository.deleteJobsByScheduledOnBeforeAndStatus(completedJobsOlderThan, JobStatus.COMPLETED.getStatusCode());
		jobRepository.deleteJobsByScheduledOnBeforeAndStatus(failedJobsOlderThan, JobStatus.FAILED.getStatusCode());
	}

	public void setJobStatusToProcessing(ProceedingJoinPoint proceedingJoinPoint, Backgroundable backgroundable) {
		String id = getId(proceedingJoinPoint, backgroundable);
		JobId jobId = new JobId(id, backgroundable.name());
		Optional<Job> optionalJob = jobRepository.findById(jobId);
		Job job;
		if (optionalJob.isEmpty()) {
			job = new Job(id, backgroundable.name(), new Date(), getExecutor(), getGroups(),
					JobStatus.PROCESSING.getStatusCode(), 0,
					getParentId(proceedingJoinPoint, backgroundable), backgroundable.parentName(), getTitle(proceedingJoinPoint, backgroundable), null, null);
			jobRepository.save(job);
		}
		else if (optionalJob.isPresent()) {
			job = optionalJob.get();
			job.setStatus(JobStatus.PROCESSING.getStatusCode());
			job.setRetryCount(job.getRetryCount() + 1);
			job.setScheduledOn(new Date());
			job.setExecutor(getExecutor());
			job.setGroups(getGroups());
			String parentId = getParentId(proceedingJoinPoint, backgroundable);
			job.setParentId(parentId);
			job.setParentName(backgroundable.parentName());
			job.setTitle(getTitle(proceedingJoinPoint, backgroundable));
			job.setError(null);

			jobRepository.save(job);
		}
	}

	public CategorizedCount getCategorizedCount(String parentName) {
		CategorizedCount categorizedCount = new CategorizedCount();
		AtomicInteger backgroundableJobTotalItems = new AtomicInteger();
		//List<Job> jobList = jobRepository.getAllByGroupsInAndParentName(getGroups(), parentName);
		EntityManager entityManager = (EntityManager) ApplicationContextHolder.getBean(EntityManager.class);
		Query query = entityManager.createNativeQuery("SELECT * FROM job j JOIN job_owner_groups jog ON j.id=jog.id WHERE  j.parent_name = ?1 AND jog.groups IN ?2", Job.class);
		query.setParameter(1, parentName);
		query.setParameter(2, getGroups());
		List<Job> jobList =  query.getResultList();
		List<Job> jobListWithoutDuplicates = new ArrayList(new HashSet(jobList));

		Arrays.stream(CategoryName.values()).forEach(categoryName -> {
			AtomicInteger categoryTotalItems = new AtomicInteger();
			List<Job> jobsOfCategory = filterJobByCategory(jobListWithoutDuplicates, categoryName);
			CategorizedCount.Category category = categorizedCount.new Category();
			categorizedCount.addCategory(category);
			category.setName(categoryName.getCategoryLabel());
			category.setTotalItems(jobsOfCategory.size());
			backgroundableJobTotalItems.getAndAdd(jobsOfCategory.size());
		});

		categorizedCount.setTotalItems(backgroundableJobTotalItems.get());

		return categorizedCount;
	}

	public Job getLatestJobForParent(String parentName, String parentId){
		return this.jobRepository.findFirstJobByParentNameEqualsAndParentIdEqualsOrderByScheduledOnDesc(parentName, parentId);
	}
}
