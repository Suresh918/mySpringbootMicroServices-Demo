package com.example.mirai.projectname.releasepackageservice.myteam.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.BaseView;
import com.example.mirai.libraries.core.model.EntityLink;
import com.example.mirai.libraries.core.model.User;
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
import com.example.mirai.projectname.libraries.model.MyChangeRoles;
import com.example.mirai.projectname.releasepackageservice.core.component.EntityResolver;
import com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam;
import com.example.mirai.projectname.releasepackageservice.myteam.model.aggregate.ReleasePackageMyTeamAggregate;
import com.example.mirai.projectname.releasepackageservice.myteam.model.aggregate.ReleasePackageMyTeamMemberAggregate;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ChangeOwnerType;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.ReleasePackageAggregate;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.ReleasePackageDetail;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.ReleasePackageList;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageService;
import com.example.mirai.projectname.releasepackageservice.shared.AggregateEventBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@EntityClass(ReleasePackageMyTeam.class)
public class ReleasePackageMyTeamService extends MyTeamService {

    @Resource
    ReleasePackageMyTeamService self;

    @Autowired
    ReleasePackageService releasePackageService;

    private MyTeamMemberService myTeamMemberService;


    public ReleasePackageMyTeamService(MyTeamMemberService myTeamMemberService, PreferredRolesService preferredRolesService, AbacProcessor abacProcessor,
                                       RbacProcessor rbacProcessor, EntityACL acl, PropertyACL pacl, GdsUserService gdsUserService, GdsGroupService gdsGroupService,
                                       CaseActionList caseActionList, EntityResolver entityResolver, JmsTemplate jmsTemplate) {
        super(myTeamMemberService, preferredRolesService, abacProcessor, rbacProcessor, acl, pacl, caseActionList, gdsUserService, gdsGroupService, entityResolver, MyChangeRoles.class);
        super.jmsTemplate = jmsTemplate;
        this.myTeamMemberService = myTeamMemberService;
    }

    public TeamDetails getTeamDetails(Long releasePackageId) {
        List<String> groups = new ArrayList<>();
        ReleasePackage releasePackage = (ReleasePackage) releasePackageService.getEntityById(releasePackageId);
        if (Objects.nonNull(releasePackage.getChangeControlBoards()))
            groups.addAll(releasePackage.getChangeControlBoards());
        return super.getTeamDetails(releasePackageId, groups);
    }

    public void addSubmitterRequesterToMyTeam(ReleasePackageAggregate releasePackageAggregate) {
        addRoleToAggregate(MyChangeRoles.submitterRequestor, releasePackageAggregate, releasePackageAggregate.getReleasePackage().getCreator());
    }

    public void addMyTeamMembersFromChangeNotice(Set<ReleasePackageMyTeamMemberAggregate> releasePackageMyTeamMemberAggregate, ReleasePackageAggregate releasePackageAggregate) {
        boolean isSubmitterRequestorAdded = false;
        for (ReleasePackageMyTeamMemberAggregate myTeamMemberAggregate : releasePackageMyTeamMemberAggregate) {
            MyTeamMember member = myTeamMemberAggregate.getMember();
            if (member.getUser().equals(releasePackageAggregate.getReleasePackage().getCreator())) {
                member.getRoles().add("submitterRequestor");
                isSubmitterRequestorAdded = true;
            }
            addChangeNoticeMyTeam(member, releasePackageAggregate);
        }
        if (!isSubmitterRequestorAdded)
            addSubmitterRequesterToMyTeam(releasePackageAggregate);
    }

    private MyChangeRoles getMyChangeRoleByRoleValue(String role) {
        Optional<MyChangeRoles> myChangeRoles = Arrays.stream(MyChangeRoles.values()).filter(value -> value.getRole().equals(role)).findFirst();
        if (myChangeRoles.isPresent())
            return myChangeRoles.get();
        return null;
    }

    public void addChangeSpecialist2ToMyTeam(ReleasePackageAggregate releasePackageAggregate, User cs2) {
        if (Objects.nonNull(cs2))
            addRoleToAggregate(MyChangeRoles.changeSpecialist2, releasePackageAggregate, cs2);
    }

