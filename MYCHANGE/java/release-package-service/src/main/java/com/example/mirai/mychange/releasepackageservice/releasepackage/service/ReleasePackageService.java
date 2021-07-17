package com.example.mirai.projectname.releasepackageservice.releasepackage.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.example.mirai.libraries.audit.model.ChangeLog;
import com.example.mirai.libraries.audit.service.AuditServiceDefaultInterface;
import com.example.mirai.libraries.cerberus.diabom.model.DiaBom;
import com.example.mirai.libraries.cerberus.diabom.service.DiaBomService;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.annotation.SecureCaseAction;
import com.example.mirai.libraries.core.annotation.SecureFetchAction;
import com.example.mirai.libraries.core.annotation.SecureFetchCriteria;
import com.example.mirai.libraries.core.annotation.SecureFetchViewCriteria;
import com.example.mirai.libraries.core.annotation.SecurePropertyMerge;
import com.example.mirai.libraries.core.exception.CaseActionNotFoundException;
import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.BaseView;
import com.example.mirai.libraries.core.model.CaseStatus;
import com.example.mirai.libraries.core.model.EntityUpdate;
import com.example.mirai.libraries.core.model.StatusInterface;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.deltareport.model.SolutionItemDelta;
import com.example.mirai.libraries.deltareport.service.DeltaReportService;
import com.example.mirai.libraries.entity.model.Id;
import com.example.mirai.libraries.entity.model.LinkedItems;
import com.example.mirai.libraries.entity.model.StatusCountOverview;
import com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.libraries.event.annotation.PublishResponse;
import com.example.mirai.libraries.gds.exception.GdsException;
import com.example.mirai.libraries.gds.service.GdsUserService;
import com.example.mirai.libraries.hana.er.ErService;
import com.example.mirai.libraries.hana.project.ProjectService;
import com.example.mirai.libraries.hana.projectlead.ProjectLeadService;
import com.example.mirai.libraries.hana.shared.exception.HanaEntityNotFoundException;
import com.example.mirai.libraries.hana.wbs.WbsService;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.libraries.sapmdg.changerequest.model.DeleteMaterialResponse;
import com.example.mirai.libraries.sapmdg.changerequest.service.SapMdgChangeRequestService;
import com.example.mirai.libraries.sapmdg.material.model.Material;
import com.example.mirai.libraries.sapmdg.material.service.SapMdgMaterialService;
import com.example.mirai.libraries.sapmdg.shared.exception.SapMdgCommunicationErrorException;
import com.example.mirai.libraries.sapmdg.shared.exception.SapMdgUnableToObsoleteException;
import com.example.mirai.libraries.security.abac.AbacAwareInterface;
import com.example.mirai.libraries.security.abac.component.AbacProcessor;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.example.mirai.libraries.security.core.component.EntityACL;
import com.example.mirai.libraries.security.core.component.PropertyACL;
import com.example.mirai.libraries.security.core.service.SecurityServiceDefaultInterface;
import com.example.mirai.libraries.security.rbac.RbacAwareInterface;
import com.example.mirai.libraries.security.rbac.component.RbacProcessor;
import com.example.mirai.libraries.teamcenter.ecn.model.DeltaReport;
import com.example.mirai.libraries.teamcenter.ecn.model.Ecn;
import com.example.mirai.libraries.teamcenter.ecn.model.Result;
import com.example.mirai.libraries.teamcenter.ecn.service.EcnService;
import com.example.mirai.projectname.libraries.impacteditem.impacteditem.model.SdlMonitor;
import com.example.mirai.projectname.libraries.impacteditem.impacteditem.service.ImpactedItemService;
import com.example.mirai.projectname.libraries.model.MyChangeRoles;
import com.example.mirai.projectname.releasepackageservice.comment.service.ReleasePackageCommentService;
import com.example.mirai.projectname.releasepackageservice.document.service.ReleasePackageCommentDocumentService;
import com.example.mirai.projectname.releasepackageservice.document.service.ReleasePackageDocumentService;
import com.example.mirai.projectname.releasepackageservice.myteam.service.ReleasePackageMyTeamService;
import com.example.mirai.projectname.releasepackageservice.releasepackage.helper.AuditHelper;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ChangeOwnerType;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.CollaborationObjectCount;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.PrerequisiteReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageCaseActions;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageContext;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageStatus;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageTypes;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.ReleasePackageAggregate;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.ReleasePackageMyTeamDetailsAggregate;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.GlobalSearch;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.LinkedObject;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.Overview;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.ReleasePackageList;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.ReleasePackageMandatoryParameters;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.ReleasePackageReorderPrerequisites;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.SapErDetails;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.SearchSummary;
import com.example.mirai.projectname.releasepackageservice.releasepackage.repository.ReleasePackageRepository;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.caseaction.CreateCaseActionService;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.helper.ReleasePackageUtil;
import com.example.mirai.projectname.releasepackageservice.shared.AggregateEventBuilder;
import com.example.mirai.projectname.releasepackageservice.shared.Constants;
import com.example.mirai.projectname.releasepackageservice.shared.exception.ChangeNoticeStatusInvalidForReadyException;
import com.example.mirai.projectname.releasepackageservice.shared.exception.ChangeObjectPublicationPendingException;
import com.example.mirai.projectname.releasepackageservice.shared.exception.DeleteMaterialFailedException;
import com.example.mirai.projectname.releasepackageservice.shared.exception.NotAllowedToAddHwTypeForCreatorRP;
import com.example.mirai.projectname.releasepackageservice.shared.exception.NotAllowedToRemoveHwOpTypeFromTeamcenterEcn;
import com.example.mirai.projectname.releasepackageservice.shared.exception.SapMdgAdditionalMaterialException;
import com.example.mirai.projectname.releasepackageservice.shared.exception.SdlPublishCaseActionFailedException;
import com.example.mirai.projectname.releasepackageservice.shared.exception.SdlReleaseCaseActionFailedException;
import com.example.mirai.projectname.releasepackageservice.shared.exception.SdlStartCaseActionFailedException;
import com.example.mirai.projectname.releasepackageservice.shared.exception.TeamcenterAdditionalSolutionItemException;
import com.example.mirai.projectname.releasepackageservice.shared.exception.TeamcenterUpdateFailedException;
import com.example.mirai.projectname.releasepackageservice.zecn.service.ZecnServiceInterface;
import com.jayway.jsonpath.JsonPath;
import er.ErDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import project.ProjectDto;
import projectlead.ProjectLeadDto;
import wbs.WbsDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@EntityClass(ReleasePackage.class)
@CacheConfig(cacheNames = {"releasePackageService"})
public class ReleasePackageService implements EntityServiceDefaultInterface, SecurityServiceDefaultInterface, AuditServiceDefaultInterface {
    @Resource
    private ReleasePackageService self;
    @Autowired
    private ReleasePackageInitializer releasePackageInitializer;
    @Autowired
    private CreateCaseActionService createCaseActionService;
    @Autowired
    private ReleasePackagePrerequisitesService releasePackagePrerequisitesService;
    @Autowired
    private ReleasePackageCommentService releasePackageCommentService;
    @Autowired
    private ReleasePackageMyTeamService releasePackageMyTeamService;
    private ErService erService;
    @Value("${com.example.mirai.projectname.releasepackageservice.product-attribute-flow.enabled:true}")
    private Boolean isSapMdgEnabled;

    private DiaBomService diaBomService;
    private EcnService ecnService;
    private DeltaReportService deltaReportService;
    private SapMdgMaterialService sapMdgMaterialService;
    private SapMdgChangeRequestService sapMdgChangeRequestService;

    private ReleasePackageDocumentService releasePackageDocumentService;
    private ReleasePackageCommentDocumentService releasePackageCommentDocumentService;
    private AbacProcessor abacProcessor;
    private RbacProcessor rbacProcessor;
    private EntityACL entityACL;
    private PropertyACL propertyACL;
    private CaseActionList caseActionList;
    private ReleasePackageStateMachine stateMachine;
    private ProjectService projectService;
    private ProjectLeadService projectLeadService;
    private WbsService workBreakdownStructureService;
    private GdsUserService gdsUserService;
    private ZecnServiceInterface releasePackageZecnService;


    private ReleasePackageRepository releasePackageRepository;

    @Autowired
    ImpactedItemService impactedItemService;

