package com.example.mirai.projectname.changerequestservice.changerequest.service;

import com.example.mirai.libraries.air.problem.model.Problem;
import com.example.mirai.libraries.air.problem.service.ProblemService;
import com.example.mirai.libraries.audit.service.AuditServiceDefaultInterface;
import com.example.mirai.libraries.cerberus.diabom.model.DiaBom;
import com.example.mirai.libraries.cerberus.diabom.service.DiaBomService;
import com.example.mirai.libraries.cerberus.functionalcluster.model.FunctionalCluster;
import com.example.mirai.libraries.cerberus.functionalcluster.service.FunctionalClusterService;
import com.example.mirai.libraries.cerberus.productbrakedownstructure.model.ProductBreakdownStructure;
import com.example.mirai.libraries.cerberus.productbrakedownstructure.service.ProductBreakdownStructureService;
import com.example.mirai.libraries.cerberus.shared.exception.CerberusException;
import com.example.mirai.libraries.core.annotation.*;
import com.example.mirai.libraries.core.exception.CaseActionNotFoundException;
import com.example.mirai.libraries.core.exception.EntityIdNotFoundException;
import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.core.model.*;
import com.example.mirai.libraries.entity.model.Id;
import com.example.mirai.libraries.entity.model.LinkedItems;
import com.example.mirai.libraries.entity.model.StatusCountOverview;
import com.example.mirai.libraries.entity.model.StatusOverview;
import com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.libraries.event.annotation.PublishResponse;
import com.example.mirai.libraries.gds.exception.GdsUserNotFoundException;
import com.example.mirai.libraries.gds.service.GdsUserService;
import com.example.mirai.libraries.hana.pmo.PmoService;
import com.example.mirai.libraries.hana.project.ProjectService;
import com.example.mirai.libraries.hana.projectlead.ProjectLeadService;
import com.example.mirai.libraries.hana.shared.exception.HanaEntityNotFoundException;
import com.example.mirai.libraries.hana.wbs.WbsService;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
/*import com.example.mirai.libraries.scm.scia.model.Scia;
import com.example.mirai.libraries.scm.scia.model.SciaContext;
import com.example.mirai.libraries.scm.scia.model.SciaSummary;
import com.example.mirai.libraries.scm.scia.service.SciaService;*/
import com.example.mirai.libraries.security.abac.AbacAwareInterface;
import com.example.mirai.libraries.security.abac.component.AbacProcessor;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.example.mirai.libraries.security.core.component.EntityACL;
import com.example.mirai.libraries.security.core.component.PropertyACL;
import com.example.mirai.libraries.security.core.service.SecurityServiceDefaultInterface;
import com.example.mirai.libraries.security.rbac.RbacAwareInterface;
import com.example.mirai.libraries.security.rbac.component.RbacProcessor;
import com.example.mirai.projectname.changerequestservice.changerequest.helper.AirPbsImportHelper;
import com.example.mirai.projectname.changerequestservice.changerequest.helper.AuditHelper;
import com.example.mirai.projectname.changerequestservice.changerequest.model.*;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestAggregate;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestChangeLogAggregate;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestDetail;
import com.example.mirai.projectname.changerequestservice.changerequest.model.dto.*;
import com.example.mirai.projectname.changerequestservice.changerequest.repository.ChangeRequestRepository;
import com.example.mirai.projectname.changerequestservice.comment.service.ChangeRequestCommentService;
import com.example.mirai.projectname.changerequestservice.customerimpact.service.CustomerImpactService;
import com.example.mirai.projectname.changerequestservice.document.service.ChangeRequestDocumentService;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.ImpactAnalysis;
import com.example.mirai.projectname.changerequestservice.impactanalysis.model.dto.CustomerImpactDetail;
import com.example.mirai.projectname.changerequestservice.impactanalysis.service.ImpactAnalysisService;
import com.example.mirai.projectname.changerequestservice.myteam.service.ChangeRequestMyTeamService;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import com.example.mirai.projectname.changerequestservice.scope.model.dto.ScopeFieldVisibilityFactor;
import com.example.mirai.projectname.changerequestservice.scope.service.ScopeService;
import com.example.mirai.projectname.changerequestservice.shared.exception.NotAllowedToAddSelfAsDependentException;
import com.example.mirai.projectname.changerequestservice.shared.exception.NotAllowedToUpdateChangeOwnerException;
import com.example.mirai.projectname.changerequestservice.shared.service.AggregateEventBuilder;
import com.example.mirai.projectname.changerequestservice.shared.util.Constants;
import com.example.mirai.projectname.changerequestservice.shared.util.Defaults;
import com.example.mirai.projectname.changerequestservice.shared.util.IssueTypes;
import com.example.mirai.projectname.libraries.impacteditem.impacteditem.service.ImpactedItemService;
import com.example.mirai.projectname.libraries.model.MyChangeRoles;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pmo.PmoDto;
import project.ProjectDto;
import projectlead.ProjectLeadDto;
import wbs.WbsDto;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeOwnerType.CREATOR;
import static com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeOwnerType.PROJECT;

@Service
@EntityClass(ChangeRequest.class)
@Slf4j
public class ChangeRequestService implements EntityServiceDefaultInterface, SecurityServiceDefaultInterface, AuditServiceDefaultInterface {
    @Resource
    public ChangeRequestService self;
    private final ChangeRequestStateMachine stateMachine;
    private final AbacProcessor abacProcessor;
    private final RbacProcessor rbacProcessor;
    private final EntityACL acl;
    private final PropertyACL pacl;
    private final CaseActionList caseActionList;
    private final ChangeRequestRepository changeRequestRepository;
    private final FunctionalClusterService functionalClusterService;
    private final ProductBreakdownStructureService productBreakdownStructureService;
    private final DiaBomService diaBomService;
    private final ProblemService problemService;
    private final ProjectService projectService;
    private final ProjectLeadService projectLeadService;
    private final GdsUserService gdsUserService;
    private final WbsService workBreakdownStructureService;
    private final PmoService pmoService;
    @Value("${skip-diabom-creation-on-submit:false}")
    public boolean skipDiabomCreationOnSubmit;
    @Value("${skip-scia-obsolescence-on-obsolete:true}")
    private boolean skipSciObsolescenceOnObsolete;
    @Autowired
    private ChangeRequestCommentService changeRequestCommentService;
    private final ChangeRequestInitializer changeRequestInitializer;
    private final ImpactedItemService impactedItemService;
    @Autowired
    public ChangeRequestDocumentService changeRequestDocumentService;
    @Autowired
    public ImpactAnalysisService impactAnalysisService;
    @Autowired
    public ChangeRequestMyTeamService changeRequestMyTeamService;
    @Autowired
    public CustomerImpactService customerImpactService;
    @Autowired
    public ScopeService scopeService;
    @Autowired
    public AirPbsImportHelper airPbsImportHelper;
    /*private final SciaService sciaService;*/

    public ChangeRequestService(ChangeRequestRepository changeRequestRepository, ChangeRequestStateMachine stateMachine, AbacProcessor abacProcessor, RbacProcessor rbacProcessor,
                                EntityACL acl, PropertyACL pacl, CaseActionList caseActionList, ProblemService problemService, DiaBomService diaBomService,
                                ProjectService projectService, PmoService pmoService,
                                WbsService workBreakdownStructureService, ProjectLeadService projectLeadService,
                                ProductBreakdownStructureService productBreakdownStructureService, FunctionalClusterService functionalClusterService,
                                GdsUserService gdsUserService, ChangeRequestInitializer changeRequestInitializer, ImpactedItemService impactedItemService /*, SciaService sciaService*/) {
        this.stateMachine = stateMachine;
        this.abacProcessor = abacProcessor;
        this.rbacProcessor = rbacProcessor;
        this.acl = acl;
        this.pacl = pacl;
        this.caseActionList = caseActionList;
        this.problemService = problemService;
        this.diaBomService = diaBomService;
        this.changeRequestRepository = changeRequestRepository;
        this.productBreakdownStructureService = productBreakdownStructureService;
        this.functionalClusterService = functionalClusterService;
        this.projectService = projectService;
        this.pmoService = pmoService;
        this.workBreakdownStructureService = workBreakdownStructureService;
        this.projectLeadService = projectLeadService;
        this.gdsUserService = gdsUserService;
        this.changeRequestInitializer = changeRequestInitializer;
        this.impactedItemService = impactedItemService;
        /*this.sciaService = sciaService;*/
    }

    @Override
    public EntityACL getEntityACL() {
        return acl;
    }

