package com.example.mirai.projectname.releasepackageservice.releasepackage.service.caseaction;


import java.util.*;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Resource;

import com.example.mirai.libraries.core.model.EntityUpdate;
import com.example.mirai.libraries.sapmdg.changerequest.service.SapMdgChangeRequestService;
import com.example.mirai.libraries.teamcenter.ecn.service.EcnService;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageContext;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageService;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageStateMachine;
import com.example.mirai.projectname.releasepackageservice.zecn.service.ZecnServiceInterface;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public abstract class CaseActionService {
    @Resource
    CaseActionService self;

    @Autowired
    private ReleasePackageService releasePackageService;

    public CaseActionService(ReleasePackageStateMachine stateMachine, EcnService ecnService,
                             SapMdgChangeRequestService sapMdgChangeRequestService, ZecnServiceInterface releasePackageZecnService,
                             DelegatingSecurityContextAsyncTaskExecutor executor) {
        this.stateMachine = stateMachine;
        this.ecnService = ecnService;
        this.sapMdgChangeRequestService = sapMdgChangeRequestService;
        this.releasePackageZecnService = releasePackageZecnService;
        this.executor = executor;
    }

    protected ReleasePackageStateMachine stateMachine;
    protected EcnService ecnService;
    protected SapMdgChangeRequestService sapMdgChangeRequestService;
    protected ZecnServiceInterface releasePackageZecnService;
    protected DelegatingSecurityContextAsyncTaskExecutor executor;

    public abstract void execute(ReleasePackage releasePackage);

    public abstract CompletableFuture<ReleasePackageContext>[] getStages(ReleasePackage releasePackage);

    @Transactional
    public ReleasePackage caseActionImpl(ReleasePackage releasePackageOriginal) {
        //to avoid updating in hibernate cache
        ReleasePackage releasePackage = (ReleasePackage) releasePackageService.getEntityById(releasePackageOriginal.getId());

        //to overwrite status in case of exceptions
        Integer originalStatus = releasePackage.getStatus();

        EntityUpdate entityUpdate = stateMachine.create(releasePackage);
        CompletableFuture<ReleasePackageContext>[] stages = getStages(releasePackage);
        CompletableFuture<Void> combinedCompletableFuture = CompletableFuture.allOf(stages);

        try {
            combinedCompletableFuture.get();
        } catch (Exception exception) {
			log.error("Exception occurred", exception);
        } finally {
            if (combinedCompletableFuture.isCompletedExceptionally()) {
                entityUpdate.removeFromChangedAttrs("status");
                entityUpdate.getEntity().setStatus(originalStatus);
            }

            List<ReleasePackageContext> releasePackageContextList = new ArrayList<>();
            Arrays.stream(stages).forEach(stage -> {
                if (!stage.isCompletedExceptionally()) {
                    try {
                        releasePackageContextList.add(stage.get());
                    } catch (Exception exception) {
						log.error("Exception occurred", exception);
                    }
                }
            });

            /*if (releasePackageContextList.size() > 0) {
                ReleasePackage latestReleasePackage = (ReleasePackage) releasePackageService.getEntityById(releasePackage.getId());
                releasePackageContextList.addAll(latestReleasePackage.getContexts());
                ((ReleasePackage) entityUpdate.getEntity()).setContexts(releasePackageContextList);
                entityUpdate.addToChangedAttrs("contexts", releasePackageContextList);
            }*/

            if (releasePackageContextList.size() > 0) {
                ReleasePackage latestReleasePackage = (ReleasePackage) releasePackageService.getEntityById(releasePackage.getId());
                List<ReleasePackageContext> releasePackageContexts = latestReleasePackage.getContexts();
                releasePackageContextList.forEach(newContext -> {
                    Optional<ReleasePackageContext> currentContext = releasePackageContexts.stream().filter(context -> Objects.equals(context.getType(), newContext.getType()) && Objects.equals(context.getContextId(), newContext.getContextId())).findFirst();
                    if (currentContext.isEmpty() && Objects.nonNull(newContext)) {
                        releasePackageContexts.add(newContext);
                    } else {
                        currentContext.get().setStatus(newContext.getStatus());
                    }
                });
                ((ReleasePackage) entityUpdate.getEntity()).setContexts(releasePackageContexts);
                entityUpdate.addToChangedAttrs("contexts", releasePackageContexts);
            }

            ReleasePackage updatedReleasePackage = (ReleasePackage) releasePackageService.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());

            return combinedCompletableFuture.isCompletedExceptionally() ? null : updatedReleasePackage;
        }
    }

}