    public ReleasePackageService(DiaBomService diaBomService,
                                 EcnService ecnService,
                                 ReleasePackageDocumentService releasePackageDocumentService,
                                 ReleasePackageCommentDocumentService releasePackageCommentDocumentService,
                                 AbacProcessor abacProcessor, RbacProcessor rbacProcessor, EntityACL entityACL, PropertyACL propertyACL,
                                 CaseActionList caseActionList, ReleasePackageStateMachine stateMachine,
                                 ProjectService projectService, ProjectLeadService projectLeadService, WbsService workBreakdownStructureService,
                                 GdsUserService gdsUserService, ReleasePackageRepository releasePackageRepository,
                                 ZecnServiceInterface releasePackageZecnService, DeltaReportService deltaReportService,
                                 SapMdgMaterialService sapMdgMaterialService, SapMdgChangeRequestService sapMdgChangeRequestService, ErService erService) {
        this.diaBomService = diaBomService;
        this.ecnService = ecnService;
        this.releasePackageDocumentService = releasePackageDocumentService;
        this.releasePackageCommentDocumentService = releasePackageCommentDocumentService;
        this.abacProcessor = abacProcessor;
        this.rbacProcessor = rbacProcessor;
        this.entityACL = entityACL;
        this.propertyACL = propertyACL;
        this.caseActionList = caseActionList;
        this.stateMachine = stateMachine;
        this.projectService = projectService;
        this.projectLeadService = projectLeadService;
        this.workBreakdownStructureService = workBreakdownStructureService;
        this.gdsUserService = gdsUserService;
        this.releasePackageZecnService = releasePackageZecnService;
        this.releasePackageRepository = releasePackageRepository;
        this.deltaReportService = deltaReportService;
        this.sapMdgMaterialService = sapMdgMaterialService;
        this.sapMdgChangeRequestService = sapMdgChangeRequestService;
        this.erService = erService;
    }

    @Override
    public EntityACL getEntityACL() {
        return entityACL;
    }

    @Override
    public PropertyACL getPropertyACL() {
        return propertyACL;
    }

    @Override
    public CaseActionList getCaseActionList() {
        return caseActionList;
    }

    @Override
    public AbacAwareInterface getABACAware() {
        return abacProcessor;
    }

    @Override
    public RbacAwareInterface getRBACAware() {
        return rbacProcessor;
    }

    @Override
    public CaseStatus performCaseActionAndGetCaseStatus(Long id, String action) {
        ReleasePackage updatedEntity = (ReleasePackage) performCaseAction(id, action);
        return this.getCaseStatus(updatedEntity);
    }

    public CaseStatus performReleaseAfterDeleteMaterialAndGetCaseStatus(Long id) {
        ReleasePackage entity = (ReleasePackage) self.getEntityById(id);
        ReleasePackage updatedEntity = self.releaseAfterDeleteMaterials(entity);
        return this.getCaseStatus(updatedEntity);
    }

    @Override
    public AggregateInterface performCaseActionAndGetCaseStatusAggregate(Long aLong, String s, Class<AggregateInterface> aClass) {
        return null;
    }

