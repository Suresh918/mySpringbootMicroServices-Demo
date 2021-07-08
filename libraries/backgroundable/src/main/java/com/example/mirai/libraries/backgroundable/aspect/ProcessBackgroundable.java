package com.example.mirai.libraries.backgroundable.aspect;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.example.mirai.libraries.backgroundable.annotation.Backgroundable;
import com.example.mirai.libraries.backgroundable.model.LongerThanExpectedException;
import com.example.mirai.libraries.backgroundable.service.JobService;
import com.example.mirai.libraries.websecurity.PrincipalAwareJwtAuthenticationToken;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ProcessBackgroundable implements Ordered {
	@Qualifier("miraiDelegatingSecurityContextAsyncTaskExecutor")
	private final DelegatingSecurityContextAsyncTaskExecutor executor;

	private final JobService jobService;

	public ProcessBackgroundable(JobService jobService,
			DelegatingSecurityContextAsyncTaskExecutor executor) {
		this.jobService = jobService;
		this.executor = executor;
	}

	@Around("@annotation(backgroundable)")
	public Object processAroundBackgroundable(final ProceedingJoinPoint proceedingJoinPoint, final Backgroundable backgroundable) throws Throwable {
		if (backgroundable.timeout() == Long.MAX_VALUE) {
			//no timeout set, implies consumer willing to wait endlessly
			return runInSameThread(proceedingJoinPoint, backgroundable);
		}
		else {
			//timeout set, implies consumer expects response in time, else push processing in background
			return runInSeparateThreadAndReturnAfterTimeout(proceedingJoinPoint, backgroundable);
		}
	}

	private Object runInSameThread(final ProceedingJoinPoint proceedingJoinPoint, final Backgroundable backgroundable) throws Throwable {
		//try {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			System.out.println(((PrincipalAwareJwtAuthenticationToken) securityContext.getAuthentication()).getPrincipal());
			jobService.setJobStatusToProcessing(proceedingJoinPoint, backgroundable);
			return runner(proceedingJoinPoint, backgroundable);
		/*}
		//commented this part as job status is set to complete/error in runner method already
		catch (Exception exception) {
			//if current exception is in the list of backgroundable.exception then do not set status to error
			jobService.setJobStatusToError(proceedingJoinPoint, exception, backgroundable);
			throw exception;
		}
		jobService.setJobStatusToCompleted(proceedingJoinPoint, backgroundable, retVal);
		return retVal;*/
	}

	private Object runInSeparateThreadAndReturnAfterTimeout(final ProceedingJoinPoint proceedingJoinPoint, final Backgroundable backgroundable) throws Throwable {
		CompletableFuture<Object> completableFuture = CompletableFuture.supplyAsync(() -> {
			try {
				SecurityContext securityContext = SecurityContextHolder.getContext();
				System.out.println(((PrincipalAwareJwtAuthenticationToken) securityContext.getAuthentication()).getPrincipal());
				return runner(proceedingJoinPoint, backgroundable);
			}
			catch (Throwable throwable) {
				throw new CompletionException(throwable);
			}
		}, executor).orTimeout(backgroundable.timeout(), TimeUnit.MILLISECONDS);

		try {
			Object obj = completableFuture.join();
			return obj;
		}
		catch (CompletionException completionException) {
			Throwable throwable = completionException.getCause();
			if (throwable instanceof TimeoutException) {
				jobService.setJobStatusToProcessing(proceedingJoinPoint, backgroundable);
				throw new LongerThanExpectedException();
			}
			throw throwable;
		}
	}

	private Object runner(ProceedingJoinPoint proceedingJoinPoint, Backgroundable backgroundable) throws Throwable {
		System.out.println(((PrincipalAwareJwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getPrincipal());
		try {
			Object retVal = proceedingJoinPoint.proceed();
			jobService.setJobStatusToCompleted(proceedingJoinPoint, backgroundable, retVal);
			return retVal;
		}
		catch (Exception exception) {
			exception.printStackTrace();
			jobService.setJobStatusToError(proceedingJoinPoint, exception, backgroundable);
			throw exception;
		}
	}

	@Override
	public int getOrder() {
		return 1;
	}
}