    @Override
    public PropertyACL getPropertyACL() {
        return pacl;
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
    @SecureCaseAction("CREATE")
    @PublishResponse(eventType = "CREATE", destination = "com.example.mirai.projectname.changerequestservice.changerequest")
    @Transactional
    public ChangeRequest create(BaseEntityInterface entity) {
        stateMachine.checkForMandatoryFieldsAndSetStatusForCreate(entity);
        ChangeRequest changeRequest = (ChangeRequest) entity;
        if (Objects.isNull(changeRequest.getTitle())) {
            changeRequest.setTitle(Defaults.TITLE);
        }
        if (Objects.isNull(changeRequest.getIsSecure())) {
            changeRequest.setIsSecure(Defaults.SECURE);
        }
        return (ChangeRequest) EntityServiceDefaultInterface.super.create(changeRequest);
    }

    @SneakyThrows
    @SecureCaseAction("CREATE_AGGREGATE")
    @PublishResponse(eventType = "CREATE_AGGREGATE", destination = "com.example.mirai.projectname.changerequestservice.changerequest",
            eventEntity = "com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest")
    @Transactional
    public ChangeRequestAggregate createChangeRequestAggregate(ChangeRequestAggregate aggregate) {
        this.changeRequestInitializer.initiateLinkedEntities(aggregate);
        ChangeRequestAggregate changeRequestAggregate = (ChangeRequestAggregate) EntityServiceDefaultInterface.super.createRootAggregate(aggregate);
        changeRequestMyTeamService.addSubmitterRequesterToMyTeam(changeRequestAggregate);
        return changeRequestAggregate;
    }

    public ChangeRequestDetail getChangeRequestDetailFromAggregate(ChangeRequestAggregate changeRequestAggregate) {
        CustomerImpactDetail customerImpactDetail = customerImpactService.evaluateCustomerImpactDetail(changeRequestAggregate.getScope());
        return new ChangeRequestDetail(changeRequestAggregate, customerImpactDetail);
    }


    @Override
    @SecureCaseAction("READ")
    public BaseEntityInterface get(Long id) {
        return EntityServiceDefaultInterface.super.get(id);
    }

    @SneakyThrows
    @Override
    public CaseStatus performCaseActionAndGetCaseStatus(Long id, String action) {
        ChangeRequest updatedEntity = (ChangeRequest) self.performCaseAction(id, action);
        return self.getCaseStatus(updatedEntity);
    }

    @SneakyThrows
    public BaseEntityInterface performCaseAction(Long id, String action) {
        ChangeRequestCaseActions caseAction;
        BaseEntityInterface entity = self.getEntityById(id);
        try {
            caseAction = ChangeRequestCaseActions.valueOf(action.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CaseActionNotFoundException();
        }
        switch (caseAction) {
            case REDRAFT:
                return self.redraft(entity);
            case SUBMIT:
                return self.submit(entity);
            case DEFINE_SOLUTION:
                return self.defineSolution(entity);
            case ANALYZE_IMPACT:
                return self.analyzeImpact(entity);
            case APPROVE:
                return self.approve(entity);
            case CLOSE:
                return self.close(entity);
            case REJECT:
                return self.reject(entity);
            case OBSOLETE:
                return self.obsolete(entity);
            case RESUBMIT:
                return self.resubmit(entity);
            case REDEFINE_SOLUTION:
                return self.redefineSolution(entity);
            case REANALYZE_IMPACT:
                return self.reanalyzeImpact(entity);
            default:
                throw new CaseActionNotFoundException();
        }
    }

    @Override
    public AggregateInterface performCaseActionAndGetCaseStatusAggregate(Long aLong, String s, Class<AggregateInterface> aClass) {
        return null;
    }

    @SneakyThrows
    @SecureCaseAction("UPDATE")
    @SecurePropertyMerge
    @PublishResponse(eventType = "MERGE", eventBuilder = AggregateEventBuilder.class,
            responseClass = ChangeRequestAggregate.class, destination = "com.example.mirai.projectname.changerequestservice.changerequest")
    @Transactional
    public ChangeRequest mergeEntity(BaseEntityInterface newInst, BaseEntityInterface oldInst, List<String> oldInsChangedAttributeNames, List<String> newInsChangedAttributeNames) {
        List<String> changedProperties = ObjectMapperUtil.getChangedProperties(oldInst, newInst);
        //it is not allowed to manually update changeowner
        if (changedProperties.contains("changeOwner")) {
            throw new NotAllowedToUpdateChangeOwnerException();
        }
        /*if (changedProperties.contains("changeOwnerType")) {
            return self.mergeChangeOwnerType(newInst, oldInst, oldInsChangedAttributeNames, newInsChangedAttributeNames);
        }
        if (changedProperties.contains("issueTypes")) {
            checkForChangeOwnerTypeChange((ChangeRequest) newInst, (ChangeRequest) oldInst, changedProperties, oldInsChangedAttributeNames, newInsChangedAttributeNames);
        }*/
        if (changedProperties.contains("dependentChangeRequestIds")) {
            self.handleDependentChangeRequests((ChangeRequest) newInst, (ChangeRequest) oldInst);
        }
        log.info("newInst " + newInsChangedAttributeNames + " oldInst " + oldInsChangedAttributeNames);
        ChangeRequest updatedChangeRequest = (ChangeRequest) self.merge(newInst, oldInst, oldInsChangedAttributeNames, newInsChangedAttributeNames);
        //update my team
        //log.info("clearing change owner for impacted item");
        //clearChangeOwnerInImpactedItem((ChangeRequest) newInst, updatedChangeRequest);
        if (changedProperties.contains("issueTypes")) {
            customerImpactService.evaluateCustomerImpactDetail(updatedChangeRequest);
        }
        log.info("updateMyteam " + changedProperties.toArray() + " size " + changedProperties.size());
        updateMyTeamOnChangeRequestUpdate(changedProperties, updatedChangeRequest);
        return updatedChangeRequest;
    }

    private void clearChangeOwnerInImpactedItem(ChangeRequest newIns, ChangeRequest updatedChangeRequest) {
        //clear change owner when change owner type is updated
        if (Objects.nonNull(newIns.getChangeOwnerType()) && newIns.getChangeOwnerType().equals(PROJECT.name()) && updatedChangeRequest.getChangeOwnerType().equals(PROJECT.name())) {
            impactedItemService.clearChangeOwner(updatedChangeRequest.getId().toString(), "CHANGEREQUEST");
        }
    }

    private void updateMyTeamOnChangeRequestUpdate(List<String> changedProperties, ChangeRequest updatedChangeRequest) {
        if (changedProperties.contains("changeSpecialist1")) {
            changeRequestMyTeamService.addChangeSpecialist1ToMyTeam(updatedChangeRequest.getChangeSpecialist1(), updatedChangeRequest.getId());
        } else if (changedProperties.contains("changeSpecialist2")) {
            changeRequestMyTeamService.addChangeSpecialist2ToMyTeam(updatedChangeRequest.getChangeSpecialist2(), updatedChangeRequest.getId());
        } else if (changedProperties.contains("projectId")) {
            addProjectLeadToMyTeam(updatedChangeRequest);
        } /*else if (changedProperties.contains("changeOwner")) {
            changeRequestMyTeamService.addChangeOwnerToMyTeam(updatedChangeRequest.getChangeOwner(), updatedChangeRequest.getId());
        }*/
    }

    @SecureCaseAction("UPDATE_CHANGE_OWNER_TYPE")
    public ChangeRequest mergeChangeOwnerType(BaseEntityInterface newInst, BaseEntityInterface oldInst, List<String> oldInsChangedAttributeNames, List<String> newInsChangedAttributeNames) {
        ChangeRequest changeRequest = (ChangeRequest) self.getEntityById(newInst.getId());
        if (((ChangeRequest) newInst).getChangeOwnerType().equals(PROJECT.name())) {
            User projectLead = changeRequestMyTeamService.getProjectLead(changeRequest.getId());
            if (Objects.nonNull(projectLead)) {
                ((ChangeRequest) newInst).setChangeOwner(projectLead);
                ((ChangeRequest) oldInst).setChangeOwner(changeRequest.getChangeOwner());
                oldInsChangedAttributeNames.add("change_owner");
                newInsChangedAttributeNames.add("change_owner");
                changeRequestMyTeamService.addChangeOwnerToMyTeam(projectLead, changeRequest.getId());
            } else {
                changeRequestMyTeamService.deleteChangeOwnerFromMyTeam(changeRequest.getId());
            }
        } else if (((ChangeRequest) newInst).getChangeOwnerType().equals(CREATOR.name())) {
            ((ChangeRequest) newInst).setChangeOwner(null);
            ((ChangeRequest) oldInst).setChangeOwner(changeRequest.getChangeOwner());
            oldInsChangedAttributeNames.add("change_owner");
            newInsChangedAttributeNames.add("change_owner");
            changeRequestMyTeamService.deleteChangeOwnerFromMyTeam(changeRequest.getId());
        }
        ChangeRequest updatedChangeRequest = (ChangeRequest) self.merge(newInst, oldInst, oldInsChangedAttributeNames, newInsChangedAttributeNames);
        clearChangeOwnerInImpactedItem((ChangeRequest) newInst, updatedChangeRequest);
        return updatedChangeRequest;
    }

    private void checkForChangeOwnerTypeChange(ChangeRequest newInst, ChangeRequest oldInst, List<String> changedProperties, List<String> oldInsChangedAttributeNames, List<String> newInsChangedAttributeNames) {
        ChangeRequest changeRequest = (ChangeRequest) self.getEntityById(newInst.getId());
        if (changeRequest.getStatus().equals(ChangeRequestStatus.DRAFTED.getStatusCode())) {
            if (!getContextByType("CHANGENOTICE", changeRequest).isEmpty())
                return;
            if (newInst.getIssueTypes().size() == 1 && newInst.getIssueTypes().contains(IssueTypes.BOP) && changeRequest.getChangeOwnerType().equals(PROJECT.name())) {
                changedProperties.add("changeOwnerType");
                oldInsChangedAttributeNames.add("change_owner_type");
                newInsChangedAttributeNames.add("change_owner_type");
                newInst.setChangeOwnerType(CREATOR.name());
                oldInst.setChangeOwnerType(PROJECT.name());
                changedProperties.add("changeOwner");
                oldInsChangedAttributeNames.add("change_owner");
                newInsChangedAttributeNames.add("change_owner");
                newInst.setChangeOwner(null);
                oldInst.setChangeOwner(changeRequest.getChangeOwner());
            } else if (!(newInst.getIssueTypes().size() == 1 && newInst.getIssueTypes().contains(IssueTypes.BOP)) && changeRequest.getChangeOwnerType().equals(CREATOR.name())) {
                changedProperties.add("changeOwnerType");
                oldInsChangedAttributeNames.add("change_owner_type");
                newInsChangedAttributeNames.add("change_owner_type");
                newInst.setChangeOwnerType(PROJECT.name());
                oldInst.setChangeOwnerType(CREATOR.name());
                if (Objects.nonNull(changeRequest.getProjectId())) {
                    User projectLead = changeRequestMyTeamService.getProjectLead(changeRequest.getId());
                    if (!Objects.isNull(projectLead)) {
                        log.info("switched from creator to project projectLead" + projectLead.toString());
                        changedProperties.add("changeOwner");
                        oldInsChangedAttributeNames.add("change_owner");
                        newInsChangedAttributeNames.add("change_owner");
                        newInst.setChangeOwner(projectLead);
                        oldInst.setChangeOwner(changeRequest.getChangeOwner());
                        log.info("switched from creator to project change_owner" + newInst.getChangeOwner().toString());
                    }
                }
            }
        }
    }

    private List<ChangeRequestContext> getContextByType(String type, ChangeRequest changeRequest) {
        if (Objects.isNull(changeRequest.getContexts()))
            return new ArrayList<>();
        return changeRequest.getContexts().stream().filter(context -> context.getType().equals(type)).collect(Collectors.toList());
    }

    public void addProjectLeadToMyTeam(ChangeRequest changeRequest) {
        String projectId = changeRequest.getProjectId();
        Long changeRequestId = changeRequest.getId();
        log.info("addProjectLeadToMyTeam projectId " + projectId + "changeRequestId " + changeRequestId);
        ProjectLeadDto newProjectLead = null;
        if (Objects.nonNull(projectId)) {
            try {
                newProjectLead = this.projectLeadService.getProjectLeadByWbsId(projectId);
            } catch (HanaEntityNotFoundException e) {
                changeRequestMyTeamService.deleteProjectLeadFromMyTeam(changeRequest);
                if (changeRequest.getChangeOwnerType().equals(PROJECT.name()))
                    clearChangeOwnerInChangeRequest(changeRequest);
            }
        } else {
            changeRequestMyTeamService.deleteProjectLeadFromMyTeam(changeRequest);
            if (changeRequest.getChangeOwnerType().equals(PROJECT.name()))
                clearChangeOwnerInChangeRequest(changeRequest);
        }
        if (Objects.nonNull(newProjectLead) && Objects.nonNull(newProjectLead.getUserId())) {
            log.info("new project lead " + newProjectLead.toString());
            User user = getUserByUserId(newProjectLead.getUserId());
            if (Objects.nonNull(user)) {
                if (changeRequest.getChangeOwnerType().equals(PROJECT.name())) {
                    changeRequest.setChangeOwner(user);
                    Map<String, Object> changedAttrs = new HashMap<>();
                    changedAttrs.put("change_owner", user);
                    self.update(changeRequest, changedAttrs);
                }
                changeRequestMyTeamService.addProjectLeadToMyTeam(user, changeRequestId, changeRequest.getChangeOwnerType().equals(PROJECT.name()));
            }
        } else {
            changeRequestMyTeamService.deleteProjectLeadFromMyTeam(changeRequest);
            if (changeRequest.getChangeOwnerType().equals(PROJECT.name()))
                clearChangeOwnerInChangeRequest(changeRequest);
        }
    }

    private void clearChangeOwnerInChangeRequest(ChangeRequest changeRequest) {
        changeRequest.setChangeOwner(null);
        Map<String, Object> changedAttrs = new HashMap<>();
        changedAttrs.put("change_owner", null);
        self.update(changeRequest, changedAttrs);
    }

    @SecureCaseAction("LINK_CR")
    public void handleDependentChangeRequests(ChangeRequest newInst, ChangeRequest oldInst) {
        List<String> dependentCrOldValue = oldInst.getDependentChangeRequestIds();
        List<String> dependentCrNewValue = newInst.getDependentChangeRequestIds();
        if (Objects.isNull(dependentCrNewValue)) {
            dependentCrNewValue = new ArrayList<>();
        }
        if (dependentCrNewValue.contains(newInst.getId().toString()))
            throw new NotAllowedToAddSelfAsDependentException();
        Map<String, Object> changedAttrs = new HashMap<>();
        dependentCrNewValue.forEach(crId -> {
            ChangeRequest dependentChangeRequest = (ChangeRequest) self.getEntityById(Long.parseLong(crId));
            List<String> dependentValues = dependentChangeRequest.getDependentChangeRequestIds();
            if (!newInst.getId().toString().equals(crId) && (Objects.isNull(dependentValues) || !dependentValues.contains(newInst.getId().toString())) && !dependentChangeRequest.getStatus().equals(ChangeRequestStatus.OBSOLETED.getStatusCode())) {
                if (Objects.isNull(dependentValues))
                    dependentValues = new ArrayList<>();
                dependentValues.add(newInst.getId().toString());
                dependentChangeRequest.setDependentChangeRequestIds(dependentValues);
                changedAttrs.put("dependent_change_request_ids", dependentValues);
                self.update(dependentChangeRequest, changedAttrs);
            }
        });

        List<String> unlinkedCRs = new ArrayList<>();
        if (Objects.isNull(dependentCrOldValue)) {
            dependentCrOldValue = new ArrayList<>();
        }
        unlinkedCRs.addAll(dependentCrOldValue);
        unlinkedCRs.removeAll(dependentCrNewValue);
        unlinkedCRs.forEach(crId -> {
            ChangeRequest dependentChangeRequest = (ChangeRequest) self.getEntityById(Long.parseLong(crId));
            if (!newInst.getId().toString().equals(crId) && dependentChangeRequest.getDependentChangeRequestIds().contains(newInst.getId().toString())) {
                List<String> dependentValues = dependentChangeRequest.getDependentChangeRequestIds();
                dependentValues.remove(newInst.getId().toString());
                dependentChangeRequest.setDependentChangeRequestIds(dependentValues);
                changedAttrs.put("dependent_change_request_ids", dependentValues);
                self.update(dependentChangeRequest, changedAttrs);
            }
        });
    }

    @SecureCaseAction("SUBMIT")
    @PublishResponse(eventType = "SUBMIT", eventBuilder = AggregateEventBuilder.class,
            responseClass = ChangeRequestAggregate.class, destination = "com.example.mirai.projectname.changerequestservice.changerequest")
    @Transactional
    public ChangeRequest submit(BaseEntityInterface entity) {
        EntityUpdate entityUpdate = stateMachine.submit(entity);
        ChangeRequest changeRequest = (ChangeRequest) self.getEntityById(entity.getId());
        /*if (!skipDiabomCreationOnSubmit && (Objects.isNull(changeRequest.getChangeOwnerType()) || changeRequest.getChangeOwnerType().equals(PROJECT.name()))) {
            Optional<ChangeRequestContext> diaBom = this.getDiaBomContext(changeRequest);
            if (diaBom.isEmpty()) {
                try {
                    diaBomService.createDiaBom(entity.getId());
                    addDiaBomContext(changeRequest, entityUpdate);
                } catch (CerberusException e) {
                    throw e;
                }
            }
            if (changeRequest.getIssueTypes().contains(IssueTypes.BOP)) {
                log.info("deleting problem items");
                impactedItemService.deleteProblemItems(changeRequest.getId().toString(), "CHANGEREQUEST");
            } else {
                log.info("deleting problem items and scope items");
                impactedItemService.deleteProblemItemsAndScopeItems(changeRequest.getId().toString(), "CHANGEREQUEST");
            }
        } else if (changeRequest.getChangeOwnerType().equals(CREATOR.name())) {
            //copy problem items to solution items service call
            List<com.example.mirai.projectname.libraries.impacteditem.impacteditem.model.MyTeamMember> changeObjectMyTeamMembers = new ArrayList<>();
            try {
                changeObjectMyTeamMembers = impactedItemService.copyProblemItemsToScopeItems(String.valueOf(entity.getId()), "CHANGEREQUEST");
            } catch (ImpactedItemException e) {
                if (!e.getApplicationStatusCode().equals(Constants.CHANGE_OBJECT_NOT_EXIST_ERROR))
                    throw e;
            }
            updateMyTeamOnSubmit(changeObjectMyTeamMembers, changeRequest.getId());
        }*/
        Optional<ChangeRequestContext> diaBom = this.getDiaBomContext(changeRequest);
        if (diaBom.isEmpty()) {
            try {
                diaBomService.createDiaBom(entity.getId());
                addDiaBomContext(changeRequest, entityUpdate);
            } catch (CerberusException e) {
                throw e;
            }
        }
        return (ChangeRequest) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    private void updateMyTeamOnSubmit(List<com.example.mirai.projectname.libraries.impacteditem.impacteditem.model.MyTeamMember> changeObjectMyTeamMembers, Long changeRequestId) {
        List<MyTeamMember> myTeamMembers = changeObjectMyTeamMembers.stream().map(myTeamMember -> {
            MyTeamMember member = new MyTeamMember();
            User user = new User();
            user.setUserId(myTeamMember.getUser().getUserId());
            user.setFullName(myTeamMember.getUser().getFullName());
            user.setEmail(myTeamMember.getUser().getEmail());
            user.setAbbreviation(myTeamMember.getUser().getAbbreviation());
            user.setDepartmentName(myTeamMember.getUser().getDepartmentName());
            member.setUser(user);
            member.setRoles(myTeamMember.getRoles());
            log.info("user " + member.getUser().toString());
            return member;
        }).collect(Collectors.toList());
        changeRequestMyTeamService.syncMyTeamForCreatorsAndUsers(myTeamMembers, changeRequestId);
    }

    private Optional<ChangeRequestContext> getDiaBomContext(ChangeRequest changeRequest) {
        if (Objects.nonNull(changeRequest.getContexts())) {
            return changeRequest.getContexts().stream().filter(context -> context.getType().equals("DIA-BOM") && context.getStatus().equals("SUCCESS")).findFirst();
        }
        return null;
    }

    private void addDiaBomContext(ChangeRequest changeRequest, EntityUpdate entityUpdate) {
        ChangeRequestContext changeRequestContext = new ChangeRequestContext("DIA-BOM", "DIA-BOM-ID", null, "SUCCESS");
        List<ChangeRequestContext> changeRequestContexts = changeRequest.getContexts();
        changeRequestContexts.add(changeRequestContext);
        Map<String, Object> changedAttrs = entityUpdate.getChangedAttrs();
        changedAttrs.put("contexts", changeRequestContexts);
        ((ChangeRequest) entityUpdate.getEntity()).setContexts(changeRequestContexts);
    }

    @SecureCaseAction("DEFINE_SOLUTION")
    @PublishResponse(eventType = "DEFINE_SOLUTION", eventBuilder = AggregateEventBuilder.class,
            responseClass = ChangeRequestAggregate.class, destination = "com.example.mirai.projectname.changerequestservice.changerequest")
    @Transactional
    public ChangeRequest defineSolution(BaseEntityInterface entity) {

        EntityUpdate entityUpdate = stateMachine.defineSolution(entity);
        return (ChangeRequest) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("ANALYZE_IMPACT")
    @PublishResponse(eventType = "ANALYZE_IMPACT", eventBuilder = AggregateEventBuilder.class,
            responseClass = ChangeRequestAggregate.class, destination = "com.example.mirai.projectname.changerequestservice.changerequest")
    @Transactional
    public ChangeRequest analyzeImpact(BaseEntityInterface entity) {

        EntityUpdate entityUpdate = stateMachine.analyzeImpact(entity);
        return (ChangeRequest) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("APPROVE")
    @PublishResponse(eventType = "APPROVE", eventBuilder = AggregateEventBuilder.class,
            responseClass = ChangeRequestAggregate.class, destination = "com.example.mirai.projectname.changerequestservice.changerequest")
    @Transactional
    public ChangeRequest approve(BaseEntityInterface entity) {

        EntityUpdate entityUpdate = stateMachine.approve(entity);
        return (ChangeRequest) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("CLOSE")
    @PublishResponse(eventType = "CLOSE", eventBuilder = AggregateEventBuilder.class,
            responseClass = ChangeRequestAggregate.class, destination = "com.example.mirai.projectname.changerequestservice.changerequest")
    @Transactional
    public ChangeRequest close(BaseEntityInterface entity) {

        EntityUpdate entityUpdate = stateMachine.close(entity);
        return (ChangeRequest) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }


    @SecureCaseAction("REJECT")
    @PublishResponse(eventType = "REJECT", eventBuilder = AggregateEventBuilder.class,
            responseClass = ChangeRequestAggregate.class, destination = "com.example.mirai.projectname.changerequestservice.changerequest")
    @Transactional
    public ChangeRequest reject(BaseEntityInterface entity) {
        EntityUpdate entityUpdate = stateMachine.reject(entity);
        customerImpactService.resetCustomerCommunicationAndApproval(entity.getId());
        return (ChangeRequest) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("OBSOLETE")
    @PublishResponse(eventType = "OBSOLETE", eventBuilder = AggregateEventBuilder.class,
            responseClass = ChangeRequestAggregate.class, destination = "com.example.mirai.projectname.changerequestservice.changerequest")
    @Transactional
    public ChangeRequest obsolete(BaseEntityInterface entity) {
        /*if (!skipSciObsolescenceOnObsolete)
            sciaService.obsoleteSciasByChangeRequestId(entity.getId());*/
        EntityUpdate entityUpdate = stateMachine.obsolete(entity);
        return (ChangeRequest) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("REDRAFT")
    @PublishResponse(eventType = "REDRAFT", eventBuilder = AggregateEventBuilder.class,
            responseClass = ChangeRequestAggregate.class, destination = "com.example.mirai.projectname.changerequestservice.changerequest")
    @Transactional
    public ChangeRequest redraft(BaseEntityInterface entity) {
        EntityUpdate entityUpdate = stateMachine.redraft(entity);
        //clearIssueTypesAndScope(entityUpdate.getChangedAttrs(), entityUpdate.getEntity());
        return (ChangeRequest) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    private void clearIssueTypesAndScope(Map<String, Object> changedAttrs, BaseEntityInterface entity) {
        List<String> issueTypes = new ArrayList<>();
        changedAttrs.put("issue_types", issueTypes);
        ((ChangeRequest) entity).setIssueTypes(issueTypes);
        scopeService.resetScope(entity.getId());
    }

    @SecureCaseAction("RESUBMIT")
    @PublishResponse(eventType = "RESUBMIT", eventBuilder = AggregateEventBuilder.class,
            responseClass = ChangeRequestAggregate.class, destination = "com.example.mirai.projectname.changerequestservice.changerequest")
    @Transactional
    public ChangeRequest resubmit(BaseEntityInterface entity) {
        EntityUpdate entityUpdate = stateMachine.submit(entity);
        return (ChangeRequest) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("REDEFINE_SOLUTION")
    @PublishResponse(eventType = "REDEFINE_SOLUTION", eventBuilder = AggregateEventBuilder.class,
            responseClass = ChangeRequestAggregate.class, destination = "com.example.mirai.projectname.changerequestservice.changerequest")
    @Transactional
    public ChangeRequest redefineSolution(BaseEntityInterface entity) {

        EntityUpdate entityUpdate = stateMachine.defineSolution(entity);
        return (ChangeRequest) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureCaseAction("REANALYZE_IMPACT")
    @PublishResponse(eventType = "REANALYZE_IMPACT", eventBuilder = AggregateEventBuilder.class,
            responseClass = ChangeRequestAggregate.class, destination = "com.example.mirai.projectname.changerequestservice.changerequest")
    @Transactional
    public ChangeRequest reanalyzeImpact(BaseEntityInterface entity) {

        EntityUpdate entityUpdate = stateMachine.analyzeImpact(entity);
        return (ChangeRequest) self.update(entityUpdate.getEntity(), entityUpdate.getChangedAttrs());
    }

    @SecureFetchAction
    public Slice<Id> getIdsByCriteria(@SecureFetchCriteria String criteria, String viewCriteria, Pageable pageable, Optional<String> sliceSelect) {
        return self.filterIds(criteria, viewCriteria, Overview.class, pageable);
    }

    @SecureFetchAction
    public BaseEntityList<Overview> getChangeRequestsOverview(@SecureFetchCriteria String criteria, String viewCriteria, Pageable pageable, Optional<String> sliceSelect) {
        Slice<BaseView> changeRequestOverviewList = EntityServiceDefaultInterface.super.getEntitiesFromView(criteria, viewCriteria, pageable, sliceSelect, Overview.class);
        return new BaseEntityList(changeRequestOverviewList);
    }

    @SecureFetchAction
    public BaseEntityList<ChangeRequestList> getChangeRequestsList(@SecureFetchViewCriteria String viewCriteria, Pageable pageable, Optional<String> sliceSelect) {
        if (viewCriteria.length() == 0) {
            viewCriteria = "status!8";
        } else if (!viewCriteria.contains("status")) {
            viewCriteria += " and status!8";
        }
        Slice<BaseView> changeRequestList = EntityServiceDefaultInterface.super.getEntitiesFromView(viewCriteria, pageable, sliceSelect, ChangeRequestList.class);
        return new BaseEntityList(changeRequestList);
    }

    @SecureFetchAction
    public BaseEntityList<LinkedObject> getChangeRequestAsLinkedObject(@SecureFetchViewCriteria String viewCriteria, Pageable pageable, Optional<String> sliceSelect) {
        Slice<BaseView> linkedObjectList = EntityServiceDefaultInterface.super.getEntitiesFromView(viewCriteria, pageable, sliceSelect, LinkedObject.class);
        return new BaseEntityList(linkedObjectList);
    }

    @SecureFetchAction
    public BaseEntityList<Summary> getChangeRequestsSummary(@SecureFetchCriteria String criteria, String viewCriteria, Pageable pageable, Optional<String> sliceSelect) {
        Slice<BaseView> changeRequestSummaryList = EntityServiceDefaultInterface.super.getEntitiesFromView(criteria, viewCriteria, pageable, sliceSelect, Summary.class);
        return new BaseEntityList(changeRequestSummaryList);
    }

    public BaseEntityList<Summary> insecureFetchChangeRequestsSummary(String criteria, String viewCriteria, Pageable pageable, Optional<String> sliceSelect) {
        Slice<BaseView> changeRequestSummaryList = EntityServiceDefaultInterface.super.getEntitiesFromView(criteria, viewCriteria, pageable, sliceSelect, Summary.class);
        return new BaseEntityList(changeRequestSummaryList);
    }


    public ChangeRequestAggregate getAggregate(long id) {
        return (ChangeRequestAggregate) EntityServiceDefaultInterface.super.getAggregate(id, (Class) ChangeRequestAggregate.class);
    }

    @Override
    public ChangeRequestAggregate getAggregate(long id, Class<AggregateInterface> aggregateInterfaceClass) {
        return (ChangeRequestAggregate) EntityServiceDefaultInterface.super.getAggregate(id, (Class) ChangeRequestAggregate.class);
    }

    @SecureCaseAction("READ")
    public ChangeRequestAggregate getChangeRequestAggregate(Long id) {
        return (ChangeRequestAggregate) EntityServiceDefaultInterface.super.getAggregate(id, (Class) ChangeRequestAggregate.class);
    }

    public ChangeRequestDetail getChangeRequestDetail(long id) {
        ChangeRequestAggregate changeRequestAggregate = self.getAggregate(id);
        CustomerImpactDetail customerImpactDetail = customerImpactService.evaluateCustomerImpactDetail(changeRequestAggregate.getScope());
        return new ChangeRequestDetail(changeRequestAggregate, customerImpactDetail);
    }

    public ChangeRequestDetail getChangeRequestDetail(ChangeRequestAggregate changeRequestAggregate) {
        CustomerImpactDetail customerImpactDetail = customerImpactService.evaluateCustomerImpactDetail(changeRequestAggregate.getScope());
        Long customerImpactId = changeRequestAggregate.getImpactAnalysis().getDetails().getCustomerImpact().getId();
        changeRequestAggregate.getImpactAnalysis().getDetails().setCustomerImpact(customerImpactService.getCustomerImpactById(customerImpactId));
        return new ChangeRequestDetail(changeRequestAggregate, customerImpactDetail);
    }

    @Transactional
    @SecureCaseAction("UPDATE")
    @SecurePropertyMerge
    @PublishResponse(eventType = "MERGE", eventBuilder = AggregateEventBuilder.class,
            responseClass = ChangeRequestAggregate.class, destination = "com.example.mirai.projectname.changerequestservice.changerequest",
            eventEntity = "com.example.mirai.cm.changerequest.model.changerequest.ChangeRequest")
    public ChangeRequestAggregate updateChangeRequestAggregate(Long id, JsonNode jsonNode) throws JsonProcessingException {
        return (ChangeRequestAggregate) self.updateAggregate(id, jsonNode, ChangeRequestAggregate.class);
    }

    public void updateContexts(ChangeRequest newInst, Map<String, Object> changedAttributeNames) {
        self.update(newInst, changedAttributeNames);
    }

    @Transactional
    public List<BaseEntityInterface> getChangeRequestModifiedInLastDays(Integer modifiedInPastDays) {
        Long currentMilliseconds = System.currentTimeMillis();
        Timestamp startTimestamp = new Timestamp(currentMilliseconds - (modifiedInPastDays * 86400000));
        Timestamp endTimestamp = new Timestamp(currentMilliseconds);
        return AuditServiceDefaultInterface.super.getEntitiesUpdatedInDuration(startTimestamp, endTimestamp);
    }

    @SecureCaseAction("UNLINK_CR")
    @PublishResponse(eventType = "MERGE", eventBuilder = AggregateEventBuilder.class,
            responseClass = ChangeRequestAggregate.class, destination = "com.example.mirai.projectname.changerequestservice.changerequest")
    @Transactional
    public ChangeRequest unlinkChangeRequest(Long id, Id unlinkId) {
        ChangeRequest changeRequest = (ChangeRequest) self.getEntityById(id);
        List<String> dependentChangeRequestIds = changeRequest.getDependentChangeRequestIds();
        dependentChangeRequestIds.remove(unlinkId.getValue().toString());
        changeRequest.setDependentChangeRequestIds(dependentChangeRequestIds);
        Map<String, Object> changedAttrs = new HashMap<>();
        changedAttrs.put("dependent_change_request_ids", changeRequest.getDependentChangeRequestIds());
        self.update(changeRequest, changedAttrs);
        //also unlink from dependent CR
        ChangeRequest dependentChangeRequest = (ChangeRequest) self.getEntityById(unlinkId.getValue());
        List<String> dependentChangeRequestIdsOfDependent = dependentChangeRequest.getDependentChangeRequestIds();
        dependentChangeRequestIdsOfDependent.remove(id.toString());
        dependentChangeRequest.setDependentChangeRequestIds(dependentChangeRequestIdsOfDependent);
        changedAttrs.put("dependent_change_request_ids", dependentChangeRequest.getDependentChangeRequestIds());
        self.update(dependentChangeRequest, changedAttrs);
        return changeRequest;
    }

    @SecureCaseAction("READ")
    public List<ProductBreakdownStructure> getProductBreakdownStructureByChangeRequestId(Long changeRequestId) {
        List<ProductBreakdownStructure> pbsItems = new ArrayList<>();
        List<ChangeRequestContext> changeRequestContexts = ((ChangeRequest) self.getEntityById(changeRequestId)).getContexts();
        if (Objects.isNull(changeRequestContexts)) {
            return new ArrayList<>();
        }
        List<ChangeRequestContext> pbsContexts = changeRequestContexts.stream().filter(context -> context.getType().toUpperCase().equals("PBS")).collect(Collectors.toList());
        int itemCount = 0;
        while (itemCount < pbsContexts.size()) {
            pbsItems.add(productBreakdownStructureService.getProductBreakdownStructureById(pbsContexts.get(itemCount).getContextId()));
            itemCount++;
        }
        return pbsItems;
    }


    public List<ProductBreakdownStructure> findProductBreakdownStructuresByPartialId(String productBreakDownStructureId) {
        return productBreakdownStructureService.findProductBreakdownStructuresById(productBreakDownStructureId);
    }

    public List<Problem> findAirProblemsByPartialId(String id) {
        return this.problemService.findProblemsByPartialNumber(id);
    }

    @SecureCaseAction("READ")
    public List<Problem> getProblemsByChangeRequestId(Long changeRequestId) {
        List<Problem> problems = new ArrayList<>();
        List<ChangeRequestContext> changeRequestContexts = ((ChangeRequest) self.getEntityById(changeRequestId)).getContexts();
        if (Objects.isNull(changeRequestContexts)) {
            return problems;
        }
        List<ChangeRequestContext> airContexts = changeRequestContexts.stream().filter(context -> context.getType().toUpperCase().equals("AIR")).collect(Collectors.toList());
        if (!airContexts.isEmpty()) {
            problems = problemService.getProblemsByNumbers(airContexts.stream().map(context -> context.getContextId()).collect(Collectors.toList()));
        }
        return problems;
    }

    @SecureCaseAction("READ")
    public FunctionalCluster getFunctionalClusterDetails(Long changeRequestId) {
        ChangeRequest changeRequest = (ChangeRequest) self.getEntityById(changeRequestId);
        if (Objects.isNull(changeRequest.getFunctionalClusterId())) {
            return null;
        }
        return functionalClusterService.getFunctionalClusterById(changeRequest.getFunctionalClusterId());
    }

    public List<FunctionalCluster> findFunctionalClusterByPartialId(String searchId) {
        return functionalClusterService.findFunctionalClusterByPartialId(searchId);
    }

    public void createDiaBom(Long changeRequestId) throws InternalAssertionException {
        diaBomService.createDiaBom(changeRequestId);
    }

    public void createDiaBomForTemplate(Long changeRequestId, Long changeNoticeId) {
        diaBomService.createDiaBomForTemplate(changeRequestId, changeNoticeId);
    }

    public DiaBom getDiaBom(Long id) {
        return diaBomService.getDiaBomByChangeRequestId(id);
    }

    @SecureFetchAction
    public StatusOverview getStatusOverview(@SecureFetchCriteria String criteria, String viewCriteria, StatusInterface[] statuses, Optional<Class> viewClass) {
        return EntityServiceDefaultInterface.super.getStatusOverview(criteria, viewCriteria, statuses, java.util.Optional.of(Overview.class));
    }

    @SecureFetchAction
    public StateOverview getStateOverview(@SecureFetchViewCriteria String viewCriteria, StatusInterface[] statuses) {
        if (viewCriteria.length() == 0) {
            viewCriteria = "status!8";
        } else if (!viewCriteria.contains("status")) {
            viewCriteria += " and status!8";
        }
        StatusOverview statusOverview = EntityServiceDefaultInterface.super.getStatusOverview(viewCriteria, statuses, ChangeRequestList.class);
        return new StateOverview(statusOverview);
    }

    public CustomerImpactDetail getCiaDetail(Long id) {
        if (Objects.isNull(self.getEntityById(id))) {
            throw new EntityIdNotFoundException(getEntityClass().getSimpleName());
        }
        Scope scope = scopeService.getScopeByChangeRequestId(id);
        return customerImpactService.evaluateCustomerImpactDetail(scope);
    }

    @SecureCaseAction("UPDATE")
    @Transactional
    public List<ImportData.Response> linkAirItems(Long id, ImportData importData) {
        return airPbsImportHelper.linkAirItems(id, importData);
    }

    @SecureCaseAction("UPDATE")
    @Transactional
    public List<ImportData.Response> linkPbsItems(Long id, ImportData importData) {
        return airPbsImportHelper.linkPbsItems(id, importData);
    }

    @Transactional
    @SecureCaseAction("UPDATE")
    public ChangeRequest unlinkAir(Long id, ImportData.Source changeRequestContext) {
        ChangeRequest changeRequest = (ChangeRequest) self.getEntityById(id);
        List<String> airIds = changeRequest.getContexts().stream().map(context -> context.getContextId()).collect(Collectors.toList());
        airIds.remove(changeRequestContext.getId());
        problemService.updateProblem(changeRequest.getId(), changeRequest.getStatus().toString(), changeRequest.getTitle(), airIds);
        Optional<ChangeRequestContext> context = changeRequest.getContexts().stream().filter(
                item -> item.getType().equals(changeRequestContext.getType()) && item.getContextId().equals(changeRequestContext.getId())).findFirst();
        if (context.isPresent()) {
            List<ChangeRequestContext> changeRequestContexts = changeRequest.getContexts();
            changeRequestContexts.remove(context.get());
            changeRequest.setContexts(changeRequestContexts);
            Map<String, Object> changedAttrs = new HashMap<>();
            changedAttrs.put("contexts", changeRequest.getContexts());
            self.update(changeRequest, changedAttrs);
        }
        return changeRequest;
    }

    @Transactional
    @SecureCaseAction("UPDATE")
    public ChangeRequest unlinkPbs(Long id, ImportData.Source source) {
        productBreakdownStructureService.unlinkCRFromProductBreakdownStructure(source.getId(), String.valueOf(id));
        ChangeRequest changeRequest = (ChangeRequest) self.getEntityById(id);
        Optional<ChangeRequestContext> context = changeRequest.getContexts().stream().filter(
                item -> item.getType().equals(source.getType()) && item.getContextId().equals(source.getId())).findFirst();
        if (context.isPresent()) {
            List<ChangeRequestContext> changeRequestContexts = changeRequest.getContexts();
            changeRequestContexts.remove(context.get());
            changeRequest.setContexts(changeRequestContexts);
            Map<String, Object> changedAttrs = new HashMap<>();
            changedAttrs.put("contexts", changeRequest.getContexts());
            self.update(changeRequest, changedAttrs);
        }
        return changeRequest;
    }

    @Override
    public LinkedItems getLinkedItems(Long id) {
        List<LinkedItems.LinkCategory> linkCategories = new ArrayList<>(Arrays.asList(new LinkedItems.LinkCategory("ChangeRequest", "CR"),
                new LinkedItems.LinkCategory("ChangeNotice", "CN"),
                new LinkedItems.LinkCategory("ReleasePackage", "RP"),
                new LinkedItems.LinkCategory("ECN", "ECN"),
                new LinkedItems.LinkCategory("TEAMCENTER", "TEAMCENTER")));
        LinkedItems linkedItems = EntityServiceDefaultInterface.super.getLinkedItems(id, linkCategories);
        Optional<LinkedItems.LinkCategory> changeRequestCategory = linkedItems.getCategories().stream().filter(linkCategory -> linkCategory.getName().equals("CHANGEREQUEST")).findFirst();
        //add change request as linked item
        if (changeRequestCategory.isPresent()) {
            ChangeRequest changeRequest = (ChangeRequest) self.getEntityById(id);
            LinkedItems.LinkItem linkItem = new LinkedItems.LinkItem(changeRequest.getId().toString(), "CHANGEREQUEST", changeRequest.getTitle());
            List<LinkedItems.LinkSubCategory> linkSubCategories = new ArrayList<>();
            List<LinkedItems.LinkItem> items = new ArrayList();
            items.add(linkItem);
            linkSubCategories.add(new LinkedItems.LinkSubCategory(items));
            changeRequestCategory.get().setTotalItems(items.size());
            changeRequestCategory.get().setSubCategories(linkSubCategories);
        }
        //map teamcenter Ids to ECNs
        Optional<LinkedItems.LinkCategory> ecnCategory = linkedItems.getCategories().stream().filter(linkCategory -> linkCategory.getName().equals("ECN")).findFirst();
        Optional<LinkedItems.LinkCategory> teamcenterCategory = linkedItems.getCategories().stream().filter(linkCategory -> linkCategory.getName().equals("TEAMCENTER")).findFirst();
        ecnCategory.get().getSubCategories().forEach(category -> category.getItems().forEach(ecnItem -> {
            teamcenterCategory.get().getSubCategories().forEach(teamcenterSubCategory -> {
                Optional<LinkedItems.LinkItem> teamcenterMatchedItem = teamcenterSubCategory.getItems().stream().filter(teamcenterItem -> teamcenterItem.getTitle().equals(ecnItem.getTitle())).findFirst();
                ecnItem.setId(teamcenterMatchedItem.isPresent() ? teamcenterMatchedItem.get().getId() : null);
            });
        }));
        if (ecnCategory.isPresent()) {
            List<LinkedItems.LinkItem> ecnItems = ecnCategory.get().getSubCategories().get(0).getItems().stream().filter(item -> Objects.nonNull(item.getId())).collect(Collectors.toList());
            ecnCategory.get().getSubCategories().get(0).setItems(ecnItems);
            ecnCategory.get().setTotalItems(ecnItems.size());
        }
        linkedItems.getCategories().remove(4);
        return linkedItems;
    }

    public CollaborationObjectCount getCollaborationObjectsCount(Long changeRequestId) {
        ChangeRequest changeRequest = (ChangeRequest) self.getEntityById(changeRequestId);
        List<ChangeRequestContext> changeRequestContexts = changeRequest.getContexts();
        if (Objects.isNull(changeRequestContexts)) {
            changeRequestContexts = new ArrayList<>();
        }
        CollaborationObjectCount collaborationObjectCount = new CollaborationObjectCount();
        Integer actionsCount = Math.toIntExact(changeRequestContexts.stream().filter(context -> context.getType().equals("ACTION")).count());
        Integer openActionsCount = Math.toIntExact(changeRequestContexts.stream().filter(context -> context.getType().equals("ACTION") && Objects.nonNull(context.getStatus()) && (context.getStatus().equals("OPEN") || context.getStatus().equals("ACCEPTED"))).count());
        Integer commentsCount = changeRequestCommentService.getCommentsCountByChangeRequestIdAndAuditor(changeRequestId);
        Integer otherDocumentsCount = changeRequestDocumentService.getOtherDocumentsCountByChangeRequestCommentIds(changeRequestId);
        ;
        Integer allDocumentsCount = 0;
        if (changeRequest.getChangeOwnerType().equals(PROJECT.name()))
            allDocumentsCount = changeRequestDocumentService.getDocumentsCountByChangeRequestId(changeRequestId);
        else
            allDocumentsCount = otherDocumentsCount;
        Slice<Id> idSlice = changeRequestCommentService.filterIds("changeRequest.id:" + changeRequestId, PageRequest.of(0, Integer.MAX_VALUE - 1));
        List<Long> commentIds = idSlice.getContent().stream().map(content -> content.getValue()).collect(Collectors.toList());
        Integer commentDocumentsCount = changeRequestDocumentService.getDocumentsCountByChangeRequestCommentIds(commentIds);
        collaborationObjectCount.setAllActionsCount(actionsCount);
        collaborationObjectCount.setOpenActionsCount(openActionsCount);
        collaborationObjectCount.setCommentsCount(commentsCount);
        collaborationObjectCount.setDocumentsCount(otherDocumentsCount);
        collaborationObjectCount.setAllAttachmentsCount(allDocumentsCount + commentDocumentsCount);
        return collaborationObjectCount;
    }

    @SecureFetchAction
    public StateOverviewByField getStatusCountByPriority(@SecureFetchCriteria String criteria, String viewCriteria) {
        Slice<Id> idSlice = self.filterIds(criteria, viewCriteria, Overview.class, PageRequest.of(0, Integer.MAX_VALUE - 1));
        List<StateOverviewByField.ChangeRequestStatusCountByFieldValue> changeRequestStatusCountsByField = new ArrayList<>();
        if (!Objects.requireNonNull(idSlice.getContent()).isEmpty()) {
            List<Long> ids = idSlice.getContent().stream().map(idItem -> idItem.getValue()).collect(Collectors.toList());
            List<StatusCountByPriority[]> statusCountByGroup = changeRequestRepository.getStatusCountByAnalysisPriority(ids);
            for (Object[] o : statusCountByGroup) {
                StateOverviewByField.ChangeRequestStatusCountByFieldValue changeRequestStatusCountByFieldValue = new StateOverviewByField.ChangeRequestStatusCountByFieldValue();
                changeRequestStatusCountByFieldValue.setCount(((StatusCountByPriority) o[0]).getCount());
                changeRequestStatusCountByFieldValue.setStatus(((StatusCountByPriority) o[0]).getStatus());
                changeRequestStatusCountByFieldValue.setType("analysisPriority");
                changeRequestStatusCountByFieldValue.setTypeCount(((StatusCountByPriority) o[0]).getAnalysisPriority());
                changeRequestStatusCountByFieldValue.setStatusLabel(ChangeRequestStatus.getLabelByCode((Integer) ((StatusCountByPriority) o[0]).getStatus()));
                changeRequestStatusCountsByField.add(changeRequestStatusCountByFieldValue);
            }

        }
        return new StateOverviewByField(changeRequestStatusCountsByField);
    }

    @SecureFetchAction
    public StatusOverview.StatusCount getChangeRequestStatusCount(@SecureFetchCriteria String criteria, String viewCriteria, Integer status) {
        Slice<Id> idSlice = self.filterIds(criteria, viewCriteria, Overview.class, PageRequest.of(0, Integer.MAX_VALUE - 1));
        Long count = 0L;
        if (!Objects.requireNonNull(idSlice.getContent()).isEmpty()) {
            List<Long> ids = idSlice.getContent().stream().map(idItem -> idItem.getValue()).collect(Collectors.toList());
            count = changeRequestRepository.getChangeRequestCountByStatus(ids, status);
        }
        return new StatusOverview.StatusCount(status, count, ChangeRequestStatus.getNameByCode(status));
    }

    @SecureCaseAction("READ")
    public WbsDto getWorkBreakdownStructure(Long changeRequestId) {
        ChangeRequest changeRequest = (ChangeRequest) self.getEntityById(changeRequestId);
        String projectId = changeRequest.getProjectId();
        if (Objects.isNull(projectId)) {
            return null;
        }
        return this.workBreakdownStructureService.getWbsByWbsId(projectId);//"1190-0002"
    }

    @SecureCaseAction("READ")
    public ProjectDto getProduct(Long changeRequestId) {
        ChangeRequest changeRequest = (ChangeRequest) self.getEntityById(changeRequestId);
        String productId = changeRequest.getProductId();
        if (Objects.isNull(productId)) {
            return null;
        }
        return this.projectService.getProjectByProjectId(productId);//"19091993"
    }

    @SecureCaseAction("READ")
    public User getProjectLead(Long changeRequestId) {
        ChangeRequest changeRequest = (ChangeRequest) self.getEntityById(changeRequestId);
        String projectId = changeRequest.getProjectId();
        if (Objects.isNull(projectId)) {
            return null;
        }
        ProjectLeadDto projectLeadDetails = this.projectLeadService.getProjectLeadByWbsId(projectId);//"1190-0002"
        if (Objects.isNull(projectLeadDetails)) {
            throw new InternalAssertionException("Error while fetching Project lead details");
        }
        if (Objects.isNull(projectLeadDetails.getUserId())) {
            throw new InternalAssertionException("Project lead not available for Project");
        }
        return getUserByUserId(projectLeadDetails.getUserId());
    }

    public PmoDetails getPmoDetails(String projectId) {
        PmoDto pmo = this.pmoService.getPmoByWbsId(projectId);//"1190-0002"
        PmoDetails pmoDetails = new PmoDetails();
        if (Objects.isNull(pmo)) {
            throw new InternalAssertionException("Error while fetching Pmo Details");
        }
        pmoDetails.setProjectId(pmo.getWbsId());
        pmoDetails.setDescription(pmo.getDescription());
        if (Objects.nonNull(pmo.getProjectLeadUserId())) {
            pmoDetails.setProjectLead(getUserByUserId(pmo.getProjectLeadUserId()));
        }
        if (Objects.nonNull(pmo.getPcmUserId())) {
            pmoDetails.setProjectClusterManager(getUserByUserId(pmo.getPcmUserId()));
        }
        if (Objects.nonNull(pmo.getPdmUserId())) {
            pmoDetails.setProductDevelopmentManager(getUserByUserId(pmo.getPdmUserId()));
        }
        return pmoDetails;
    }

    public User getUserByUserId(String userId) {
        try {
            return gdsUserService.getUserByUserId(userId);
        } catch (GdsUserNotFoundException e) {
            log.info("user not found in GDS for " + userId);
        }
        User user = new User();
        user.setUserId(userId);
        return user;
    }

    @SecureFetchAction
    public BaseEntityList<SearchSummary> getChangeRequestSearchSummary(@SecureFetchCriteria String criteria, String viewCriteria, Pageable pageable, Optional<String> sliceSelect) {
        Slice<BaseView> changeRequestSearchSummaryList = EntityServiceDefaultInterface.super.getEntitiesFromView(criteria, viewCriteria, pageable, sliceSelect, SearchSummary.class);
        BaseEntityList baseEntityList = new BaseEntityList(changeRequestSearchSummaryList);
        List<Long> ids = (List<Long>) baseEntityList.getResults().stream().map(item -> ((SearchSummary) item).getId()).collect(Collectors.toList());
        List<ChangeBoardRule> changeBoardRulesByChangeRequestIds = changeRequestRepository.getChangeBoardRulesByIds(ids);
        Map<Long, List<ChangeBoardRule>> changeBoardRules = changeBoardRulesByChangeRequestIds.stream().collect(Collectors.groupingBy(event -> event.getChangeRequestId()));
        baseEntityList.getResults().stream().forEach(item -> {
            Long id = ((SearchSummary) item).getId();
            RuleSet ruleSet = new RuleSet();
            if (changeBoardRules.containsKey(id)) {
                List<String> rules = changeBoardRules.get(id).stream().map(ruleset -> ruleset.getChangeBoardRule()).collect(Collectors.toList());
                ruleSet.setRules(rules);
                ruleSet.setRuleSetName(changeBoardRules.get(id).stream().iterator().next().getRuleSetName());
            }
            ((SearchSummary) item).setChangeBoardRuleSet(ruleSet);
        });
        return baseEntityList;
    }


    public BaseEntityList<AgendaLinkOverview> getChangeRequestAgendaLinkSummary(String[] agendaItemIds, Pageable pageable, Optional<String> sliceSelect, boolean includeRuleSet) {
        String viewCriteria = "agendaItemId@" + Arrays.asList(agendaItemIds).stream().collect(Collectors.joining(","));
        Slice<BaseView> agendaLinkOverviewList = EntityServiceDefaultInterface.super.getEntitiesFromView(viewCriteria, pageable, sliceSelect, AgendaLinkOverview.class);
        List<Long> changeRequestIds = new ArrayList<>();
        agendaLinkOverviewList.getContent().stream().forEach(changeRequest -> {
            changeRequestIds.add(((AgendaLinkOverview) changeRequest).getChangeRequestId());
            String projectId = ((AgendaLinkOverview) changeRequest).getProjectId();
            if (Objects.nonNull(projectId)) {
                PmoDetails pmoDetails;
                try {
                    pmoDetails = self.getPmoDetails(((AgendaLinkOverview) changeRequest).getProjectId());
                } catch (HanaEntityNotFoundException e) {
                    pmoDetails = new PmoDetails();
                    pmoDetails.setProjectId(projectId);
                    log.info("Fetching Pmo details of CR " + ((AgendaLinkOverview) changeRequest).getChangeRequestId() + " failed:: projectId "+ projectId);
                }
                ((AgendaLinkOverview) changeRequest).setPmoDetails(pmoDetails);
            }
        });
        if (includeRuleSet) {
            List<ChangeBoardRule> changeBoardRulesByChangeRequestIds = changeRequestRepository.getChangeBoardRulesByIds(changeRequestIds);
            Map<Long, List<ChangeBoardRule>> changeBoardRules = changeBoardRulesByChangeRequestIds.stream().collect(Collectors.groupingBy(event -> event.getChangeRequestId()));
            agendaLinkOverviewList.getContent().stream().forEach(item -> {
                Long id = ((AgendaLinkOverview) item).getChangeRequestId();
                RuleSet ruleSet = new RuleSet();
                if (changeBoardRules.containsKey(id)) {
                    List<String> rules = changeBoardRules.get(id).stream().map(ruleset -> ruleset.getChangeBoardRule()).collect(Collectors.toList());
                    ruleSet.setRules(rules);
                    ruleSet.setRuleSetName(changeBoardRules.get(id).stream().iterator().next().getRuleSetName());
                }
                ((AgendaLinkOverview) item).setChangeBoardRuleSet(ruleSet);
            });
        }
        return new BaseEntityList(agendaLinkOverviewList);
    }

    @SecureFetchAction
    public List<ChangeRequestCategory> getChangeRequestTrackerBoardSummary(@SecureFetchViewCriteria String viewCriteria, Pageable pageable, Optional<String> sliceSelect) {
        Slice<BaseView> changeRequestList = EntityServiceDefaultInterface.super.getEntitiesFromView(viewCriteria, pageable, sliceSelect, TrackerboardSummary.class);
        BaseEntityList baseEntityList = new BaseEntityList(changeRequestList);
        List<TrackerboardSummary> changeRequestSummary = baseEntityList.getResults();
        List<ChangeRequestCategory> changeRequestCategoryList = new ArrayList<>();
        //group by project and status
        Map<Object, Map<Integer, List<TrackerboardSummary>>> groupedData = changeRequestSummary.stream().collect(Collectors.groupingBy(event -> Optional.ofNullable(event.getProjectId()), Collectors.groupingBy(TrackerboardSummary::getStatus)));
        groupedData.keySet().stream().forEach(item -> {
            ChangeRequestCategory changeRequestCategory = new ChangeRequestCategory();
            if (((Optional) item).isEmpty()) {
                changeRequestCategory.setCategoryId(null);
            } else {
                String projectId = (String) ((Optional) item).get();
                changeRequestCategory.setCategoryId(projectId);
                try {
                    WbsDto projectDetails = this.workBreakdownStructureService.getWbsByWbsId(projectId);
                    if (Objects.nonNull(projectDetails)) {
                        changeRequestCategory.setDescription(projectDetails.getDescription());
                    }
                } catch(HanaEntityNotFoundException e) {
                    log.info("Project not found in Hana for project Id" + projectId);
                }
            }
            //populate all states for each project id
            List<ChangeRequestCategory.ChangeRequestStateCount> changeRequestStateList = initiateStateList();
            groupedData.get(item).keySet().stream().forEach(statusKey -> {
                Optional<ChangeRequestCategory.ChangeRequestStateCount> changeRequestState = changeRequestStateList.stream().filter(state -> state.getName().equals(ChangeRequestState.getStateByStatus(statusKey))).findFirst();
                if (changeRequestState.isPresent()) {
                    changeRequestState.get().setCount(changeRequestState.get().getCount() + Long.parseLong(String.valueOf(groupedData.get(item).get(statusKey).size())));
                    changeRequestState.get().getItems().addAll(groupedData.get(item).get(statusKey).stream().map(summary -> new ChangeRequestCategory.ChangeRequestBrief(summary.getId(), summary.getTitle())).collect(Collectors.toList()));
                }
            });
            changeRequestCategory.setSubCategories(changeRequestStateList);
            changeRequestCategoryList.add(changeRequestCategory);
        });
        return changeRequestCategoryList;
    }

    private List<ChangeRequestCategory.ChangeRequestStateCount> initiateStateList() {
        List list = new ArrayList();
        Arrays.stream(ChangeRequestState.values()).forEach(state -> {
            ChangeRequestCategory.ChangeRequestStateCount changeRequestStateCount = new ChangeRequestCategory.ChangeRequestStateCount();
            changeRequestStateCount.setName(state.name());
            changeRequestStateCount.setLabel(state.getStateLabel());
            changeRequestStateCount.setItems(new ArrayList<>());
            changeRequestStateCount.setCount(0L);
            changeRequestStateCount.setType("ChangeRequest");
            list.add(changeRequestStateCount);
        });
        return list;
    }

    public ScopeFieldVisibilityFactor getScopeFieldVisibilityFactor(Long id) {
        ScopeFieldVisibilityFactor scopeFieldVisibilityFactor = new ScopeFieldVisibilityFactor();
        Scope scope = this.scopeService.getScopeByChangeRequestId(id);
        if (Objects.isNull(scope)) {
            throw new EntityIdNotFoundException();
        }
        ImpactAnalysis impactAnalysis = this.impactAnalysisService.getImpactAnalysisByChangeRequestId(id);
        CustomerImpactDetail customerImpactDetail = new CustomerImpactDetail();
        customerImpactDetail.evaluatePartsToolingInScope(scope);
        customerImpactDetail.evaluatePartsManufacturedBefore(impactAnalysis);
        scopeFieldVisibilityFactor.setShowExistingPartQuestion(
                Objects.nonNull(customerImpactDetail.getPartsToolingInScope()) && customerImpactDetail.getPartsToolingInScope().toUpperCase().equals(Constants.NAME_YES));
        scopeFieldVisibilityFactor.setShowOtherQuestions(
                Objects.nonNull(customerImpactDetail.getPartsToolingInScope()) && customerImpactDetail.getPartsToolingInScope().toUpperCase().equals(Constants.NAME_YES)
                        && Objects.nonNull(customerImpactDetail.getPartsManufacturedBefore()) && customerImpactDetail.getPartsManufacturedBefore().toUpperCase().equals(Constants.NAME_YES));
        return scopeFieldVisibilityFactor;
    }

    public ChangeRequest getChangeRequestDetailsByAgendaItemId(String agendaItemId) {
        Long changeRequestId = getChangeRequestIdByAgendaItemId(agendaItemId);
        if (Objects.nonNull(changeRequestId))
            return (ChangeRequest) self.getEntityById(changeRequestId);
        return null;
    }

    public Long getChangeRequestIdByContext(String contextId, String contextType) {
        String criteria = "contexts.contextId:" + contextId + " and contexts.type:" + contextType;
        Slice<Id> idSlice = self.filterIds(criteria, PageRequest.of(0, 1));
        if (!idSlice.getContent().isEmpty()) {
            Long changeRequestId = Objects.requireNonNull(idSlice.getContent().get(0)).getValue();
            return changeRequestId;
        }
        throw new EntityIdNotFoundException();
    }

    public List<Problem> getProblemsByAgendaItemId(String agendaItemId) {
        Long changeRequestId =  self.getChangeRequestIdByAgendaItemId(agendaItemId);
        if (Objects.nonNull(changeRequestId))
            return self.getProblemsByChangeRequestId(changeRequestId);
        return new ArrayList<>();
    }

    public List<ProductBreakdownStructure> getProductBreakdownStructuresByAgendaItemId(String agendaItemId) {
        Long changeRequestId =  self.getChangeRequestIdByAgendaItemId(agendaItemId);
        if (Objects.nonNull(changeRequestId))
            return self.getProductBreakdownStructureByChangeRequestId(changeRequestId);
        return new ArrayList<>();
    }


    public Long getChangeRequestIdByAgendaItemId(String agendaItemId) {
        String viewCriteria = "agendaItemId:" + agendaItemId;
        Slice<BaseView> agendaItemContextList = EntityServiceDefaultInterface.super.getEntitiesFromView(viewCriteria, PageRequest.of(0,1), Optional.empty(), AgendaItemContext.class);
        if (!agendaItemContextList.getContent().isEmpty()) {
            AgendaItemContext agendaItemContext = (AgendaItemContext) agendaItemContextList.getContent().get(0);
            return agendaItemContext.getChangeRequestId();
        }
        return null;
    }

    public WbsDto getProjectByAgendaItemId(String agendaItemId) {
        Long changeRequestId = self.getChangeRequestIdByAgendaItemId(agendaItemId);
        if (Objects.nonNull(changeRequestId))
            return self.getWorkBreakdownStructure(changeRequestId);
        return null;
    }

    public ProjectDto getProductByAgendaItemId(String agendaItemId) {
        Long changeRequestId = self.getChangeRequestIdByAgendaItemId(agendaItemId);
        if (Objects.nonNull(changeRequestId))
            return self.getProduct(changeRequestId);
        return null;
    }

    public List<ChangeRequestProjectDto> getProductByAgendaItemIds(String[] agendaItemIds) {
        List<ChangeRequestProjectDto> changeRequestProjectDtos = new ArrayList<>();
        for (String agendaItemId: agendaItemIds) {
            Long changeRequestId = self.getChangeRequestIdByAgendaItemId(agendaItemId);
            if (Objects.nonNull(changeRequestId)) {
                ProjectDto projectDto = self.getProduct(changeRequestId);
                changeRequestProjectDtos.add(new ChangeRequestProjectDto(projectDto, changeRequestId));
            }
        }
        return changeRequestProjectDtos;
    }

    public PmoDetails getPmoDetailsByAgendaItemId(String agendaItemId) {
        Long changeRequestId = self.getChangeRequestIdByAgendaItemId(agendaItemId);
        if (Objects.isNull(changeRequestId))
            return null;
        ChangeRequest changeRequest = (ChangeRequest) self.getEntityById(changeRequestId);
        String projectId = changeRequest.getProjectId();
        if (Objects.nonNull(projectId))
            return getPmoDetails(projectId);
        return null;
    }

    public void updateMyTeamWithChangeObjectMyTeam(List<MyTeamMember> myTeamMembers, String changeRequestNumber) {
        Long changeRequestId = null;
        try {
            changeRequestId = Long.parseLong(changeRequestNumber);
        } catch (NumberFormatException e) {
            log.info("change request number from impacted item is invalid :: " + changeRequestNumber);
        }
        if (Objects.nonNull(changeRequestId))
            changeRequestMyTeamService.syncMyTeamForCreatorsAndUsers(myTeamMembers, changeRequestId);
    }

    @Override
    public AggregateInterface getChangeLogAggregate(long id, Class<AggregateInterface> aggregateClass, boolean includeDeleted) {
        ChangeRequestChangeLogAggregate changeRequestChangeLogAggregate = (ChangeRequestChangeLogAggregate) AuditServiceDefaultInterface.super.getChangeLogAggregate(id, aggregateClass, includeDeleted);
        return AuditHelper.handleAuditEntriesForContexts(changeRequestChangeLogAggregate);
    }

    @Override
    public List<StatusCountOverview> getStatusCountOverview(String criteria, String viewCriteria, StatusInterface[] statuses, Optional<Class> viewClass) {
        List<StatusCountOverview> statusCountOverview = EntityServiceDefaultInterface.super.getStatusCountOverview(criteria, viewCriteria, statuses, Optional.of(Overview.class));
        return statusCountOverview.stream().filter(overviewItem -> !(overviewItem.getName().equals(ChangeRequestStatus.OBSOLETED.getStatusCode())
                || overviewItem.getName().equals(ChangeRequestStatus.CLOSED.getStatusCode()) || overviewItem.getName().equals(ChangeRequestStatus.REJECTED.getStatusCode()))).collect(Collectors.toList());
    }

    public void updateChangeOwner(Long changeRequestId, User changeOwner) {
        ChangeRequest changeRequest = (ChangeRequest) self.getEntityById(changeRequestId);
        if (!Objects.equals(changeRequest.getChangeOwner(), changeOwner)) {
            changeRequest.setChangeOwner(changeOwner);
            Map<String, Object> changedAttrs = new HashMap<>();
            changedAttrs.put("change_owner", changeOwner);
            self.update(changeRequest, changedAttrs);
        }
    }

    public ChangeRequestSummaryScia getChangeRequestSummaryForScm(Long changeRequestId) {
        ChangeRequest changeRequest = (ChangeRequest) self.getEntityById(changeRequestId);
        return new ChangeRequestSummaryScia(changeRequest);
    }

    /*@SecureCaseAction("CREATE_SCIA")
    public SciaSummary createScia(Long id, Scia scia) {
        SciaContext sciaContext = new SciaContext();
        sciaContext.setContextId("" + id);
        sciaContext.setName(scia.getTitle());
        sciaContext.setType("CHANGEREQUEST");
        ChangeRequest changeRequest = (ChangeRequest) self.getEntityById(id);
        sciaContext.setStatus("" + changeRequest.getStatus());
        List<SciaContext> sciaContextList = new ArrayList<>();
        sciaContextList.add(sciaContext);
        scia.setContexts(sciaContextList);

        SciaSummary createdScia = sciaService.createScia(scia);
        ChangeRequestContext changeRequestSciaContext = new ChangeRequestContext("SCIA", "" + createdScia.getId(), createdScia.getTitle(), "" + createdScia.getStatus());
        List<ChangeRequestContext> changeRequestContexts = changeRequest.getContexts();
        if (Objects.isNull(changeRequestContexts))
            changeRequestContexts = new ArrayList<>();
        changeRequestContexts.add(changeRequestSciaContext);
        changeRequest.setContexts(changeRequestContexts);
        Map<String, Object> changedAttrs = new HashMap<>();
        changedAttrs.put("contexts", changeRequestContexts);
        self.update(changeRequest, changedAttrs);
        return createdScia;
    }

    /* public List<SciaSummary> getScias(Long changeRequestId) {
        return sciaService.getSciasByChangeRequestId(changeRequestId);
    }

    @SecureCaseAction("CREATE_SCIA")
    public SciaSummary cloneScia(Long changeRequestId, Long sciaId) {
        SciaSummary createdScia = sciaService.copyScia(sciaId);
        ChangeRequest changeRequest = (ChangeRequest) self.getEntityById(changeRequestId);
        List<ChangeRequestContext> changeRequestNewContextList = new ArrayList<>();
        changeRequestNewContextList.addAll(changeRequest.getContexts());
        ChangeRequestContext changeRequestSciaContext = new ChangeRequestContext("SCIA", "" + createdScia.getId(), createdScia.getTitle(), "" + createdScia.getStatus());
        System.out.println("##########1111111111====" + changeRequestSciaContext.getName() + " " + changeRequestSciaContext.getContextId());
        changeRequestNewContextList.add(changeRequestSciaContext);
        changeRequest.setContexts(changeRequestNewContextList);
        changeRequestNewContextList.forEach(ctx -> System.out.println("##########22222====" + ctx.getName() + " " + ctx.getContextId()));
        self.update(changeRequest);
        return createdScia;
    }*/

    @SecureFetchAction
    public BaseEntityList<GlobalSearch> getChangeRequestsForGlobalSearch(@SecureFetchViewCriteria String viewCriteria, Pageable pageable, Optional<String> sliceSelect) {
        Slice<BaseView> changeRequestOverviewList = EntityServiceDefaultInterface.super.getEntitiesFromView(viewCriteria, pageable, sliceSelect, GlobalSearch.class);
        return new BaseEntityList(changeRequestOverviewList);
    }

    public IsFirstDraft getChangeRequestIsFirstDraft(Long id) {
        IsFirstDraft isFirstDraft = new IsFirstDraft();
        isFirstDraft.setIsFirstDraft(!AuditServiceDefaultInterface.super.propertyHadSpecifiedValueEarlier(id,"status",ChangeRequestStatus.DRAFTED.getStatusCode()));
        return isFirstDraft;
    }

    @SneakyThrows
    @Transactional
    public ChangeRequest mergeEntityBySystemUser(BaseEntityInterface newInst, BaseEntityInterface oldInst, List<String> oldInsChangedAttributeNames, List<String> newInsChangedAttributeNames) {
        List<String> changedProperties = ObjectMapperUtil.getChangedProperties(oldInst, newInst);
        log.info("newInst " + newInsChangedAttributeNames + " oldInst " + oldInsChangedAttributeNames);
        ChangeRequest updatedChangeRequest = (ChangeRequest) self.merge(newInst, oldInst, oldInsChangedAttributeNames, newInsChangedAttributeNames);
        //update my team
        log.info("updateMyteam " + changedProperties.toArray() + " size " + changedProperties.size());
        updateMyTeamOnChangeRequestUpdate(changedProperties, updatedChangeRequest);
        return updatedChangeRequest;
    }

    public void updateMyTeamMemberFields(User user, Long linkedEntityId, String role) {
        Map<String, Object> changedAttrs = new HashMap<>();
        ChangeRequest changeRequest = (ChangeRequest) self.getEntityById(linkedEntityId);
        if (Objects.equals(role, MyChangeRoles.changeSpecialist1.getRole())) {
            changeRequest.setChangeSpecialist1(user);
            changedAttrs.put("change_specialist1", user);
        } else if (Objects.equals(role, MyChangeRoles.changeSpecialist2.getRole())) {
            changeRequest.setChangeSpecialist2(user);
            changedAttrs.put("change_specialist2", user);
        } else if (Objects.equals(role, MyChangeRoles.changeOwner.getRole())) {
            changeRequest.setChangeOwner(user);
            changedAttrs.put("change_owner", user);
        } else {
            return;
        }
        self.update(changeRequest, changedAttrs);
    }

    public ChangeRequest createChangeRequestInApprovedStatus(ChangeRequestAggregate aggregate) {
        ChangeRequestAggregate changeRequestAggregate = (ChangeRequestAggregate) EntityServiceDefaultInterface.super.createRootAggregate(aggregate);
        changeRequestMyTeamService.addSubmitterRequesterToMyTeam(changeRequestAggregate);
        ChangeRequest changeRequest = changeRequestAggregate.getDescription();
        //update CS1,CS2 to myteam
        changeRequestMyTeamService.addChangeSpecialist1ToMyTeam(changeRequest.getChangeSpecialist1(), changeRequest.getId());
        changeRequestMyTeamService.addChangeSpecialist2ToMyTeam(changeRequest.getChangeSpecialist2(), changeRequest.getId());
        //update project lead in myteam
        addProjectLeadToMyTeam(changeRequest);
        //Submit CR => dia bom
        ChangeRequest submittedChangeRequest = submit(changeRequest);
        //define solution
        defineSolution(submittedChangeRequest);
        //Evaluate CIA
        Long scopeId = changeRequestAggregate.getScope().getId();
        Scope updatedScope = (Scope) scopeService.getEntityById(scopeId);
        customerImpactService.evaluateCustomerImpactDetail(updatedScope);
        //.analyze impact
        changeRequest = (ChangeRequest) self.getEntityById(changeRequest.getId());
        ChangeRequest impactAnalyzedChangeRequest = analyzeImpact(changeRequest);
        //approve
        ChangeRequest approvedChangeRequest = this.approve(impactAnalyzedChangeRequest);
        return approvedChangeRequest;
    }
}
