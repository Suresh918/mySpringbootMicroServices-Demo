package com.example.mirai.projectname.changerequestservice.myteam.service;

import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.model.*;
import com.example.mirai.libraries.entity.model.Id;
import com.example.mirai.libraries.event.annotation.PublishResponse;
import com.example.mirai.libraries.gds.service.GdsGroupService;
import com.example.mirai.libraries.gds.service.GdsUserService;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.libraries.myteam.model.dto.Member;
import com.example.mirai.libraries.myteam.model.dto.MyTeamBulkUpdate;
import com.example.mirai.libraries.myteam.model.dto.MyTeamPublishData;
import com.example.mirai.libraries.myteam.model.dto.TeamDetails;
import com.example.mirai.libraries.myteam.service.MyTeamMemberService;
import com.example.mirai.libraries.myteam.service.MyTeamService;
import com.example.mirai.libraries.myteam.service.PreferredRolesService;
import com.example.mirai.libraries.security.abac.component.AbacProcessor;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.example.mirai.libraries.security.core.component.EntityACL;
import com.example.mirai.libraries.security.core.component.PropertyACL;
import com.example.mirai.libraries.security.rbac.component.RbacProcessor;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeOwnerType;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestAggregate;
import com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate.ChangeRequestDetail;
import com.example.mirai.projectname.changerequestservice.changerequest.model.dto.ChangeRequestList;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestService;
import com.example.mirai.projectname.changerequestservice.core.component.EntityResolver;
import com.example.mirai.projectname.changerequestservice.myteam.model.ChangeRequestMyTeam;
import com.example.mirai.projectname.changerequestservice.myteam.model.aggregate.ChangeRequestMyTeamAggregate;
import com.example.mirai.projectname.changerequestservice.myteam.model.aggregate.ChangeRequestMyTeamMemberAggregate;
import com.example.mirai.projectname.changerequestservice.myteam.repository.ChangeRequestMyTeamRepository;
import com.example.mirai.projectname.changerequestservice.shared.service.AggregateEventBuilder;
import com.example.mirai.projectname.libraries.model.MyChangeRoles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@EntityClass(ChangeRequestMyTeam.class)
public class ChangeRequestMyTeamService extends MyTeamService {
    @Resource
    private ChangeRequestMyTeamService self;

    @Autowired
    private ChangeRequestService changeRequestService;


    protected DelegatingSecurityContextAsyncTaskExecutor executor;

    private MyTeamMemberService myTeamMemberService;

    @Autowired(required = false)
    private JmsTemplate jmsTemplate;

    public ChangeRequestMyTeamService(MyTeamMemberService myTeamMemberService, PreferredRolesService preferredRolesService, AbacProcessor abacProcessor,
                                      RbacProcessor rbacProcessor, EntityACL acl, PropertyACL pacl, GdsUserService gdsUserService,
                                      GdsGroupService gdsGroupService,
                                      CaseActionList caseActionList,
                                      ChangeRequestMyTeamRepository changeRequestMyTeamRepository,
                                      DelegatingSecurityContextAsyncTaskExecutor executor,
                                      EntityResolver entityResolver) {
        super(myTeamMemberService, preferredRolesService, abacProcessor, rbacProcessor, acl, pacl, caseActionList, gdsUserService, gdsGroupService, entityResolver, MyChangeRoles.class);
        this.executor = executor;
        super.jmsTemplate = jmsTemplate;
        this.myTeamMemberService = myTeamMemberService;

    }

    public TeamDetails getTeamDetails(Long changeRequestId) {
        List<String> groups = new ArrayList<>();
        ChangeRequest changeRequest = (ChangeRequest) changeRequestService.getEntityById(changeRequestId);
        if (Objects.nonNull(changeRequest.getChangeBoards()))
            groups.addAll(changeRequest.getChangeBoards());
        if (Objects.nonNull(changeRequest.getChangeControlBoards()))
            groups.addAll(changeRequest.getChangeControlBoards());
        return super.getTeamDetails(changeRequestId, groups);
    }

    @Override
    public MyTeamService getSelf() {
        return self;
    }

    @Override
    public Long getMyTeamIdByLinkedEntity(Long changeRequestId) {
        String criteria = "changeRequest.id:" + changeRequestId;
        Pageable pageable = PageRequest.of(0, 1);
        Slice<Id> idSlice = this.filterIds(criteria, pageable);
        return (Objects.nonNull(idSlice.getContent()) && idSlice.getNumberOfElements() > 0) ? idSlice.getContent().get(0).getValue() : null;
    }

    @Override
    public Class getMyTeamAggregateClass() {
        return ChangeRequestMyTeamAggregate.class;
    }