    public void addRoleToAggregate(MyChangeRoles myChangeRole, ReleasePackageAggregate releasePackageAggregate, User user) {
        MyTeamMember myTeamMember = new MyTeamMember();
        myTeamMember.setUser(user);
        myTeamMember.setRoles(new ArrayList<>(Arrays.asList(myChangeRole.getRole())));
        Long myTeamId = releasePackageAggregate.getMyTeamDetails().getMyTeam().getId();
        Member member = self.addUserToMyTeamByMyTeamId(myChangeRole, myTeamMember.getUser(), myTeamId);
        myTeamMember.setId(member.getId());
        ReleasePackageMyTeamMemberAggregate myTeamMemberAggregate = new ReleasePackageMyTeamMemberAggregate();
        myTeamMemberAggregate.setMember(myTeamMember);
        releasePackageAggregate.getMyTeamDetails().getMembers().add(myTeamMemberAggregate);
    }

    public void addChangeNoticeMyTeam(MyTeamMember myTeamMember, ReleasePackageAggregate releasePackageAggregate) {
        Long myTeamId = releasePackageAggregate.getMyTeamDetails().getMyTeam().getId();
        Set<EntityLink<BaseEntityInterface>> entityLinkSet = new HashSet<>();
        entityLinkSet.add(new EntityLink(myTeamId, this.getEntityClass()));

        Member member = self.addMember(myTeamMember, entityLinkSet);
        myTeamMember.setId(member.getId());
        ReleasePackageMyTeamMemberAggregate myTeamMemberAggregate = new ReleasePackageMyTeamMemberAggregate();
        myTeamMemberAggregate.setMember(myTeamMember);
        releasePackageAggregate.getMyTeamDetails().getMembers().add(myTeamMemberAggregate);
    }

    public void addExecutorToMyTeam(User executor, Long releasePackageId) {
        self.addUserToMyTeamByParentId(MyChangeRoles.ecnExecutor, executor, releasePackageId);
    }

    public void addPlmCoordinatorToMyTeam(User plmCoordinator, Long releasePackageId) {
        self.addUserToMyTeamByParentId(MyChangeRoles.coordinatorSCMPLM, plmCoordinator, releasePackageId);
    }

    public void addChangeSpecialist3ToMyTeam(User changeSpecialist3, Long releasePackageId) {
        self.addUserToMyTeamByParentId(MyChangeRoles.changeSpecialist3, changeSpecialist3, releasePackageId);
    }

    public void addProjectLeadToMyTeam(User projectLead, Long releasePackageId) {
        self.addUserToMyTeamByParentId(MyChangeRoles.developmentAndEngineeringProjectLead, projectLead, releasePackageId);
    }

    public void deleteProjectLeadFromMyTeam(ReleasePackage releasePackage) {
        deleteRoleFromMyTeamMember(releasePackage.getId(), MyChangeRoles.developmentAndEngineeringProjectLead);
        if (releasePackage.getChangeOwnerType().equals(ChangeOwnerType.PROJECT.name()))
            deleteRoleFromMyTeamMember(releasePackage.getId(), MyChangeRoles.changeOwner);
    }

    public void deleteRoleFromMyTeamMember(Long releasePackageId, MyChangeRoles myChangeRoles) {
        Long releasePackageMyTeamId = getMyTeamIdByLinkedEntity(releasePackageId);
        List<MyTeamMember> membersWithRole = getMembersByRole(myChangeRoles.getRole(), releasePackageMyTeamId);
        membersWithRole.forEach(myTeamMember -> deleteRoleFromMyTeamMember(myTeamMember, MyChangeRoles.changeOwner.getRole()));
    }

    public void addChangeSpecialist2ToMyTeam(User changeSpecialist2, Long releasePackageId) {
        self.addUserToMyTeamByParentId(MyChangeRoles.changeSpecialist2, changeSpecialist2, releasePackageId);
    }

    public boolean isChangeSpecialist2Updated(ReleasePackage releasePackage, User user) {
        Long myTeamId = getMyTeamIdByLinkedEntity(releasePackage.getId());
        List<MyTeamMember> myTeamMembers = getMyTeamMembersByRole(MyChangeRoles.changeSpecialist2.getRole(), myTeamId);
        if (myTeamMembers.isEmpty())
            return true;
        return myTeamMembers.get(0).getUser().equals(user);
    }

    @Override
    public MyTeamService getSelf() {
        return self;
    }

    @Override
    public Long getMyTeamIdByLinkedEntity(Long releasePackageId) {
        String criteria = "releasePackage.id:" + releasePackageId;
        Pageable pageable = PageRequest.of(0, 1);
        Slice<Id> idSlice = this.filterIds(criteria, pageable);
        return Objects.nonNull(idSlice.getContent()) && idSlice.getNumberOfElements() > 0 ? idSlice.getContent().get(0).getValue() : null;
    }

    @Override
    public Class getMyTeamAggregateClass() {
        return ReleasePackageMyTeamAggregate.class;
    }

