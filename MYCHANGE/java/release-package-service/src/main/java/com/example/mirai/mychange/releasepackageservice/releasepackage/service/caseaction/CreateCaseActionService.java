package com.example.mirai.projectname.releasepackageservice.releasepackage.service.caseaction;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Resource;

import com.example.mirai.libraries.backgroundable.annotation.Backgroundable;
import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.event.annotation.PublishResponse;
import com.example.mirai.libraries.sapmdg.changerequest.model.ChangeRequest;
import com.example.mirai.libraries.sapmdg.changerequest.model.SapMdgChangeRequest;
import com.example.mirai.libraries.sapmdg.changerequest.service.SapMdgChangeRequestService;
import com.example.mirai.libraries.sapmdg.shared.exception.SapMdgCrAlreadyExistsException;
import com.example.mirai.libraries.sapmdg.shared.exception.SapMdgException;
import com.example.mirai.libraries.teamcenter.ecn.model.Ecn;
import com.example.mirai.libraries.teamcenter.ecn.model.Result;
import com.example.mirai.libraries.teamcenter.ecn.service.EcnService;
import com.example.mirai.libraries.teamcenter.shared.exception.TeamcenterECNAlreadyExists;
import com.example.mirai.libraries.teamcenter.shared.exception.TeamcenterException;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageContext;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageStatus;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageTypes;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.ReleasePackageAggregate;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageStateMachine;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.helper.ReleasePackageUtil;
import com.example.mirai.projectname.releasepackageservice.shared.AggregateEventBuilder;
import com.example.mirai.projectname.releasepackageservice.zecn.service.ZecnServiceInterface;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class CreateCaseActionService  extends CaseActionService {
    @Resource
    CreateCaseActionService self;

    @Value("${com.example.mirai.projectname.releasepackageservice.product-attribute-flow.enabled:true}")
    private Boolean isSapMdgEnabled;

    public CreateCaseActionService(ReleasePackageStateMachine stateMachine, EcnService ecnService, SapMdgChangeRequestService sapMdgChangeRequestService,
                                   ZecnServiceInterface releasePackageZecnService,
                                   DelegatingSecurityContextAsyncTaskExecutor executor) {
        super(stateMachine, ecnService, sapMdgChangeRequestService, releasePackageZecnService, executor);
    }

    @Override
    public CompletableFuture<ReleasePackageContext>[] getStages(ReleasePackage releasePackage) {

        List<CompletableFuture<ReleasePackageContext>> completableFutureList = new ArrayList<>();

        if (Objects.nonNull(releasePackage.getTypes()) && (releasePackage.getTypes().contains(ReleasePackageTypes.HW.getType()) || releasePackage.getTypes().contains(ReleasePackageTypes.PR.getType()))) {
            String teamcenterId = ReleasePackageUtil.getReleasePackageContextId(releasePackage, "TEAMCENTER");
            if (Objects.isNull(teamcenterId) || teamcenterId.length() == 0)
                completableFutureList.add(CompletableFuture.supplyAsync(() -> self.createTeamcenterEcn(releasePackage), executor));
        }
        if(isSapMdgEnabled) {
            String sapMdgId = ReleasePackageUtil.getReleasePackageContextId(releasePackage, "MDG-CR");
            if (Objects.equals(releasePackage.getSapChangeControl(), true) && Objects.nonNull(releasePackage.getTypes())
                    && releasePackage.getTypes().contains(ReleasePackageTypes.HW.name()) && (Objects.isNull(sapMdgId) || Objects.equals(sapMdgId, ""))) {
                completableFutureList.add(CompletableFuture.supplyAsync(() -> self.createMdgChangeRequest(releasePackage), executor));
            }
        }


        return completableFutureList.toArray(new CompletableFuture[completableFutureList.size()]);
    }

    @Override
    public void execute(ReleasePackage releasePackage) {
        CompletableFuture.runAsync(() -> self.caseActionImpl(releasePackage), executor).whenComplete((x, y) -> {
        });
    }

    @PublishResponse(eventType = "CREATE", eventBuilder = AggregateEventBuilder.class, responseClass = ReleasePackageAggregate.class,
            destination = "com.example.mirai.projectname.releasepackageservice.releasepackage")
    @Transactional
    public ReleasePackage caseActionImpl(ReleasePackage releasePackage) {
        return super.caseActionImpl(releasePackage);
    }


    @Backgroundable(name = "CREATE-ECN", idGenerator = "#releasePackage.getId()",
            parentIdExtractor = "#releasePackage.getReleasePackageNumber()", parentName = "RELEASEPACKAGE")
    public ReleasePackageContext createTeamcenterEcn(ReleasePackage releasePackage) {
        String ecnId = ReleasePackageUtil.getReleasePackageContextId(releasePackage, "ECN");

        Ecn ecn = new Ecn();
        ecn.setId(ecnId);
        ecn.setReleasePackageStatus(ReleasePackageStatus.CREATED.getStatusName());
        ecn.setTitle(releasePackage.getTitle());
        ecn.setSapChangeControl(releasePackage.getSapChangeControl());
        ecn.setValidFrom(releasePackage.getPlannedEffectiveDate());
        ecn.setReleasePackageNumber(releasePackage.getReleasePackageNumber());
        Result result = null;
        try {
            result = ecnService.createEngineeringChangeNotice(ecn);
            result.setStatus("SUCCESS");
            self.createZecn(releasePackage);
        } catch (TeamcenterECNAlreadyExists teamcenterECNAlreadyExists) {
            String engineeringChangeNumber = ReleasePackageUtil.getReleasePackageContextId(releasePackage, "ECN");
            ecn = ecnService.getTeamcenterId(engineeringChangeNumber);
            if (Objects.nonNull(ecn) && Objects.nonNull(ecn.getTeamcenterId())) {
                String teamcenterId = ecn.getTeamcenterId();
                result = new Result();
                result.setTeamcenterId(teamcenterId);
                result.setStatus("SUCCESS");
                self.createZecn(releasePackage);
            }
        } catch (TeamcenterException e) {
            throw e;
        }

        if (Objects.isNull(result) || !result.getStatus().equalsIgnoreCase("SUCCESS")) {
            throw new InternalAssertionException("ECN creation in Teamcenter failed.");
        }

        ReleasePackageContext releasePackageContext = new ReleasePackageContext();
        releasePackageContext.setType("TEAMCENTER");
        ReleasePackageContext ecnContext = ReleasePackageUtil.getReleasePackageContext(releasePackage, "ECN");
        releasePackageContext.setContextId(result.getTeamcenterId());
        releasePackageContext.setName(ecnContext.getContextId());

        return releasePackageContext;
    }


    @Backgroundable(name = "CREATE-MDGCR", idGenerator = "#releasePackage.getId()", parentIdExtractor = "#releasePackage.getReleasePackageNumber()",
            parentName = "RELEASEPACKAGE")
    public ReleasePackageContext createMdgChangeRequest(ReleasePackage releasePackage) {
        ChangeRequest mdgChangeRequest = new ChangeRequest();
        mdgChangeRequest.setReleasePackageNumber(releasePackage.getReleasePackageNumber());
        if (Objects.nonNull(releasePackage.getPlmCoordinator()))
            mdgChangeRequest.setPlmUserId(releasePackage.getPlmCoordinator().getUserId());
        mdgChangeRequest.setDescription(releasePackage.getTitle());
        try {
            mdgChangeRequest = sapMdgChangeRequestService.createChangeRequest(mdgChangeRequest);
        } catch (SapMdgCrAlreadyExistsException sapMdgCrAlreadyExistsException) {
            SapMdgChangeRequest sapMdgChangeRequest = sapMdgChangeRequestService.getSapMdgCRId(releasePackage.getReleasePackageNumber()); //getCR by rp number
            String sapMdgId = sapMdgChangeRequest.getMdgCrId();
            mdgChangeRequest = new ChangeRequest();
            mdgChangeRequest.setId(sapMdgId);
        } catch (SapMdgException e) {
            throw e;
        }

        ReleasePackageContext releasePackageContext = new ReleasePackageContext();
        releasePackageContext.setContextId(mdgChangeRequest.getId());
        releasePackageContext.setType("MDG-CR");
        releasePackageContext.setName("MDG-CR-" + mdgChangeRequest.getId());
        return releasePackageContext;

    }

    public void createZecn(ReleasePackage releasePackage) {
        String zecnStatus = ReleasePackageUtil.getReleasePackageContextStatus(releasePackage, "ZECN");
        if (Objects.nonNull(zecnStatus) && zecnStatus.equals("PUBLISHED"))
            return;

        String state = "1 - In Process";
        String ecnId = ReleasePackageUtil.getReleasePackageContextId(releasePackage, "ECN");
        releasePackageZecnService.processAndSendMessage(ecnId, releasePackage.getTitle(), state);

        ReleasePackageContext releasePackageContext = new ReleasePackageContext();
        releasePackageContext.setType("ZECN");
        releasePackageContext.setStatus("PUBLISHED");
    }
}