    @Override
    @PublishResponse(eventType = "ADD_MYTEAM_MEMBER", eventBuilder = AggregateEventBuilder.class,
            responseClass = MyTeamPublishData.class, eventEntity = "com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest",
            destination = "com.example.mirai.projectname.changerequestservice.myteam")
    public MyTeamMember publishAddedMyTeamMember(MyTeamMember myTeamMember) {
        return myTeamMember;
    }

    @Override
    @PublishResponse(eventType = "UPDATE_MYTEAM_MEMBER", eventBuilder = AggregateEventBuilder.class,
            responseClass = MyTeamPublishData.class, eventEntity = "com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest",
            destination = "com.example.mirai.projectname.changerequestservice.myteam")
    public MyTeamMember publishUpdatedMyTeamMember(MyTeamMember myTeamMember) {
        return myTeamMember;
    }

    @Override
    @PublishResponse(eventType = "DELETE_MYTEAM_MEMBER", eventBuilder = AggregateEventBuilder.class,
            responseClass = MyTeamPublishData.class, eventEntity = "com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest",
            destination = "com.example.mirai.projectname.changerequestservice.myteam")
    public MyTeamMember publishDeletedMyTeamMember(MyTeamMember myTeamMember) {
        return myTeamMember;
    }

    @Override
    public CaseStatus getCaseStatus(BaseEntityInterface baseEntityInterface) {
        return null;
    }

    /*@Transactional
    public void updateMyTeamPreferredRoles(String userId, List<String> roles) {
        self.updatePreferredRoles(userId, roles);
    }*/

    public void addSubmitterRequesterToMyTeam(ChangeRequestAggregate changeRequestAggregate) {
        MyTeamMember myTeamMember = new MyTeamMember();
        myTeamMember.setUser(changeRequestAggregate.getDescription().getCreator());
        myTeamMember.setRoles(new ArrayList<>(Arrays.asList(MyChangeRoles.submitterRequestor.getRole())));
        Long myTeamId = changeRequestAggregate.getMyTeamDetails().getMyTeam().getId();
        Member member = self.addUserToMyTeamByMyTeamId(MyChangeRoles.submitterRequestor, myTeamMember.getUser(), myTeamId);
        myTeamMember.setId(member.getId());
        ChangeRequestMyTeamMemberAggregate myTeamMemberAggregate = new ChangeRequestMyTeamMemberAggregate();
        myTeamMemberAggregate.setMember(myTeamMember);
        changeRequestAggregate.getMyTeamDetails().getMembers().add(myTeamMemberAggregate);
    }

    public void addProjectLeadToMyTeam(User projectLead, Long changeRequestId, boolean isProjectCR) {
        log.info("adding project lead " + projectLead.toString());
        self.addUserToMyTeamByParentId(MyChangeRoles.developmentAndEngineeringProjectLead, projectLead, changeRequestId);
        if (isProjectCR) {
            self.addUserToMyTeamByParentId(MyChangeRoles.changeOwner, projectLead, changeRequestId);
        }
    }

    public void addChangeSpecialist1ToMyTeam(User changeSpecialist1, Long changeRequestId) {
        self.addUserToMyTeamByParentId(MyChangeRoles.changeSpecialist1, changeSpecialist1, changeRequestId);
    }

    public void addChangeSpecialist2ToMyTeam(User changeSpecialist2, Long changeRequestId) {
        self.addUserToMyTeamByParentId(MyChangeRoles.changeSpecialist2, changeSpecialist2, changeRequestId);
    }

    public void addChangeOwnerToMyTeam(User changeOwner, Long changeRequestId) {
        if (Objects.nonNull(changeOwner))
            self.addUserToMyTeamByParentId(MyChangeRoles.changeOwner, changeOwner, changeRequestId);
        else
            deleteRoleFromMyTeamMember(changeRequestId, MyChangeRoles.changeOwner);
    }

    public User getProjectLead(Long changeRequestId) {
        Long myTeamId = getMyTeamIdByLinkedEntity(changeRequestId);
        List<MyTeamMember> usesWithProjectLeadRole = getMembersByRole(MyChangeRoles.developmentAndEngineeringProjectLead.getRole(), myTeamId);
        if (usesWithProjectLeadRole.isEmpty()) {
            return null;
        }
        return usesWithProjectLeadRole.get(0).getUser();
    }

    public void deleteRoleFromMyTeamMember(MyChangeRoles role, Long changeRequestId) {
        Long myTeamId = getMyTeamIdByLinkedEntity(changeRequestId);
        User projectLead = getProjectLead(changeRequestId);
        if (Objects.nonNull(projectLead))
            deleteRoleFromMyTeamMember(projectLead, role, myTeamId);
    }