    @Override
    @PublishResponse(eventType = "ADD-MYTEAM-MEMBER", eventBuilder = AggregateEventBuilder.class,
            responseClass = MyTeamPublishData.class, eventEntity = "com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage",
            destination = "com.example.mirai.projectname.releasepackageservice.myteam")
    public MyTeamMember publishAddedMyTeamMember(MyTeamMember myTeamMember) {
        return myTeamMember;
    }

    @Override
    @PublishResponse(eventType = "UPDATE-MYTEAM-MEMBER", eventBuilder = AggregateEventBuilder.class,
            responseClass = MyTeamPublishData.class, eventEntity = "com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage",
            destination = "com.example.mirai.projectname.releasepackageservice.myteam")
    public MyTeamMember publishUpdatedMyTeamMember(MyTeamMember myTeamMember) {
        return myTeamMember;
    }

    @Override
    @PublishResponse(eventType = "DELETE-MYTEAM-MEMBER", eventBuilder = AggregateEventBuilder.class,
            responseClass = MyTeamPublishData.class, eventEntity = "com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage",
            destination = "com.example.mirai.projectname.releasepackageservice.myteam")
    public MyTeamMember publishDeletedMyTeamMember(MyTeamMember myTeamMember) {
        return myTeamMember;
    }

    @Transactional
    public ReleasePackageDetail.MyTeamDetail updateMyTeamForCreatorsAndUsers(String releasePackageNumber, List<MyTeamMember> myTeamMembers) {
        Long releasePackageId = releasePackageService.getReleasePackageIdByReleasePackageNumber(releasePackageNumber);
        Long myTeamId = getMyTeamIdByLinkedEntity(releasePackageId);
        syncMyTeamForCreatorsAndUsers(myTeamMembers, releasePackageId);
        ReleasePackageMyTeamAggregate releasePackageMyTeamAggregate = (ReleasePackageMyTeamAggregate) getAggregate(myTeamId, (Class) ReleasePackageMyTeamAggregate.class);
        return new ReleasePackageDetail.MyTeamDetail(releasePackageMyTeamAggregate);
    }

    public void syncMyTeamForCreatorsAndUsers(List<MyTeamMember> changeObjectMyTeamMembers, Long releasePackageId) {
        Long releasePackageMyTeamId = getMyTeamIdByLinkedEntity(releasePackageId);
        if (Objects.isNull(releasePackageMyTeamId))
            return;

        Set<EntityLink<BaseEntityInterface>> entityLinkSet = new HashSet<>();
        entityLinkSet.add(new EntityLink(releasePackageMyTeamId, ReleasePackageMyTeam.class));

        changeObjectMyTeamMembers.stream().forEach(myTeamMember -> myTeamMember.setId(null));
        updateMyTeamMembersWithRole(releasePackageMyTeamId, changeObjectMyTeamMembers, MyChangeRoles.creator.getRole(), entityLinkSet);
        updateMyTeamMembersWithRole(releasePackageMyTeamId, changeObjectMyTeamMembers, MyChangeRoles.user.getRole(), entityLinkSet);
        Optional<MyTeamMember> changeOwner = changeObjectMyTeamMembers.stream().filter(myTeamMember -> myTeamMember.getRoles().contains(MyChangeRoles.changeOwner.getRole())).findFirst();
        if (changeOwner.isPresent()) {
            addOrUpdateMember(changeOwner.get(), entityLinkSet, MyChangeRoles.changeOwner.getRole());
            releasePackageService.updateChangeOwner(releasePackageId, changeOwner.get().getUser());
        }
    }