    @SneakyThrows
    public BaseEntityInterface performCaseAction(Long id, String action) {
        ReleasePackageCaseActions caseAction;
        ReleasePackage entity = (ReleasePackage) self.getEntityById(id);
        try {
            caseAction = ReleasePackageCaseActions.valueOf(action.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CaseActionNotFoundException();
        }
        switch (caseAction) {
            case CREATE:
                return self.createCaseAction(entity);
            case RECREATE:
                return self.recreate(entity);
            case READY:
                return self.ready(entity);
            case REREADY:
                return self.readyForRelease(entity);
            case RELEASE:
                return self.release(entity);
            case CLOSE:
                return self.close(entity);
            case OBSOLETE:
                return self.obsolete(entity);
            default:
                throw new CaseActionNotFoundException();
        }
    }


    @Override
    public CaseStatus getCaseStatus(BaseEntityInterface baseEntityInterface) {
        CaseStatus caseStatus = SecurityServiceDefaultInterface.super.getCaseStatus(baseEntityInterface);
        caseStatus.setStatusLabel(ReleasePackageStatus.getLabelByCode(baseEntityInterface.getStatus()));
        return caseStatus;
    }

    @Override
    @SecureCaseAction("READ")
    public BaseEntityInterface get(Long id) {
        return EntityServiceDefaultInterface.super.get(id);
    }

    @Override
    @SecureCaseAction("SUBMIT")
    @PublishResponse(eventType = "SUBMIT", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReleasePackageAggregate.class, destination = "com.example.mirai.projectname.releasepackageservice.releasepackage")
    @Transactional
    public ReleasePackage create(BaseEntityInterface entity) {
        return (ReleasePackage) EntityServiceDefaultInterface.super.create(entity);
    }


    @Transactional
    @SecureCaseAction("SUBMIT_AGGREGATE")
    @PublishResponse(eventType = "SUBMIT_AGGREGATE", eventBuilder = AggregateEventBuilder.class, responseClass = ReleasePackageAggregate.class,
            destination = "com.example.mirai.projectname.releasepackageservice.releasepackage",
            eventEntity = "com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage")
    public ReleasePackageAggregate createAggregate(ReleasePackageAggregate aggregate) {
        ReleasePackageMyTeamDetailsAggregate myTeamDetailsAggregate = aggregate.getMyTeamDetails();
        releasePackageInitializer.initializeReleasePackage(aggregate);
        ReleasePackageAggregate releasePackageAggregate = (ReleasePackageAggregate) EntityServiceDefaultInterface.super.createRootAggregate(aggregate);
        if(Objects.nonNull(myTeamDetailsAggregate) && Objects.nonNull(myTeamDetailsAggregate.getMembers())) {
            releasePackageMyTeamService.addMyTeamMembersFromChangeNotice(myTeamDetailsAggregate.getMembers(), releasePackageAggregate);
        }
        abacProcessor.refreshUserRoles(releasePackageAggregate.getReleasePackage());
        return releasePackageAggregate;
    }

    public Long getSequenceNumberForEcn() {
        return releasePackageRepository.getSequenceNumberForEcn();
    }

    @SecureCaseAction("CREATE")
    public ReleasePackage createCaseAction(BaseEntityInterface entity) {
        ReleasePackage releasePackage = (ReleasePackage) getEntityById(entity.getId());
        stateMachine.checkForMandatoryFields(entity, ReleasePackageCaseActions.CREATE.name());
        if (Objects.nonNull(releasePackage.getTypes()) && releasePackage.getTypes().size() == 1 && releasePackage.getTypes().contains(ReleasePackageTypes.WI.getType())) {
            List<ReleasePackageContext> releasePackageContextsList = releasePackage.getContexts();
            Optional<ReleasePackageContext> optionalChangeObjectContext =releasePackageContextsList.stream().filter(item->item.getType().equalsIgnoreCase(Constants.CHANGE_OBJECT)).findFirst();
            if (optionalChangeObjectContext.isPresent() && Objects.equals(optionalChangeObjectContext.get().getStatus(), Constants.CHANGE_OBJECT_STATUS_NEW)) {
                startSdlWorkFlow(releasePackage);
            }
        }
        createCaseActionService.execute((ReleasePackage) entity);
        return (ReleasePackage) entity;
    }

    private void startSdlWorkFlow(ReleasePackage releasePackage) {
        String changeObjectStatus = impactedItemService.performCaseAction(releasePackage.getReleasePackageNumber(), Constants.START_CASEACTION);
        if (!changeObjectStatus.equalsIgnoreCase(Constants.CHANGE_OBJECT_STATUS_CREATED)) {
            throw new SdlStartCaseActionFailedException();
        } else {
            Map<String, Object> changedAttributes = new HashMap();

            List<ReleasePackageContext> releasePackageContextsList = new ArrayList();
            releasePackageContextsList = releasePackage.getContexts();
            for (ReleasePackageContext rpcontext : releasePackageContextsList) {
                if (rpcontext.getType().equalsIgnoreCase("CHANGEOBJECT")) {
                    rpcontext.setStatus(changeObjectStatus);
                    changedAttributes.put("contexts", rpcontext);

                }
            }
            self.update(releasePackage, changedAttributes);
        }
    }


    @SecureCaseAction("RECREATE")
    @PublishResponse(eventType = "RECREATE", eventBuilder = AggregateEventBuilder.class, responseClass = ReleasePackageAggregate.class,
            destination = "com.example.mirai.projectname.releasepackageservice.releasepackage")
    @Transactional
    public ReleasePackage recreate(BaseEntityInterface entity) {
        stateMachine.checkForMandatoryFields(entity, ReleasePackageCaseActions.RECREATE.name());
        EntityUpdate entityUpdate = stateMachine.recreate(entity);
        checkAndUpdateTeamcenter(entity);
        return (ReleasePackage) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("READY")
    @PublishResponse(eventType = "READY", eventBuilder = AggregateEventBuilder.class, responseClass = ReleasePackageAggregate.class,
            destination = "com.example.mirai.projectname.releasepackageservice.releasepackage")
    @Transactional
    public ReleasePackage ready(BaseEntityInterface entity) {
        ReleasePackage releasePackage= (ReleasePackage) entity;
        checkForChangeNoticeStatus(releasePackage);
        stateMachine.checkForMandatoryFields(entity, ReleasePackageCaseActions.READY.name());
        EntityUpdate entityUpdate = null;
        log.info("release package types " + releasePackage.getTypes());
        if (Objects.nonNull(releasePackage.getTypes()) && releasePackage.getTypes().contains(ReleasePackageTypes.WI.getType())) {
            String changeObjectStatus = "";
            Optional<ReleasePackageContext> changeObjectContext = releasePackage.getContexts().stream().filter(releasePackageContext -> releasePackageContext.getType().equalsIgnoreCase("CHANGEOBJECT")).findFirst();
            if (changeObjectContext.isPresent()) {
                changeObjectStatus = impactedItemService.performCaseAction(releasePackage.getReleasePackageNumber(), "RELEASE");
                if (!Objects.isNull(changeObjectStatus) && changeObjectStatus.equalsIgnoreCase(Constants.CHANGE_OBJECT_STATUS_RELEASED)) {
                    //update change object contexts
                    changeObjectContext.get().setStatus(changeObjectStatus);
                    //self.update(releasePackage, changedAttrs);
                    entityUpdate = stateMachine.ready(entity);
                    Map<String, Object> changedAttrs = entityUpdate.getChangedAttrs();
                    changedAttrs.put("contexts", releasePackage.getContexts());
                    ReleasePackage releasePackageUpdated = (ReleasePackage) entityUpdate.getEntity();
                    releasePackageUpdated.setContexts(releasePackage.getContexts());
                    entityUpdate.setEntity(releasePackageUpdated);
                    entityUpdate.setChangedAttrs(changedAttrs);
                    checkAndUpdateTeamcenter(entity);
                } else if (Objects.isNull(changeObjectStatus) || changeObjectStatus.equalsIgnoreCase(Constants.CHANGE_OBJECT_STATUS_VALIDATED)) {
                    throw new SdlReleaseCaseActionFailedException();
                } else {
                    entityUpdate = stateMachine.ready(entity);
                    checkAndUpdateTeamcenter(entity);
                }
            } else {
                entityUpdate = stateMachine.ready(entity);
                checkAndUpdateTeamcenter(entity);
            }
        } else {
            entityUpdate = stateMachine.ready(entity);
            checkAndUpdateTeamcenter(entity);
        }
        return (ReleasePackage) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    private void checkForChangeNoticeStatus(ReleasePackage releasePackage) {
        Optional<ReleasePackageContext> changeNoticeContext = releasePackage.getContexts().stream().filter(context -> context.getType().equals("CHANGENOTICE")).findFirst();
        if (changeNoticeContext.isPresent()) {
            String changeNoticeStatus = changeNoticeContext.get().getStatus();
            if (Objects.nonNull(changeNoticeStatus) && !changeNoticeStatus.toUpperCase().equals("PLANNED")) {
                throw new ChangeNoticeStatusInvalidForReadyException();
            }
        }
    }

    private void checkAndUpdateTeamcenter(BaseEntityInterface entity) {
        ReleasePackage releasePackageDetails = (ReleasePackage) self.getEntityById(entity.getId());
        if (Objects.nonNull(releasePackageDetails.getTypes()) && releasePackageDetails.getTypes().size() == 1 && releasePackageDetails.getTypes().contains(ReleasePackageTypes.WI.name())) {
            return;
        }
        Optional<ReleasePackageContext> teamcenterContext = releasePackageDetails.getContexts().stream().filter(item -> Objects.equals(item.getType(), "TEAMCENTER")).findFirst();
        if (teamcenterContext.isPresent())
            updateReleasePackageStatusInTeamcenter(entity);
    }

    @SecureCaseAction("REREADY")
    @PublishResponse(eventType = "REREADY", eventBuilder = AggregateEventBuilder.class, responseClass = ReleasePackageAggregate.class,
            destination = "com.example.mirai.projectname.releasepackageservice.releasepackage")
    @Transactional
    public ReleasePackage readyForRelease(BaseEntityInterface entity) {
        stateMachine.checkForMandatoryFields(entity, ReleasePackageCaseActions.REREADY.name());
        EntityUpdate entityUpdate = stateMachine.reready(entity);
        checkAndUpdateTeamcenter(entity);
        return (ReleasePackage) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("RELEASE")
    @PublishResponse(eventType = "RELEASE", eventBuilder = AggregateEventBuilder.class, responseClass = ReleasePackageAggregate.class,
            destination = "com.example.mirai.projectname.releasepackageservice.releasepackage")
    @Transactional
    public ReleasePackage release(BaseEntityInterface entity) throws Exception {
        ReleasePackage releasePackage = (ReleasePackage) entity;
        stateMachine.checkForMandatoryFields(entity, ReleasePackageCaseActions.RELEASE.name());
        if(isSapMdgEnabled) {
            checkForSolutionItemDelta(entity);
            releaseForActivationSapMdgCr(releasePackage);
        }
        /*EntityUpdate entityUpdate = null;
        if (Objects.nonNull(releasePackage.getTypes()) && releasePackage.getTypes().contains(ReleasePackageTypes.WI.getType())) {
            Optional<ReleasePackageContext> changeObjectContext = releasePackage.getContexts().stream().filter(releasePackageContext -> releasePackageContext.getType().equalsIgnoreCase("CHANGEOBJECT")).findFirst();
            if (changeObjectContext.isPresent()) {
                entityUpdate = performCaseActionOnImpactedItems(releasePackage);
            } else {
                entityUpdate = stateMachine.release(entity);
                checkAndUpdateTeamcenter(entity);
            }
        } else {
            entityUpdate = stateMachine.release(entity);
            checkAndUpdateTeamcenter(entity);
        }*/
        EntityUpdate entityUpdate = stateMachine.release(entity);
        checkAndUpdateTeamcenter(entity);
        return (ReleasePackage) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    private EntityUpdate performCaseActionOnImpactedItems(ReleasePackage releasePackage) {
        String changeObjectStatus = impactedItemService.performCaseAction(releasePackage.getReleasePackageNumber(), Constants.PUBLISH_CASEACTION);
        if (changeObjectStatus.equals(Constants.CHANGE_OBJECT_STATUS_PUBLICATION_PENDING))
            throw new ChangeObjectPublicationPendingException();
        if (!Objects.isNull(changeObjectStatus) && changeObjectStatus.equalsIgnoreCase(Constants.CHANGE_OBJECT_STATUS_CLOSED)) {
            Optional<ReleasePackageContext> changeObjectContext = releasePackage.getContexts().stream().filter(releasePackageContext -> releasePackageContext.getType().equalsIgnoreCase("CHANGEOBJECT")).findFirst();
            changeObjectContext.get().setStatus(changeObjectStatus);
            EntityUpdate entityUpdate = stateMachine.close(releasePackage);
            Map<String, Object> changedAttributes = entityUpdate.getChangedAttrs();
            changedAttributes.put("contexts", releasePackage.getContexts());
            //adding release package contexts to entity update
            ReleasePackage releasePackageUpdated = (ReleasePackage) entityUpdate.getEntity();
            releasePackageUpdated.setContexts(releasePackage.getContexts());
            entityUpdate.setEntity(releasePackageUpdated);
            entityUpdate.setChangedAttrs(changedAttributes);
            checkAndUpdateTeamcenter(releasePackage);
            return entityUpdate;
        } else {
            throw new SdlPublishCaseActionFailedException();
        }
    }

    @SecureCaseAction("RELEASE")
    @PublishResponse(eventType = "RELEASE", eventBuilder = AggregateEventBuilder.class, responseClass = ReleasePackageAggregate.class,
            destination = "com.example.mirai.projectname.releasepackageservice.releasepackage")
    @Transactional
    public ReleasePackage releaseAfterDeleteMaterials(BaseEntityInterface entity) {
        EntityUpdate entityUpdate = stateMachine.release(entity);
        if(isSapMdgEnabled){
            try {
                checkForSolutionItemDelta(entity);
            } catch (SapMdgAdditionalMaterialException e) {
                deleteMaterialsFromSapMdg(e.getItems(), ((ReleasePackage) entity).getReleasePackageNumber());
            }
            ReleasePackage releasePackage = (ReleasePackage)self.getEntityById(entity.getId());
            releaseForActivationSapMdgCr(releasePackage);
        }
        checkAndUpdateTeamcenter(entity);
        return (ReleasePackage) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    private void deleteMaterialsFromSapMdg(List<String> items, String rleleasePackageNumber) {
        DeleteMaterialResponse deleteMaterialResponse = sapMdgChangeRequestService.deleteMaterialByMaterialNumber(rleleasePackageNumber, items);
        if (deleteMaterialResponse.getStatus().equals("ERROR")) {
            throw new DeleteMaterialFailedException();
        }
    }

    private void checkForSolutionItemDelta(BaseEntityInterface entity) {
        //delta validation check
        String teamcenterId = getTeamcenterId(entity.getId());

        List<com.example.mirai.libraries.deltareport.model.dto.SolutionItemDelta> solutionItemDeltaList = new ArrayList<>();

        SolutionItemDelta solutionItemDelta = deltaReportService.getSolutionItemDeltaByTeamCenterId(teamcenterId);
        if (Objects.nonNull(solutionItemDelta))
            solutionItemDeltaList = solutionItemDelta.getItemDeltaList();

        List<Material> materialList = new ArrayList<>();
        Optional<ReleasePackageContext> sapMdgCrContext = ((ReleasePackage) entity).getContexts().stream().filter(context -> context.getType().equals("MDG-CR")).findFirst();
        if (!sapMdgCrContext.isEmpty()) {
            String sapMdgContextId = sapMdgCrContext.get().getContextId();
            materialList = sapMdgMaterialService.getMaterialList(sapMdgContextId);
        }
        List<String> missingItemsInTeamcenter = new ArrayList<>();
        List<com.example.mirai.libraries.deltareport.model.dto.SolutionItemDelta> finalSolutionItemDeltaList = solutionItemDeltaList;
        materialList.stream().forEach(material -> {
            Optional<com.example.mirai.libraries.deltareport.model.dto.SolutionItemDelta> optionalSolutionItemDelta = finalSolutionItemDeltaList.stream()
                    .filter(reviewSolutionItemDelta -> Objects.equals(reviewSolutionItemDelta.getSolutionItemId(), material.getId())).findFirst();

            if (optionalSolutionItemDelta.isEmpty()) {
                missingItemsInTeamcenter.add(material.getId());
            }

        });
        List<String> missingItemsInSapMdg = new ArrayList<>();
        List<Material> finalMaterialList = materialList;
        solutionItemDeltaList.stream().forEach(solutionItemDeltaItem -> {
            Optional<Material> optionalMaterial = finalMaterialList.stream()
                    .filter(material -> Objects.equals(material.getId(), solutionItemDeltaItem.getSolutionItemId())).findFirst();

            if (optionalMaterial.isEmpty() && Objects.nonNull(solutionItemDeltaItem.getSolutionItemId())) {
                missingItemsInSapMdg.add(solutionItemDeltaItem.getSolutionItemId());
            }
        });

        if (!missingItemsInSapMdg.isEmpty()) {
            throw new TeamcenterAdditionalSolutionItemException(missingItemsInSapMdg);
        }

        if (!missingItemsInTeamcenter.isEmpty()) {
            throw new SapMdgAdditionalMaterialException(missingItemsInTeamcenter);
        }

    }

    private void releaseForActivationSapMdgCr(ReleasePackage releasePackage) {
        ReleasePackageContext releasePackageSapMdgContext = ReleasePackageUtil.getReleasePackageContext(releasePackage, "MDG-CR");
        if(Objects.nonNull(releasePackageSapMdgContext) &&
                (Objects.isNull(releasePackageSapMdgContext.getStatus()) ||!releasePackageSapMdgContext.getStatus().equalsIgnoreCase("RELEASED"))) {
            sapMdgMaterialService.releaseForActivationMdgCrByReleasePackageNumber(releasePackage.getReleasePackageNumber());
            releasePackageSapMdgContext.setStatus("RELEASED");
            Map<String, Object> changedAttributes = new HashMap();
            changedAttributes.put("contexts", releasePackage.getContexts());
            self.update(releasePackage,changedAttributes);
        }
    }

    @SecureCaseAction("CLOSE")
    @Transactional
    public ReleasePackage close(ReleasePackage releasePackage) {
        return self.closeReleasePackage(releasePackage);
    }

    @PublishResponse(eventType = "CLOSE", eventBuilder = AggregateEventBuilder.class, responseClass = ReleasePackageAggregate.class,
            destination = "com.example.mirai.projectname.releasepackageservice.releasepackage")
    public ReleasePackage closeReleasePackage(ReleasePackage releasePackage) {
        String state = "99";
        stateMachine.checkForMandatoryFields(releasePackage, ReleasePackageCaseActions.CLOSE.name());
        if(isSapMdgEnabled) {
            checkForSolutionItemDelta(releasePackage);
            releaseForActivationSapMdgCr(releasePackage);
        }
        EntityUpdate entityUpdate = null;
        /*if (Objects.nonNull(releasePackage.getTypes()) && releasePackage.getTypes().contains(ReleasePackageTypes.WI.getType())) {
            Optional<ReleasePackageContext> changeObjectContext = releasePackage.getContexts().stream().filter(releasePackageContext -> releasePackageContext.getType().equalsIgnoreCase("CHANGEOBJECT")).findFirst();
            if (changeObjectContext.isPresent()) {
                entityUpdate = performCaseActionOnImpactedItems(releasePackage);
            } else {
                entityUpdate = stateMachine.close(releasePackage);
                checkAndUpdateTeamcenter(releasePackage);
            }
        } else {
            entityUpdate = stateMachine.close(releasePackage);
            checkAndUpdateTeamcenter(releasePackage);
        }*/
        entityUpdate = stateMachine.close(releasePackage);
        log.info("updating in Teamcenter when closing RP " + releasePackage.getReleasePackageNumber() + "::id " + releasePackage.getId());
        checkAndUpdateTeamcenter(releasePackage);
        String ecnId = ReleasePackageUtil.getReleasePackageContextId(releasePackage, "ECN");
        log.info("auto closing Release package ecn id " + ecnId);
        this.releasePackageZecnService.processAndSendMessage(ecnId, releasePackage.getTitle(), state);
        log.info("after zecn message publish auto closing Release package ecn id " + ecnId + " status " + entityUpdate.getEntity().getStatus());
        ReleasePackage updatedReleasePackage = (ReleasePackage) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
        log.info("status of RP " + updatedReleasePackage.getId() + " updated to status " + updatedReleasePackage.getStatus());
        return updatedReleasePackage;

    }

    @SecureCaseAction("OBSOLETE")
    @PublishResponse(eventType = "OBSOLETE", eventBuilder = AggregateEventBuilder.class, responseClass = ReleasePackageAggregate.class,
            destination = "com.example.mirai.projectname.releasepackageservice.releasepackage")
    @Transactional
    public ReleasePackage obsolete(ReleasePackage releasePackage){
        EntityUpdate entityUpdate = null;
        if(isSapMdgEnabled)
            obsoleteSapMdgCr(releasePackage);
        if (releasePackage.getStatus() == ReleasePackageStatus.CREATED.getStatusCode()) {
            String teamcenterId = getTeamcenterId(releasePackage.getId());
            String ecnStatus = ecnService.getEcnStatus(teamcenterId);
            if (Objects.nonNull(ecnStatus)) {
                if (JsonPath.parse(ecnStatus).read("ECNStatus").equals(Constants.ECN_STATUS_Open) || JsonPath.parse(ecnStatus).read("ECNStatus").equals(Constants.ECN_STATUS_OPEN)) {
                    throw new InternalAssertionException("Could not update Teamcenter. ECN does not have release status 'Cancelled'");
                } else if (ecnStatus.equals(Constants.ECN_STATUS_Cancelled) || ecnStatus.equals(Constants.ECN_STATUS_CANCELLED)) {
                    entityUpdate = stateMachine.obsolete(releasePackage);
                    updateReleasePackageStatusInTeamcenter(releasePackage);
                }

            }
        } else {
            entityUpdate = stateMachine.obsolete(releasePackage);
            checkAndUpdateTeamcenter(releasePackage);
        }
        return (ReleasePackage) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    private void obsoleteSapMdgCr(ReleasePackage releasePackage) {
        ReleasePackageContext releasePackageSapMdgContext = ReleasePackageUtil.getReleasePackageContext(releasePackage, "MDG-CR");
        if(Objects.nonNull(releasePackageSapMdgContext) && !releasePackageSapMdgContext.getStatus().equalsIgnoreCase("OBSOLETED")) {
            try{
                sapMdgMaterialService.obsoleteMdgCrByReleasePackageNumber(releasePackage.getReleasePackageNumber());
                releasePackageSapMdgContext.setStatus("OBSOLETED");
                Map<String, Object> changedAttributes = new HashMap();
                changedAttributes.put("contexts", releasePackage.getContexts());
                self.update(releasePackage,changedAttributes);
            } catch (SapMdgUnableToObsoleteException e) {
                throw new SapMdgUnableToObsoleteException();
            } catch (SapMdgCommunicationErrorException e) {
                throw new SapMdgCommunicationErrorException();
            }
        }
    }

    public DiaBom getDiaBom(Long releasePackageId) {
        if (Objects.nonNull(releasePackageId)) {
            ReleasePackage releasePackage = (ReleasePackage) self.getEntityById(releasePackageId);
            Optional<ReleasePackageContext> changeNoticeData = releasePackage.getContexts().stream().filter(context -> context.getType().equals("CHANGENOTICE")).findFirst();
            if (changeNoticeData.isPresent()) {
                return diaBomService.getDiaBomByChangeNoticeId(Long.parseLong(changeNoticeData.get().getContextId()));
            }
        }
        return null;
    }

    @SecureFetchAction
    public BaseEntityList<ReleasePackageList> getReleasePackageList(@SecureFetchViewCriteria  String viewCriteria, Pageable pageable, Optional<String> sliceSelect) {
        Slice<BaseView> releasePackageList = EntityServiceDefaultInterface.super.getEntitiesFromView(viewCriteria, pageable, sliceSelect, ReleasePackageList.class);
        return new BaseEntityList(releasePackageList);
    }

    @SecureFetchAction
    public BaseEntityList<Overview> getReleasePackageOverview(@SecureFetchCriteria String criteria, String viewCriteria, Pageable pageable, Optional<String> sliceSelect) {
        Slice<BaseView> releasePackageOverviewList = EntityServiceDefaultInterface.super.getEntitiesFromView(criteria, viewCriteria, pageable, sliceSelect, Overview.class);
        return new BaseEntityList(releasePackageOverviewList);
    }

    @SneakyThrows
    @SecureCaseAction("UPDATE")
    @SecurePropertyMerge
    @PublishResponse(eventType = "MERGE", eventBuilder = com.example.mirai.libraries.event.AggregateEventBuilder.class,
            responseClass = ReleasePackageAggregate.class, destination = "com.example.mirai.projectname.releasepackageservice.releasepackage")
    @Transactional
    public ReleasePackage mergeEntity(BaseEntityInterface newInst, BaseEntityInterface oldInst, List<String> oldInsChangedAttributeNames, List<String> newInsChangedAttributeNames) {
        List<String> changedProperties = ObjectMapperUtil.getChangedProperties(oldInst, newInst);
        ReleasePackage releasePackage = (ReleasePackage) self.getEntityById(newInst.getId());
        Boolean isReleasePackageInDraftStatus = releasePackage.getStatus().equals(ReleasePackageStatus.DRAFTED.getStatusCode());
        ReleasePackageContext releasePackageTeamCenterContext = ReleasePackageUtil.getReleasePackageContext(releasePackage, "TEAMCENTER");
        if (!isReleasePackageInDraftStatus && Objects.nonNull(releasePackageTeamCenterContext) && (changedProperties.contains("title") || changedProperties.contains("plannedEffectiveDate"))) {
            updateReleasePackageInTeamcenter((ReleasePackage) newInst, changedProperties);
        }
        updateReleasePackageTypes((ReleasePackage) newInst, (ReleasePackage) oldInst, changedProperties);
        return updateReleasePackageAttributes(newInst, oldInst, oldInsChangedAttributeNames, newInsChangedAttributeNames, changedProperties);
    }

    private void updateReleasePackageTypes(ReleasePackage newInst, ReleasePackage oldInst, List<String> changedProperties) {
        if(changedProperties.contains("types")){
            List<String> newTypes = newInst.getTypes();
            List<String> oldTypes = oldInst.getTypes();
            //if H/W is selected and user adds WI (In RP Type) , them also automatically select Operations/Process(BoP)
            if(newTypes.contains("HW") && newTypes.contains("WI") && !newTypes.contains("OP-PR") && oldTypes.contains("HW") && !oldTypes.contains("OP-PR")){
                newTypes.add("OP-PR");
            }
            //if H/W and OP and WI are selected and user unselects Operations/Process(Bop) then automatically Unselect WI
            if(newTypes.contains("HW") && newTypes.contains("WI") && !newTypes.contains("OP-PR") && oldTypes.contains("HW") && oldTypes.contains("WI") && oldTypes.contains("OP-PR")){
                newTypes.remove("WI");
            }
        }
    }

    //added for contexts synchronization
    public ReleasePackage updateContexts(BaseEntityInterface newInst, Map<String, Object> changedAttributeNames) {
        return (ReleasePackage) self.update(newInst, changedAttributeNames);
    }

    public void updateReleasePackageInTeamcenter(ReleasePackage releasePackage, List<String> changedProperties) {
        Ecn ecn = new Ecn();
        String teamcenterId = getTeamcenterId(releasePackage.getId());
        ecn.setTeamcenterId(teamcenterId);

        if (changedProperties.contains("status")) {
            ecn.setReleasePackageStatus(ReleasePackageStatus.getStatusNameByCode(releasePackage.getStatus()));
        }
        if (changedProperties.contains("title")) {
            ecn.setTitle(releasePackage.getTitle());
        }
        if (changedProperties.contains("sapChangeControl")) {
            ecn.setSapChangeControl(releasePackage.getSapChangeControl());
        }
        if (changedProperties.contains("plannedEffectiveDate")) {
            ecn.setValidFrom(releasePackage.getPlannedEffectiveDate());
        }
        ecn.setReleasePackageNumber(releasePackage.getReleasePackageNumber());

        Result result;
        log.info("updating ecn status in teamcenter status:: " + ecn.getReleasePackageStatus() + " Rp number ::" + releasePackage.getReleasePackageNumber());
        if(Objects.nonNull(AutomaticClosureHolder.isAutomaticClosure().get()) && AutomaticClosureHolder.isAutomaticClosure().get().equals(true)) {
            log.info("auto closure -Before updating ecn status in teamcenter status:: " + ecn.getReleasePackageStatus() + " Rp number ::" + releasePackage.getReleasePackageNumber() );
            result = ecnService.automaticallyCloseEngineeringChangeNotice(ecn);
            if (Objects.nonNull(result))
                log.info("auto closure -After updating ecn status in teamcenter status:: " + ecn.getReleasePackageStatus() + " Rp number ::" + releasePackage.getReleasePackageNumber() + " Result:: " + result.getStatus() + " TC Id:: " + result.getTeamcenterId() + " Details:: " + result.getDetails());
            else
                log.info("auto closure -After updating ecn status in teamcenter status:: " + ecn.getReleasePackageStatus() + " Rp number ::" + releasePackage.getReleasePackageNumber() + " result is null");
        } else {
            result = ecnService.updateEngineeringChangeNotice(ecn);
            log.info("After updating ecn status in teamcenter status:: " + ecn.getReleasePackageStatus() + " Rp number ::" + releasePackage.getReleasePackageNumber() + " Result:: " + result.getStatus() + " TC Id:: " + result.getTeamcenterId() + " Details:: " + result.getDetails());
        }

        if (Objects.isNull(result) || result.getStatus().equalsIgnoreCase("ERROR")) {
            throw new TeamcenterUpdateFailedException();
        }
    }

    private void updateReleasePackageStatusInTeamcenter(BaseEntityInterface releasePackage) {
        List<String> changedProperties = new ArrayList<>();
        changedProperties.add("status");
        this.updateReleasePackageInTeamcenter((ReleasePackage) releasePackage, changedProperties);
    }

    private ReleasePackage updateReleasePackageAttributes(BaseEntityInterface newInst, BaseEntityInterface oldInst, List<String> oldInsChangedAttributeNames, List<String> newInsChangedAttributeNames, List<String> changedProperties) {
        ReleasePackage updatedReleasePackage = (ReleasePackage) self.merge(newInst, oldInst, oldInsChangedAttributeNames, newInsChangedAttributeNames);
        if (changedProperties.contains("changeSpecialist3")) {
            if(Objects.isNull(updatedReleasePackage.getChangeSpecialist3())){
                releasePackageMyTeamService.deleteChangeSpecialist3FromMyTeam(updatedReleasePackage.getId());
            } else {
                releasePackageMyTeamService.addChangeSpecialist3ToMyTeam(updatedReleasePackage.getChangeSpecialist3(), updatedReleasePackage.getId());
            }
        } else if (changedProperties.contains("executor")) {
            if(Objects.isNull(updatedReleasePackage.getExecutor())){
                releasePackageMyTeamService.deleteEcnExecutorFromMyTeam(updatedReleasePackage.getId());
            } else {
                releasePackageMyTeamService.addExecutorToMyTeam(updatedReleasePackage.getExecutor(), updatedReleasePackage.getId());
            }
        } else if (changedProperties.contains("plmCoordinator")) {
            if(Objects.isNull(updatedReleasePackage.getPlmCoordinator())){
                releasePackageMyTeamService.deleteRole(updatedReleasePackage.getPlmCoordinator(),
                        MyChangeRoles.coordinatorSCMPLM, updatedReleasePackage.getId());
            } else {
                releasePackageMyTeamService.addPlmCoordinatorToMyTeam(updatedReleasePackage.getPlmCoordinator(), updatedReleasePackage.getId());
            }
        } else if (changedProperties.contains("projectId") && Objects.nonNull(updatedReleasePackage.getProjectId())) {
            addProjectLeadToMyTeam(updatedReleasePackage);
        }
        return updatedReleasePackage;
    }

    private void addProjectLeadToMyTeam(ReleasePackage releasePackage) {
        String projectId = releasePackage.getProjectId();
        ProjectLeadDto projectLead = null;
        try {
            projectLead = this.projectLeadService.getProjectLeadByWbsId(projectId);
        } catch(HanaEntityNotFoundException e) {
            log.info("Project id not found in HANA");
            releasePackageMyTeamService.deleteProjectLeadFromMyTeam(releasePackage);
            if (releasePackage.getChangeOwnerType().equals(ChangeOwnerType.PROJECT.name())) {
                clearChangeOwnerInReleasePackage(releasePackage);
            }
        }
        if (Objects.nonNull(projectLead) && Objects.nonNull(projectLead.getUserId())) {
            User user = getUserByUserId(projectLead.getUserId());
            releasePackageMyTeamService.addProjectLeadToMyTeam(user, releasePackage.getId());
            if (releasePackage.getChangeOwnerType().equals(ChangeOwnerType.PROJECT.name())) {
                releasePackage.setChangeOwner(user);
                Map<String, Object> changedAttrs = new HashMap<>();
                changedAttrs.put("change_owner", user);
                self.update(releasePackage, changedAttrs);
            }
        } else {
            releasePackageMyTeamService.deleteProjectLeadFromMyTeam(releasePackage);
            if (releasePackage.getChangeOwnerType().equals(ChangeOwnerType.PROJECT.name())) {
                clearChangeOwnerInReleasePackage(releasePackage);
            }
        }
    }
    private void clearChangeOwnerInReleasePackage(ReleasePackage releasePackage) {
        releasePackage.setChangeOwner(null);
        Map<String, Object> changedAttrs = new HashMap<>();
        changedAttrs.put("change_owner", null);
        self.update(releasePackage, changedAttrs);
    }

    public User getUserByUserId(String userId) {
        try {
            return gdsUserService.getUserByUserId(userId);
        } catch (GdsException e) {
            log.info("user not found in GDS for " + userId);
        }
        User user = new User();
        user.setUserId(userId);
        return user;
    }

    public DeltaReport getDelta1Report(Long id) {
        String teamcenterId = getTeamcenterId(id);
        return ecnService.getDeltaReport(teamcenterId);
    }

    @Transactional
    public List<BaseEntityInterface> getUpdatedReleasePackagesInDuration(int days) {
        Long currentMilliseconds = System.currentTimeMillis();
        Timestamp startTimestamp = new Timestamp(currentMilliseconds - (days * 86400000));
        Timestamp endTimestamp = new Timestamp(currentMilliseconds);
        return AuditServiceDefaultInterface.super.getEntitiesUpdatedInDuration(startTimestamp, endTimestamp);
    }

    @SecureCaseAction("READ")
    public ProjectDto getProduct(Long id) {
        ReleasePackage releasePackage = (ReleasePackage) self.getEntityById(id);
        if (Objects.isNull(releasePackage.getProductId()))
            return null;
        return this.projectService.getProjectByProjectId(releasePackage.getProductId());

    }

    @SecureCaseAction("READ")
    public User getProjectLead(Long id) {
        ReleasePackage releasePackage = (ReleasePackage) self.getEntityById(id);
        if (Objects.isNull(releasePackage.getProjectId()))
            return null;

        ProjectLeadDto projectLeadDetails = this.projectLeadService.getProjectLeadByWbsId(releasePackage.getProjectId());
        if (Objects.isNull(projectLeadDetails) || Objects.isNull(projectLeadDetails.getUserId())) {
            throw new InternalAssertionException("Error while fetching Project lead details");
        }
        return getUserByUserId(projectLeadDetails.getUserId());

    }

    @SecureCaseAction("READ")
    public WbsDto getWorkBreakdownStructure(Long id) {
        ReleasePackage releasePackage = (ReleasePackage) self.getEntityById(id);
        if (Objects.isNull(releasePackage.getProjectId()))
            return null;
        return this.workBreakdownStructureService.getWbsByWbsId(releasePackage.getProjectId());
    }

    @Cacheable(key = "\"getReleasePackageIdByReleasePackageNumber-\" + #root.args[0]", condition = "@entityConfigSpringCacheConfiguration !=null &&  !(@entityConfigSpringCacheConfiguration.getType().equalsIgnoreCase(\"NONE\"))")
    public Long getReleasePackageIdByReleasePackageNumber(String releasePackageNumber) {
        return this.getReleasePackageIdByCriteria("releasePackageNumber:" + releasePackageNumber);
    }

    @Cacheable(key = "\"getReleasePackageNumberByReleasePackageId-\" + #root.args[0]", condition = "@entityConfigSpringCacheConfiguration !=null &&  !(@entityConfigSpringCacheConfiguration.getType().equalsIgnoreCase(\"NONE\"))")
    public String getReleasePackageNumberByReleasePackageId(Long releasePackageId) {
        ReleasePackage releasePackage = (ReleasePackage) self.getEntityById(releasePackageId);
        return releasePackage.getReleasePackageNumber();
    }


    @Cacheable(key = "\"getReleasePackageIdByContext-\" + #root.args[0] + #root.args[1]", condition = "@entityConfigSpringCacheConfiguration !=null &&  !(@entityConfigSpringCacheConfiguration.getType().equalsIgnoreCase(\"NONE\"))")
    public Long getReleasePackageIdByContext(String contextId, String contextType) {
        return this.getReleasePackageIdByCriteria("contexts.type:" + contextType + " and contexts.contextId:" + contextId);
    }

    private Long getReleasePackageIdByCriteria(String criteria) {
        Slice<Id> idSlice = this.filterIds(criteria, PageRequest.of(0, 1));
        return (Objects.nonNull(idSlice) && idSlice.getNumberOfElements() > 0) ? idSlice.getContent().get(0).getValue() : null;
    }

    public List<Long> getReleasePackageIdsByContext(String contextId, String contextType) {
        return this.getReleasePackageIdsByCriteria("contexts.type:" + contextType + " and contexts.contextId:" + contextId + " and status@1,2,3,4");
    }

    private List<Long> getReleasePackageIdsByCriteria(String criteria) {
        Slice<Id> idSlice = this.filterIds(criteria, PageRequest.of(0, Integer.MAX_VALUE -1));
        if  (Objects.nonNull(idSlice) && idSlice.getNumberOfElements() > 0) {
            return idSlice.getContent().stream().map(Id::getValue).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public CollaborationObjectCount getCollaborationObjectsCount(Long id) {
        ReleasePackage releasePackage = (ReleasePackage) self.getEntityById(id);
        List<ReleasePackageContext> releasePackageContexts = releasePackage.getContexts();
        CollaborationObjectCount collaborationObjectCount = new CollaborationObjectCount();
        Integer actionsCount = Math.toIntExact(releasePackageContexts.stream().filter(context -> context.getType().equals("ACTION")).count());
        Integer openActionsCount = Math.toIntExact(releasePackageContexts.stream().filter(context -> context.getType().equals("ACTION") && (context.getStatus().equals("OPEN") || context.getStatus().equals("ACCEPTED"))).count());
        Integer commentsCount = releasePackageCommentService.getCommentsCountByReleasePackageIdAndAuditor(id);
        Integer othersDocumentsCount = releasePackageDocumentService.getOtherDocumentsCountByReleasePackageId(id);
        Integer allDocumentsCount = 0;
        if (releasePackage.getChangeOwnerType().equals(ChangeOwnerType.PROJECT.name()))
            allDocumentsCount = releasePackageDocumentService.getDocumentsCountByReleasePackageId(releasePackage.getId());
        else
            allDocumentsCount = othersDocumentsCount;
        Slice<Id> idSlice = releasePackageCommentService.filterIds("releasePackage.id:" + id, PageRequest.of(0, Integer.MAX_VALUE - 1));
        List<Long> commentIds = idSlice.getContent().stream().map(content -> content.getValue()).collect(Collectors.toList());
        Integer commentDocumentsCount = releasePackageCommentDocumentService.getDocumentsCountByReleasePackageCommentIds(commentIds);
        collaborationObjectCount.setAllActionsCount(actionsCount);
        collaborationObjectCount.setOpenActionsCount(openActionsCount);
        collaborationObjectCount.setCommentsCount(commentsCount);
        collaborationObjectCount.setDocumentsCount(othersDocumentsCount);
        collaborationObjectCount.setAllAttachmentsCount(allDocumentsCount + commentDocumentsCount);

        return collaborationObjectCount;
    }

    private String getTeamcenterId(Long id) {
        ReleasePackage releasePackageByEntity = (ReleasePackage) self.getEntityById(id);
        List<ReleasePackageContext> releasePackageContextList = releasePackageByEntity.getContexts();
        if (Objects.isNull(releasePackageContextList))
            throw new InternalAssertionException("Teamcenter Id not found");
        Optional<ReleasePackageContext> releasePackageContext = releasePackageContextList.stream().filter(context -> context.getType().toUpperCase().equals("TEAMCENTER")).findFirst();
        if (releasePackageContext.isPresent()) {
            return releasePackageContext.get().getContextId();
        }
        throw new InternalAssertionException("Teamcenter Id not found");
    }


    public BaseEntityList<Overview> getPrerequisitesOverview(Long id) {
        BaseEntityList prerequisitesOverviewList = releasePackagePrerequisitesService.getOverview(id);
        return prerequisitesOverviewList;
    }

    //TODO: add securefetchcriteria and securefetch action
    public List<String> getPrerequisiteReleasePackageNumbers(Long id) {
        return releasePackagePrerequisitesService.getPrerequisiteReleasePackageNumbers(id);
    }

    @SecureCaseAction("UPDATE_PREREQUISITE")
    public ReleasePackageReorderPrerequisites reorderPrerequisites(Long id, PrerequisiteReleasePackage prerequisiteReleasePackage, Boolean isImpactCheckRequired) {
        return releasePackagePrerequisitesService.reorder(id, prerequisiteReleasePackage, isImpactCheckRequired);
    }

    @SecureCaseAction("REMOVE_PREREQUISITE")
    public ReleasePackageReorderPrerequisites deletePrerequisites(Long id, PrerequisiteReleasePackage prerequisiteReleasePackage, Boolean isImpactCheckRequired) {
        return releasePackagePrerequisitesService.delete(id, prerequisiteReleasePackage, isImpactCheckRequired);
    }

    @SecureCaseAction("ADD_PREREQUISITE")
    public BaseEntityList<Overview> addPrerequisites(Long id, List<PrerequisiteReleasePackage> perquisiteReleasePackages) {
        return releasePackagePrerequisitesService.add(id, perquisiteReleasePackages);
    }

    @SecureFetchAction
    public BaseEntityList<SearchSummary> getReleasePackageSearchSummary(@SecureFetchCriteria String criteria, String viewCriteria, String view, Pageable pageable, Optional<String> sliceSelect) {
        Slice<BaseView> releasePackageSearchSummaryList = EntityServiceDefaultInterface.super.getEntitiesFromView(criteria, viewCriteria, pageable, sliceSelect, SearchSummary.class);
        BaseEntityList<SearchSummary> baseEntityList = new BaseEntityList(releasePackageSearchSummaryList);
        if (view.length() == 0) {
            return baseEntityList;
        }
        List<String> releasePackageIds = new ArrayList<>();
        baseEntityList.getResults().stream().forEach(item -> {
            String prerequisitesReleasePackageId = item.getPrerequisiteReleasePackageids();
            if (Objects.nonNull(prerequisitesReleasePackageId))
                releasePackageIds.addAll(Arrays.asList(prerequisitesReleasePackageId.split("\\s*,\\s*")));
        });
        if (releasePackageIds.isEmpty()) {
            return baseEntityList;
        }
        criteria = "id@" + (releasePackageIds.stream().filter(item -> !item.isEmpty()).collect(Collectors.toList())).toString().replaceAll("\\s", "");

        pageable = PageRequest.of(0, Integer.MAX_VALUE - 1);
        Slice<BaseView> prerequisitesList = EntityServiceDefaultInterface.super.getEntitiesFromView(criteria, viewCriteria, pageable, sliceSelect, SearchSummary.class);
        releasePackageSearchSummaryList.getContent().stream().forEach(item -> {
            String prerequisitesReleasePackage = ((SearchSummary) item).getPrerequisiteReleasePackageids();
            final List<Long> prerequisiteIds;
            if (Objects.nonNull(prerequisitesReleasePackage)) {
                prerequisiteIds = Arrays.asList(prerequisitesReleasePackage.split("\\s*,\\s*")).stream().filter(id -> !id.isEmpty()).map(id -> Long.parseLong(id)).collect(Collectors.toList());
            } else {
                prerequisiteIds = new ArrayList<>();
            }
            if (!prerequisiteIds.isEmpty()) {
                List<BaseView> preRequisitesSearchSummaries = prerequisitesList.getContent().stream().filter(searchItem -> prerequisiteIds.contains(((SearchSummary) searchItem).getId())).collect(Collectors.toList());
                ((SearchSummary) item).setPrerequisiteReleasePackages(preRequisitesSearchSummaries);
            }
        });

        return new BaseEntityList(releasePackageSearchSummaryList);
    }

    public List<String> getParentReleasePackageIdsOfPrerequisite(final long id, final long parentId) {
        return releasePackageRepository.getParentReleasePackageIdsOfPrerequisite(id, parentId);
    }

    public ReleasePackageAggregate getAggregate(long id) {
        return (ReleasePackageAggregate) EntityServiceDefaultInterface.super.getAggregate(id, (Class) ReleasePackageAggregate.class);
    }

    @Override
    public ChangeLog getChangeLog(Long id) {
        ChangeLog releasePackageChangeLog = AuditServiceDefaultInterface.super.getChangeLog(id);
        return AuditHelper.handleAuditEntriesForContexts(releasePackageChangeLog);
    }

    @Override
    public LinkedItems getLinkedItems(Long id) {
        List<LinkedItems.LinkCategory> linkCategories = new ArrayList<>(Arrays.asList(new LinkedItems.LinkCategory("ChangeRequest", "CR"),
                new LinkedItems.LinkCategory("ChangeNotice", "CN"),
                new LinkedItems.LinkCategory("ReleasePackage", "RP"),
                new LinkedItems.LinkCategory("ECN", "ECN")));
        LinkedItems linkedItems = EntityServiceDefaultInterface.super.getLinkedItems(id, linkCategories);
        Optional<LinkedItems.LinkCategory> releasePackageCategory = linkedItems.getCategories().stream().filter(linkCategory -> linkCategory.getName().equals("RELEASEPACKAGE")).findFirst();
        //add sibling RPs as linked items
        if (releasePackageCategory.isPresent()) {
            List<ReleasePackage> siblingReleasePackages = getSiblingReleasePackages(id, Optional.empty());
            List<LinkedItems.LinkSubCategory> linkSubCategories = new ArrayList<>();
            List<LinkedItems.LinkItem> items = new ArrayList();
            siblingReleasePackages.forEach(item -> {
                LinkedItems.LinkItem linkItem = new LinkedItems.LinkItem(item.getReleasePackageNumber(), "RELEASEPACKAGE", item.getTitle());
                items.add(linkItem);
            });
            linkSubCategories.add(new LinkedItems.LinkSubCategory(items));
            releasePackageCategory.get().setSubCategories(linkSubCategories);
            releasePackageCategory.get().setTotalItems(items.size());
        }
        return linkedItems;
    }

    private List<ReleasePackage> getSiblingReleasePackages(Long releasePackageId, Optional<Sort> sort) {
        ReleasePackage releasePackage = (ReleasePackage) self.getEntityById(releasePackageId);
        Optional<ReleasePackageContext> releasePackageContext = releasePackage.getContexts().stream().filter(context -> context.getType().equals("CHANGENOTICE")).findFirst();
        if (releasePackageContext.isPresent()) {
            Pageable pageable = sort.isEmpty() ? PageRequest.of(0, Integer.MAX_VALUE -1) :  PageRequest.of(0, Integer.MAX_VALUE -1, sort.get());
            BaseEntityList<ReleasePackage> relatedReleasePackages = self.filter("contexts.contextId: "+ releasePackageContext.get().getContextId() + " and contexts.type:" + releasePackageContext.get().getType(), pageable);
            return relatedReleasePackages.getResults();
        }
        return new ArrayList<>();
    }

    @Override
    public List<StatusCountOverview> getStatusCountOverview(String criteria, String viewCriteria, StatusInterface[] statuses, Optional<Class> viewClass) {
        List<StatusCountOverview> statusCountOverview = EntityServiceDefaultInterface.super.getStatusCountOverview(criteria, viewCriteria, statuses, Optional.of(Overview.class));
        return statusCountOverview.stream().filter(overviewItem -> !(overviewItem.getName().equals(ReleasePackageStatus.OBSOLETED.getStatusCode())
                || overviewItem.getName().equals(ReleasePackageStatus.CLOSED.getStatusCode()))).collect(Collectors.toList());
    }

    public List<SdlMonitor> performCaseActionAndGetSdlMonitor(Long id, String workInstructionIds, String caseAction){
        ReleasePackage releasePackage = (ReleasePackage) self.getEntityById(id);
        return impactedItemService.performCaseActionAndGetSdlMonitor(releasePackage.getReleasePackageNumber(),workInstructionIds,caseAction);
        //TODO  SDL possible
    }

    public List<LinkedItems.LinkItem> getLinkedReleasePackages(String id, String type, String sortData) {
        List<ReleasePackage> linkedReleasePackages = new ArrayList<>();
        if (Objects.nonNull(sortData) && sortData.length() > 0 && sortData.split(",").length != 2) {
            throw new InternalAssertionException("Invalid Sort configuration.");
        }
        Direction direction = sortData.split(",")[1].equals("asc") ? Direction.ASC : Direction.DESC;
        Sort sort = Sort.by(direction, sortData.split(",")[0]);
        if (type.toUpperCase().equals("RELEASEPACKAGE")) {
            Long releasePackageId = getReleasePackageIdByReleasePackageNumber(id);
            linkedReleasePackages = getSiblingReleasePackages(releasePackageId, Optional.of(sort));
        } else if (type.toUpperCase().equals("CHANGEREQUEST") || type.toUpperCase().equals("CHANGENOTICE")) {
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE-1, sort);
            BaseEntityList<ReleasePackage> baseEntityList = filter("contexts.contextId:" + id + " and contexts.type:"+ type, pageable);
            linkedReleasePackages = baseEntityList.getResults();
        }
        List<LinkedItems.LinkItem> items = new ArrayList<>();
        linkedReleasePackages.forEach(item -> {
            LinkedItems.LinkItem linkItem = new LinkedItems.LinkItem(item.getReleasePackageNumber(), "RELEASEPACKAGE", item.getTitle());
            items.add(linkItem);
        });
        return items;
    }

    public void updateChangeOwner(Long releasePackageId, User changeOwner) {
        ReleasePackage releasePackage = (ReleasePackage) self.getEntityById(releasePackageId);
        if (!Objects.equals(releasePackage.getChangeOwner(), changeOwner)) {
            releasePackage.setChangeOwner(changeOwner);
            Map<String, Object> changedAttrs = new HashMap<>();
            changedAttrs.put("change_owner", changeOwner);
            self.update(releasePackage, changedAttrs);
        }
    }

    public SapErDetails getSapErDetails(Long id) {
        String ecnId = ReleasePackageUtil.getReleasePackageContextId((ReleasePackage) self.getEntityById(id), "ECN");
        String[] ecn = ecnId.split("-");
        ErDto erDto = erService.getErByErId(ecn[1]);
        if(Objects.nonNull(erDto)){
            return new SapErDetails(erDto.getErId(),Integer.parseInt(erDto.getStatus()));
        }
        return null;
    }

    public List<ReleasePackageMandatoryParameters> getMandatoryParametersByContextId(String contextId, String contextType){
        List<ReleasePackageMandatoryParameters> mandatoryParametersList = new ArrayList<>();
        List<Long> releasePackageIds=getReleasePackageIdsByContext(contextId,contextType);
        for(Long id:releasePackageIds) {
            ReleasePackage releasePackage = (ReleasePackage) self.getEntityById(id);
            List<String> mandatoryFields = new ArrayList<>();
            ReleasePackageMandatoryParameters releasePackageMissingDetails = new ReleasePackageMandatoryParameters();
            if(!Objects.isNull(releasePackage) && !Objects.equals(releasePackage.getChangeOwnerType(), "") && Objects.equals(releasePackage.getChangeOwnerType(), ChangeOwnerType.CREATOR.name())){
                if(Objects.isNull(releasePackage.getPlannedEffectiveDate()))
                    mandatoryFields.add("plannedEffectiveDate");
                if(Objects.isNull(releasePackage.getPlannedReleaseDate()) )
                    mandatoryFields.add("plannedReleaseDate");
                if(Objects.isNull(releasePackage.getTypes()))
                    mandatoryFields.add("types");
            }
           else if(!Objects.isNull(releasePackage) && !Objects.equals(releasePackage.getChangeOwnerType(), "") && releasePackage.getChangeOwnerType().equalsIgnoreCase(ChangeOwnerType.PROJECT.name())){
                if(Objects.isNull(releasePackage.getPlannedEffectiveDate()))
                    mandatoryFields.add("plannedEffectiveDate");
                if(Objects.isNull(releasePackage.getPlannedReleaseDate()))
                    mandatoryFields.add("plannedReleaseDate");
                if(Objects.isNull(releasePackage.getTypes()) )
                    mandatoryFields.add("types");
                if(Objects.isNull(releasePackage.getSapChangeControl()))
                    mandatoryFields.add("sapChangeControl");
            }
            releasePackageMissingDetails.setId(id);
            releasePackageMissingDetails.setReleasePackageNumber(releasePackage.getReleasePackageNumber());
            releasePackageMissingDetails.setMissingDetails(mandatoryFields);
            mandatoryParametersList.add(releasePackageMissingDetails);
}

        return mandatoryParametersList;
    }


    @Transactional
    public ReleasePackage mergeReleasePackageTypesBySystemUser(ReleasePackage newIns, ReleasePackage oldIns, List<String> oldInsChangedAttributeNames, List<String> newInsChangedAttributeNames) throws IllegalAccessException {
        List<String> changedProperties = ObjectMapperUtil.getChangedProperties(oldIns, newIns);
        if (!changedProperties.isEmpty() && changedProperties.size() == 1 && changedProperties.contains("types")) {
            if (newIns.getTypes().isEmpty()) {
                throw new InternalAssertionException("Not allowed to clear RP types");
            }
            ReleasePackage releasePackage = (ReleasePackage) self.getEntityById(newIns.getId());
            if (releasePackage.getChangeOwnerType().equals(ChangeOwnerType.CREATOR.name()) && newIns.getTypes().contains(ReleasePackageTypes.HW.getType())) {
                throw new NotAllowedToAddHwTypeForCreatorRP();
            }
            Optional<ReleasePackageContext> teamcenterContext = releasePackage.getContexts().stream().filter(context -> Objects.equals(context.getType(), "TEAMCENTER")).findFirst();
            if (teamcenterContext.isPresent() && (releasePackage.getTypes().contains(ReleasePackageTypes.PR.getType()) || releasePackage.getTypes().contains(ReleasePackageTypes.HW.getType()))
                        && newIns.getTypes().size() == 1 && newIns.getTypes().contains(ReleasePackageTypes.WI.getType())) {
                    throw new NotAllowedToRemoveHwOpTypeFromTeamcenterEcn();
            }
            updateReleasePackageTypes(newIns, oldIns, changedProperties);
            return (ReleasePackage) self.merge(newIns, oldIns, newInsChangedAttributeNames, oldInsChangedAttributeNames);
        } else {
            throw new InternalAssertionException("Not allowed to update fields other than types with system account");
        }
    }

    public void updateMyTeamWithChangeObjectMyTeam(List<MyTeamMember> changeObjectMyTeamMembers, String releasePackageNumber) {
        if (Objects.nonNull(releasePackageNumber)) {
            Long releasePackageId = self.getReleasePackageIdByReleasePackageNumber(releasePackageNumber);
            releasePackageMyTeamService.syncMyTeamForCreatorsAndUsers(changeObjectMyTeamMembers, releasePackageId);
        }
    }

    @SecureFetchAction
    public BaseEntityList<LinkedObject> getReleasePackageAsLinkedObject(@SecureFetchViewCriteria  String viewCriteria, Pageable pageable, Optional<String> sliceSelect) {
        Slice<BaseView> linkedObjectList = EntityServiceDefaultInterface.super.getEntitiesFromView(viewCriteria, pageable, sliceSelect, LinkedObject.class);
        return new BaseEntityList(linkedObjectList);
    }

    public static class AutomaticClosureHolder {
        private static final ThreadLocal<Boolean> isAutomaticClosure = new ThreadLocal();

        private AutomaticClosureHolder() {
        }

        public static ThreadLocal<Boolean> isAutomaticClosure() {
            return isAutomaticClosure;
        }
    }


    @SecureFetchAction
    public BaseEntityList<Overview> getReleasePackagesForGlobalSearch(@SecureFetchViewCriteria String viewCriteria, Pageable pageable, Optional<String> sliceSelect) {
        Slice<BaseView> releasePackageOverviewList = EntityServiceDefaultInterface.super.getEntitiesFromView(viewCriteria, pageable, sliceSelect, GlobalSearch.class);
        return new BaseEntityList(releasePackageOverviewList);
    }

    @SecureCaseAction("READ")
    public AggregateInterface getReleasePackageAggregate(Long id) {
        return EntityServiceDefaultInterface.super.getAggregate(id, (Class) ReleasePackageAggregate.class);
    }

    @SneakyThrows
    @Transactional
    public ReleasePackage mergeEntityBySystemUser(BaseEntityInterface newInst, BaseEntityInterface oldInst, List<String> oldInsChangedAttributeNames, List<String> newInsChangedAttributeNames) {
        log.info("newInst " + newInsChangedAttributeNames + " oldInst " + oldInsChangedAttributeNames);
        return (ReleasePackage) self.merge(newInst, oldInst, oldInsChangedAttributeNames, newInsChangedAttributeNames);
    }

    public void updateMyTeamMemberFields(User user, Long linkedEntityId, String role) {
        ReleasePackage releasePackage = (ReleasePackage) self.getEntityById(linkedEntityId);
        Map<String, Object> changedAttrs = new HashMap<>();
        if (Objects.equals(role, MyChangeRoles.changeSpecialist3.getRole())) {
            releasePackage.setChangeSpecialist3(user);
            changedAttrs.put("change_specialist3", user);
        } else if (Objects.equals(role, MyChangeRoles.ecnExecutor.getRole())) {
            releasePackage.setExecutor(user);
            changedAttrs.put("executor", user);
        } else if (Objects.equals(role, MyChangeRoles.changeOwner.getRole()) && Objects.nonNull(user)) {
            releasePackage.setChangeOwner(user);
            changedAttrs.put("change_owner", user);
        } else {
            return;
        }
        self.update(releasePackage, changedAttrs);
    }
}