    @Transactional
    public ChangeRequestDetail.MyTeamDetail updateMyTeamForCreatorsAndUsers(Long changeRequestId, List<MyTeamMember> myTeamMembers) {
        Long myTeamId = getMyTeamIdByLinkedEntity(changeRequestId);
        syncMyTeamForCreatorsAndUsers(myTeamMembers, changeRequestId);
        ChangeRequestMyTeamAggregate ChangeRequestMyTeamAggregate = (com.example.mirai.projectname.changerequestservice.myteam.model.aggregate.ChangeRequestMyTeamAggregate) getAggregate(myTeamId, (Class) com.example.mirai.projectname.changerequestservice.myteam.model.aggregate.ChangeRequestMyTeamAggregate.class);
        return new ChangeRequestDetail.MyTeamDetail(ChangeRequestMyTeamAggregate);
    }

    public void syncMyTeamForCreatorsAndUsers(List<MyTeamMember> changeObjectMyTeamMembers, Long changeRequestId) {
        Long changeRequestMyTeamId = getMyTeamIdByLinkedEntity(changeRequestId);
        if (Objects.isNull(changeRequestMyTeamId))
            return;

        Set<EntityLink<BaseEntityInterface>> entityLinkSet = new HashSet<>();
        entityLinkSet.add(new EntityLink(changeRequestMyTeamId, ChangeRequestMyTeam.class));

        changeObjectMyTeamMembers.stream().forEach(myTeamMember -> myTeamMember.setId(null));
        updateMyTeamMembersWithRole(changeRequestMyTeamId, changeObjectMyTeamMembers, MyChangeRoles.creator.getRole(), entityLinkSet);
        updateMyTeamMembersWithRole(changeRequestMyTeamId, changeObjectMyTeamMembers, MyChangeRoles.user.getRole(), entityLinkSet);
        Optional<MyTeamMember> changeOwner = changeObjectMyTeamMembers.stream().filter(myTeamMember -> myTeamMember.getRoles().contains(MyChangeRoles.changeOwner.getRole())).findFirst();
        if (changeOwner.isPresent()) {
            log.info("in syncing CO my team " + changeOwner.get().toString());
            addOrUpdateMember(changeOwner.get(), entityLinkSet, MyChangeRoles.changeOwner.getRole());
            changeRequestService.updateChangeOwner(changeRequestId, changeOwner.get().getUser());
        }

    }

    public void updateMyTeamMembersWithRole(Long changeRequestMyTeamId, List<MyTeamMember> changeObjectMyTeamMembers, String role, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
        List<MyTeamMember> membersWithRoleInChangeRequestMyTeam = getMyTeamMembersByRole(role, changeRequestMyTeamId);
        List<MyTeamMember> membersWithRoleInChangeObjectMyTeam = changeObjectMyTeamMembers.stream().filter(myTeamMember -> myTeamMember.getRoles().contains(role)).collect(Collectors.toList());

        List<MyTeamMember> roleRemovedFromMembers = membersWithRoleInChangeRequestMyTeam.stream().filter(myTeamMember -> isUserExistInMyTeamWithRole(membersWithRoleInChangeObjectMyTeam, myTeamMember, role).isEmpty()).collect(Collectors.toList());
        List<MyTeamMember> roleAddedForMembers = membersWithRoleInChangeObjectMyTeam.stream().filter(myTeamMember -> isUserExistInMyTeamWithRole(membersWithRoleInChangeRequestMyTeam, myTeamMember, role).isEmpty()).collect(Collectors.toList());

        roleRemovedFromMembers.forEach(member -> deleteRoleFromMyTeamMember(member, role));

        roleAddedForMembers.forEach(member -> {
            MyTeamMember myTeamMember = new MyTeamMember();
            myTeamMember.setUser(member.getUser());
            List<String> roles = new ArrayList<>(Arrays.asList(role));
            myTeamMember.setRoles(roles);
            addOrUpdateMemberWithSharedRole(myTeamMember, entityLinkSet);
        });
    }

    private Optional<MyTeamMember> isUserExistInMyTeamWithRole(List<MyTeamMember> usersWithRoleInMyTeam, MyTeamMember myTeamMember, String role) {
        if (usersWithRoleInMyTeam.isEmpty())
            return Optional.empty();
        return usersWithRoleInMyTeam.stream().filter(member -> member.getUser().equals(myTeamMember.getUser()) && member.getRoles().contains(role)).findFirst();
    }