    public void updateMyTeamMembersWithRole(Long releasePackageMyTeamId, List<MyTeamMember> changeObjectMyTeamMembers, String role, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
        List<MyTeamMember> membersWithRoleInReleasePackageMyTeam = getMyTeamMembersByRole(role, releasePackageMyTeamId);
        List<MyTeamMember> membersWithRoleInChangeObjectMyTeam = changeObjectMyTeamMembers.stream().filter(myTeamMember -> myTeamMember.getRoles().contains(role)).collect(Collectors.toList());

        List<MyTeamMember> roleRemovedFromMembers = membersWithRoleInReleasePackageMyTeam.stream().filter(myTeamMember -> isUserExistInMyTeamWithRole(membersWithRoleInChangeObjectMyTeam, myTeamMember, role).isEmpty()).collect(Collectors.toList());
        List<MyTeamMember> roleAddedForMembers = membersWithRoleInChangeObjectMyTeam.stream().filter(myTeamMember -> isUserExistInMyTeamWithRole(membersWithRoleInReleasePackageMyTeam, myTeamMember, role).isEmpty()).collect(Collectors.toList());

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

    public void deleteRole(User user, MyChangeRoles role, Long releasePackageId) {
        Long myTeamId = getMyTeamIdByLinkedEntity(releasePackageId);
        if (Objects.nonNull(user))
            deleteRoleFromMyTeamMember(user, role, myTeamId);
    }

    public void deleteChangeSpecialist3FromMyTeam(Long releasePackageId) {
        deleteRoleFromMyTeamMember(releasePackageId, MyChangeRoles.changeSpecialist3);
    }

    public void deleteEcnExecutorFromMyTeam(Long releasePackageId) {
        deleteRoleFromMyTeamMember(releasePackageId, MyChangeRoles.ecnExecutor);
    }

    //added for migration -> without auth check
    public void addMyTeamMemberForMigration(Long MyTeamId, MyTeamMember myTeamMember) {
        HashSet<EntityLink<BaseEntityInterface>> entityLinkSet = new HashSet();
        entityLinkSet.add(new EntityLink(MyTeamId, ReleasePackageMyTeam.class));
        addMemberWithoutPublish(myTeamMember, entityLinkSet);
    }

    public void updateRoleForMembersOfReleasePackages(String[] releasePackageNumbers, User myTeamMemberUser) {
        List<Long> releasePackageIds = new ArrayList<>();
        Arrays.asList(releasePackageNumbers).stream().forEach(releasePackageNumber -> {
            Long releasePackageId = releasePackageService.getReleasePackageIdByReleasePackageNumber(releasePackageNumber);
            if (Objects.nonNull(releasePackageId)) {
                releasePackageIds.add(releasePackageId);
            }
        });
        releasePackageIds.forEach(releasePackageId -> self.addUserToMyTeamByParentId(MyChangeRoles.changeOwner, myTeamMemberUser, releasePackageId));
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
    public String getCaseObjectType() {
        return "RP";
    }

    @Override
    public void setLinkedEntityIdsByFilter(MyTeamBulkUpdate myTeamBulkUpdate) {
        Slice<BaseView> releasePackagesSlice = releasePackageService.getEntitiesFromView(myTeamBulkUpdate.getViewCriteria(), PageRequest.of(0, Integer.MAX_VALUE - 1), Optional.empty(), ReleasePackageList.class);
        if (Objects.nonNull(releasePackagesSlice) && releasePackagesSlice.getNumberOfElements() > 0) {
            List<Long> linkedEntityIds = releasePackagesSlice.getContent().stream().map(item -> ((ReleasePackageList)item).getId()).collect(Collectors.toList());
            myTeamBulkUpdate.setCaseObjectIds(linkedEntityIds);
        }
    }

    @Override
    public void setLinkedEntityNumbers(MyTeamBulkUpdate myTeamBulkUpdate) {
        if (Objects.nonNull(myTeamBulkUpdate.getCaseObjectIds())) {
            List<String> releasePackageNumbers = new ArrayList<>();
            myTeamBulkUpdate.getCaseObjectIds().forEach(releasePackageId -> {
                String releasePackageNumber = releasePackageService.getReleasePackageNumberByReleasePackageId(releasePackageId);
                if (Objects.nonNull(releasePackageNumber))
                    releasePackageNumbers.add(releasePackageNumber);
            });
            myTeamBulkUpdate.setCaseObjectNumbers(releasePackageNumbers);
        }
    }

    @Override
    public String getRoleLabel(String roleName) {
        return MyChangeRoles.getLabel(roleName);
    }

    public void deleteMyTeam(Long myTeamId, Set<ReleasePackageMyTeamMemberAggregate> members) {
        Iterator<ReleasePackageMyTeamMemberAggregate> iterator = members.iterator();
        while (iterator.hasNext()) {
            ReleasePackageMyTeamMemberAggregate memberAggregate = iterator.next();
            myTeamMemberService.delete(memberAggregate.getMember().getId());
        }
        self.delete(myTeamId);
    }

    @Override
    protected void updateEntityWithNewMember(User userToAdd, Long linkedEntityId, String role) {
        if (Objects.equals(role, MyChangeRoles.changeSpecialist3.getRole()) || Objects.equals(role, MyChangeRoles.ecnExecutor.getRole()) || Objects.equals(role, MyChangeRoles.changeOwner.getRole()))
            releasePackageService.updateMyTeamMemberFields(userToAdd, linkedEntityId, role);
    }
}