    public void addMyTeamMemberForMigration(Long MyTeamId, MyTeamMember myTeamMember) {
        HashSet<EntityLink<BaseEntityInterface>> entityLinkSet = new HashSet();
        entityLinkSet.add(new EntityLink(MyTeamId, ChangeRequestMyTeam.class));
        addMemberWithoutPublish(myTeamMember, entityLinkSet);
    }

    public void deleteChangeOwnerFromMyTeam(Long changeRequestId) {
        deleteRoleFromMyTeamMember(changeRequestId, MyChangeRoles.changeOwner);
    }

    public void deleteRoleFromMyTeamMember(Long changeRequestId, MyChangeRoles myChangeRoles) {
        Long changeRequestMyTeamId = getMyTeamIdByLinkedEntity(changeRequestId);
        List<MyTeamMember> membersWithRole = getMembersByRole(myChangeRoles.getRole(), changeRequestMyTeamId);
        membersWithRole.forEach(myTeamMember -> deleteRoleFromMyTeamMember(myTeamMember, myChangeRoles.getRole()));
    }

    public void deleteProjectLeadFromMyTeam(ChangeRequest changeRequest) {
        deleteRoleFromMyTeamMember(changeRequest.getId(), MyChangeRoles.developmentAndEngineeringProjectLead);
        if (changeRequest.getChangeOwnerType().equals(ChangeOwnerType.PROJECT.name()))
            deleteRoleFromMyTeamMember(changeRequest.getId(), MyChangeRoles.changeOwner);
    }

    @Override
    public List<String> getUnupdateableRoles() {
        return new ArrayList(Arrays.asList(MyChangeRoles.submitterRequestor.getRole(), MyChangeRoles.changeSpecialist1.getRole(),
                MyChangeRoles.changeSpecialist2.getRole(), MyChangeRoles.developmentAndEngineeringProjectLead.getRole(),
                MyChangeRoles.changeOwner.getRole(), MyChangeRoles.creator.getRole(), MyChangeRoles.user.getRole()));
    }

    @Override
    public List<String> getNonSharedRoles() {
        return new ArrayList(Arrays.asList(MyChangeRoles.submitterRequestor.getRole(), MyChangeRoles.changeSpecialist1.getRole(),
                MyChangeRoles.changeSpecialist2.getRole(), MyChangeRoles.developmentAndEngineeringProjectLead.getRole(),
                MyChangeRoles.changeSpecialist3.getRole(), MyChangeRoles.ecnExecutor.getRole(),
                MyChangeRoles.changeOwner.getRole()));
    }
    @Override
    public void setLinkedEntityIdsByFilter(MyTeamBulkUpdate myTeamBulkUpdate) {
        Slice<BaseView> idSlice = changeRequestService.getEntitiesFromView(myTeamBulkUpdate.getViewCriteria(), PageRequest.of(0, Integer.MAX_VALUE - 1), Optional.empty(), ChangeRequestList.class);
        if (Objects.nonNull(idSlice) && idSlice.getNumberOfElements() > 0) {
            List<Long> linkedEntityIds = idSlice.getContent().stream().map(item -> ((ChangeRequestList)item).getId()).collect(Collectors.toList());
            myTeamBulkUpdate.setCaseObjectIds(linkedEntityIds);
        }
    }

    @Override
    public String getCaseObjectType() {
        return "CR";
    }

    @Override
    public void setLinkedEntityNumbers(MyTeamBulkUpdate myTeamBulkUpdate) {
        if (Objects.nonNull(myTeamBulkUpdate.getCaseObjectIds())) {
            List<String> changeRequestNumbers = myTeamBulkUpdate.getCaseObjectIds().stream().map(Object::toString).collect(Collectors.toList());
            myTeamBulkUpdate.setCaseObjectNumbers(changeRequestNumbers);
        }
    }

    @Override
    public String getRoleLabel(String roleName) {
        return MyChangeRoles.getLabel(roleName);
    }

    public void deleteMyTeam(Long myTeamId, Set<ChangeRequestMyTeamMemberAggregate> members) {
        Iterator<ChangeRequestMyTeamMemberAggregate> iterator = members.iterator();
        while (iterator.hasNext()) {
            ChangeRequestMyTeamMemberAggregate memberAggregate = iterator.next();
            myTeamMemberService.delete(memberAggregate.getMember().getId());
        }
        self.delete(myTeamId);
    }

    @Override
    protected void updateEntityWithNewMember(User userToAdd, Long linkedEntityId, String role) {
        if (Objects.equals(role, MyChangeRoles.changeSpecialist1.getRole()) || Objects.equals(role, MyChangeRoles.changeSpecialist2.getRole()) || Objects.equals(role, MyChangeRoles.changeOwner.getRole()))
        changeRequestService.updateMyTeamMemberFields(userToAdd, linkedEntityId, role);
    }
}
