package com.example.mirai.libraries.myteam.service;

import com.example.mirai.libraries.audit.service.AuditServiceDefaultInterface;
import com.example.mirai.libraries.backgroundable.annotation.Backgroundable;
import com.example.mirai.libraries.core.annotation.Aggregate;
import com.example.mirai.libraries.core.annotation.AggregateRoot;
import com.example.mirai.libraries.core.annotation.SecureCaseAction;
import com.example.mirai.libraries.core.annotation.SecureLinkedEntityCaseAction;
import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.core.model.*;
import com.example.mirai.libraries.entity.model.Id;
import com.example.mirai.libraries.entity.service.EntityResolverDefaultInterface;
import com.example.mirai.libraries.entity.service.EntityServiceDefaultInterface;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.libraries.event.EventActorExtractorInterface;
import com.example.mirai.libraries.gds.service.GdsGroupService;
import com.example.mirai.libraries.gds.service.GdsUserService;
import com.example.mirai.libraries.myteam.exception.MyTeamMemberExistsException;
import com.example.mirai.libraries.myteam.model.MyTeam;
import com.example.mirai.libraries.myteam.model.MyTeamBulkOperation;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.libraries.myteam.model.aggregate.MemberAggregateInterface;
import com.example.mirai.libraries.myteam.model.dto.Member;
import com.example.mirai.libraries.myteam.model.dto.MyTeamBulkUpdate;
import com.example.mirai.libraries.myteam.model.dto.TeamDetails;
import com.example.mirai.libraries.security.abac.AbacAwareInterface;
import com.example.mirai.libraries.security.abac.component.AbacProcessor;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.example.mirai.libraries.security.core.component.EntityACL;
import com.example.mirai.libraries.security.core.component.PropertyACL;
import com.example.mirai.libraries.security.core.service.SecurityServiceDefaultInterface;
import com.example.mirai.libraries.security.rbac.RbacAwareInterface;
import com.example.mirai.libraries.security.rbac.component.RbacProcessor;
import com.example.mirai.libraries.util.ReflectionUtil;
import com.example.mirai.projectname.libraries.user.model.PreferredRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import java.util.*;
import java.util.stream.Collectors;

@CacheConfig(cacheNames = {"myTeamService"})
@Slf4j
public abstract class MyTeamService implements EntityServiceDefaultInterface, SecurityServiceDefaultInterface, AuditServiceDefaultInterface {
    private final AbacProcessor abacProcessor;

    private final RbacProcessor rbacProcessor;

    private final EntityACL acl;

    private final PropertyACL pacl;

    private final CaseActionList caseActionList;

    private final PreferredRolesService preferredRolesService;

    private final MyTeamMemberService myTeamMemberService;

    private final GdsUserService gdsUserService;

    private final GdsGroupService gdsGroupService;

    private final EntityResolverDefaultInterface entityResolverDefaultInterface;

    private final Class<? extends Roles> roles;

    private MyTeamService self;

    public JmsTemplate jmsTemplate;

    @Value("${mirai.libraries.myteam.bulk-update.topic:null}")
    public String topicToPublishBulkUpdates;

    public MyTeamService(MyTeamMemberService myTeamMemberService, PreferredRolesService preferredRolesService,
                         AbacProcessor abacProcessor, RbacProcessor rbacProcessor,
                         EntityACL acl, PropertyACL pacl, CaseActionList caseActionList, GdsUserService gdsUserService,
                         GdsGroupService gdsGroupService, EntityResolverDefaultInterface entityResolverDefaultInterface, Class<? extends Roles> roles) {
        this.myTeamMemberService = myTeamMemberService;
        this.preferredRolesService = preferredRolesService;
        this.abacProcessor = abacProcessor;
        this.rbacProcessor = rbacProcessor;
        this.acl = acl;
        this.pacl = pacl;
        this.caseActionList = caseActionList;
        this.gdsUserService = gdsUserService;
        this.gdsGroupService = gdsGroupService;
        this.entityResolverDefaultInterface = entityResolverDefaultInterface;
        this.roles = roles;
    }

    @PostConstruct
    private void postConstruct() {
        this.self = getSelf();
    }

    public abstract MyTeamService getSelf();

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

    @SecureLinkedEntityCaseAction(caseAction = "CREATE_MY_TEAM")
    public MyTeam createMyTeam(BaseEntityInterface entity, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
        return (MyTeam) EntityServiceDefaultInterface.super.createLinkedEntityWithLinks(entity, entityLinkSet);
    }

    @SecureLinkedEntityCaseAction(caseAction = "ADD_MY_TEAM_MEMBER")
    public Member addMember(MyTeamMember entity, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
        MyTeamMember addedMyTeamMember = myTeamMemberService.getMemberByUserId(entity, entityLinkSet);
        if (Objects.nonNull(addedMyTeamMember)) {
            throw new MyTeamMemberExistsException();
        }
        MyTeamMember myTeamMember = myTeamMemberService.addMember(entity, entityLinkSet);
        MyTeamService myTeamService = (MyTeamService) ApplicationContextHolder.getService(MyTeamService.class);
        myTeamService.publishAddedMyTeamMember(myTeamMember);
        return getMember(myTeamMember);
    }

    public Member addMemberWithoutPublish(MyTeamMember entity, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
        MyTeamMember addedMyTeamMember = myTeamMemberService.getMemberByUserId(entity, entityLinkSet);
        if (Objects.nonNull(addedMyTeamMember)) {
            throw new MyTeamMemberExistsException();
        }
        MyTeamMember myTeamMember = myTeamMemberService.addMember(entity, entityLinkSet);
        return getMember(myTeamMember);
    }

    @SecureLinkedEntityCaseAction(caseAction = "ADD_MY_TEAM_MEMBER")
    public Member addOrUpdateMember(MyTeamMember entity, Set<EntityLink<BaseEntityInterface>> entityLinkSet, String role) {
        MyTeamMember addedMyTeamMember = myTeamMemberService.getMemberByUserId(entity, entityLinkSet);
        List<MyTeamMember> membersWithRole = myTeamMemberService.getMembersByRole(role, entityLinkSet);
        if (addedMyTeamMember != null && !membersWithRole.isEmpty() && addedMyTeamMember.getUser().equals(membersWithRole.get(0).getUser())) {
            return getMember(membersWithRole.get(0));
        }
        // update current member with this role
        if (!membersWithRole.isEmpty() && Objects.nonNull(membersWithRole.get(0))) {
            MyTeamMember memberWithRole = membersWithRole.get(0);
            log.info("memberWithRole " + memberWithRole.getRoles().toString() + " user " + memberWithRole.getUser().getUserId());
            if (memberWithRole.getRoles().size() == 1 && memberWithRole.getRoles().contains(role)) {
                myTeamMemberService.delete(memberWithRole.getId());
                self.publishDeletedMyTeamMember(memberWithRole);
            } else if (memberWithRole.getRoles().size() > 1 && memberWithRole.getRoles().contains(role)) {
                memberWithRole.getRoles().remove(role);
                memberWithRole.setRoles(memberWithRole.getRoles().stream().distinct().collect(Collectors.toList()));
                myTeamMemberService.update(memberWithRole);
                self.publishUpdatedMyTeamMember(memberWithRole);
            }
        }
        // update the new member with role
        MyTeamMember myTeamMember;
        if (Objects.nonNull(addedMyTeamMember)) {
            List<String> roles = addedMyTeamMember.getRoles();
            if (Objects.isNull(roles)) {
                roles = new ArrayList<>();
            }
            roles.add(role);
            addedMyTeamMember.setRoles(roles.stream().distinct().collect(Collectors.toList()));
            myTeamMember = (MyTeamMember) myTeamMemberService.update(addedMyTeamMember);
            self.publishUpdatedMyTeamMember(myTeamMember);
        } else {
            List<String> roles = new ArrayList<>();
            roles.add(role);
            entity.setRoles(roles);
            myTeamMember = myTeamMemberService.addMember(entity, entityLinkSet);
            MyTeamService myTeamService = (MyTeamService) ApplicationContextHolder.getService(MyTeamService.class);
            myTeamService.publishAddedMyTeamMember(myTeamMember);
        }
        return getMember(myTeamMember);
    }

    public void deleteRoleFromMyTeamMember(User user, Roles role, Long myTeamId) {
        deleteRoleFromMyTeamMember(user, role.getRole(), myTeamId);
    }

    public void deleteRoleFromMyTeamMember(User user, String role, Long myTeamId) {
        BaseEntityList<MyTeamMember> baseEntityList = myTeamMemberService.filter("myteam.id:" + myTeamId + " and user.userId:" + user.getUserId(), PageRequest.of(0, 1));
        baseEntityList.getResults().forEach(item -> {
            deleteRoleFromMyTeamMember(item, role);
        });
    }

    public void deleteRoleFromMyTeamMember(MyTeamMember myTeamMember, String role) {
        if (myTeamMember.getRoles().size() == 1 && myTeamMember.getRoles().contains(role)) {
            myTeamMemberService.delete(myTeamMember.getId());
            self.publishDeletedMyTeamMember(myTeamMember);
        } else {
            List<String> roles = myTeamMember.getRoles();
            roles.remove(role);
            myTeamMember.setRoles(roles);
            myTeamMemberService.update(myTeamMember);
            self.publishUpdatedMyTeamMember(myTeamMember);
        }
    }

    public void removeRole(Set<MemberAggregateInterface> memberAggregates, Roles role) {
        Optional<MemberAggregateInterface> myTeamMember = memberAggregates.stream().filter(memberAggregate -> memberAggregate.getMember().getRoles().contains(role.getRole())).findFirst();
        if (myTeamMember.isPresent()) {
            List<String> roles = myTeamMember.get().getMember().getRoles();
            if (roles.size() == 1 && roles.get(0).equals(role.getRole())) {
                memberAggregates.remove(myTeamMember.get());
            } else if (roles.size() > 1) {
                roles.remove(role.getRole());
                myTeamMember.get().getMember().setRoles(roles);
            }
        }
    }

    protected abstract MyTeamMember publishAddedMyTeamMember(MyTeamMember myTeamMember);

    protected abstract MyTeamMember publishUpdatedMyTeamMember(MyTeamMember myTeamMember);

    protected abstract MyTeamMember publishDeletedMyTeamMember(MyTeamMember myTeamMember);

    @Cacheable(key = "\"getMyTeamIdByLinkedEntity-\" + #root.args[0]", condition = "@entityConfigSpringCacheConfiguration !=null &&  !(@entityConfigSpringCacheConfiguration.getType().equalsIgnoreCase(\"NONE\"))")
    public abstract Long getMyTeamIdByLinkedEntity(Long linkedEntityId);

    public abstract Class getMyTeamAggregateClass();

    public Member getMember(MyTeamMember myTeamMember) {
        Member member = new Member(myTeamMember);
        setPreferredRolesOfUser(member);
        return member;
    }

    public void deleteMember(Long memberId, Class linkedEntityClass) {
        MyTeamMember myTeamMember = (MyTeamMember) myTeamMemberService.getEntityById(memberId);
        Set<EntityLink<BaseEntityInterface>> entityLinkSet = new HashSet<>();
        entityLinkSet.add(new EntityLink(myTeamMember.getMyteam().getId(), linkedEntityClass));
        MyTeamService myTeamService = (MyTeamService) ApplicationContextHolder.getService(MyTeamService.class);
        self.deleteMember(myTeamMember, entityLinkSet);
    }

    @SecureLinkedEntityCaseAction(caseAction = "REMOVE_MY_TEAM_MEMBER")
    public void deleteMember(BaseEntityInterface member, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
        //entityLinkSet is being used by annotation SecureLinkedEntityCaseAction
        myTeamMemberService.delete(member.getId());
        self.publishDeletedMyTeamMember((MyTeamMember) member);
    }

    public MyTeamMember getMyTeamMember(Long id) {
        return (MyTeamMember) myTeamMemberService.getEntityById(id);
    }

    @Override
    @SecureCaseAction("READ")
    public BaseEntityInterface get(Long id) {
        return EntityServiceDefaultInterface.super.get(id);
    }

    public TeamDetails getTeamDetails(Long linkedEntityId, List<String> groups) {
        Class myTeamAggregateClass = getMyTeamAggregateClass();
        AggregateInterface myTeamAggregate = getAggregateByParent(linkedEntityId, myTeamAggregateClass);
        TeamDetails teamDetails = new TeamDetails();

        String memberAggregateSetFieldName = ReflectionUtil.getSoleFieldNameWithAnnotation(myTeamAggregate.getClass(), Aggregate.class);
        Set<MemberAggregateInterface> memberAggregateSet = (Set<MemberAggregateInterface>) ReflectionUtil.getFieldValue(myTeamAggregate, memberAggregateSetFieldName);
        if (Objects.isNull(memberAggregateSet)) {
            memberAggregateSet = new HashSet<>();
        }
        List<Member> myTeamMembers = new ArrayList<>();
        for (MemberAggregateInterface memberAggregate : memberAggregateSet) {
            myTeamMembers.add(new Member(memberAggregate.getMember()));
        }
        setPreferredRolesOfUsers(myTeamMembers);

        List<Member> groupsMembers = new ArrayList<>();
        if (!groups.isEmpty()) {
            List<Group> groupDetails = gdsGroupService.getGroupsByGroupIds(groups);
            groupsMembers = getGroupsMembers(groupDetails);
        }
        setPreferredRolesOfUsers(groupsMembers);

        for (Member groupMember : groupsMembers) {
            boolean addedToMyTeam = memberAggregateSet.stream().anyMatch(memberAggregate ->
                    memberAggregate.getMember().getUser().getUserId().equals(groupMember.getUser().getUserId()));
            groupMember.setAddedToMyTeam(addedToMyTeam);
        }
        List<Member> totalMembers = new ArrayList<>();
        List<Member> finalGroupsMembers = groupsMembers;
        myTeamMembers.forEach(myTeamMember -> {
            Optional<Member> matchedGroupMember = finalGroupsMembers.stream().filter(groupMember -> myTeamMember.getUser().getUserId().equals(groupMember.getUser().getUserId())).findFirst();
            if (matchedGroupMember.isPresent()) {
                myTeamMember.setGroups(matchedGroupMember.get().getGroups());
                myTeamMember.setOtherRoles(matchedGroupMember.get().getOtherRoles());
                myTeamMember.setPreferredRoles(matchedGroupMember.get().getPreferredRoles());
                finalGroupsMembers.remove(matchedGroupMember.get());
            }
        });
        totalMembers.addAll(myTeamMembers);
        totalMembers.addAll(groupsMembers);
        totalMembers.sort(Comparator.comparing(member -> member.getUser().getFullName()));
        teamDetails.setAllMembers(totalMembers);
        List<String> fieldNames = ReflectionUtil.getFieldNamesWithAnnotation(myTeamAggregate.getClass(), AggregateRoot.class);
        MyTeam myTeam = (MyTeam) ReflectionUtil.getFieldValue(myTeamAggregate, fieldNames.get(0));
        teamDetails.setMyTeamId(myTeam.getId());
        return teamDetails;
    }

    public void setPreferredRolesOfUser(Member member) {
        List<Member> members = new ArrayList<>();
        members.add(member);
        setPreferredRolesOfUsers(members);
    }

    private List<String> getAllRoles() {
        return Arrays.stream(roles.getEnumConstants()).map(constant -> constant.getRole()).collect(Collectors.toList());
    }

    public void setPreferredRolesOfUsers(List<Member> members) {
        if (members.isEmpty()) {
            return;
        }
        List<String> userIds = members.stream().map(groupMember -> groupMember.getUser().getUserId()).collect(Collectors.toList());
        List<PreferredRole> userRoles = preferredRolesService.getPreferredRolesByUserIds(userIds);
        List<String> allRoles = getAllRoles();
        members.stream().forEach(groupMember -> {
            Optional<PreferredRole> matchedUser = userRoles.stream().filter(user -> user.getUserId().equals(groupMember.getUser().getUserId())).findFirst();
            if (matchedUser.isPresent()) {
                System.out.println("user " + matchedUser.get().getUserId() + "preferred roles " + matchedUser.get().getPreferredRoles());
                groupMember.setPreferredRoles(Arrays.asList(matchedUser.get().getPreferredRoles()));
                List<String> otherRoles = allRoles.stream()
                        .filter(element -> !Arrays.asList(matchedUser.get().getPreferredRoles()).contains(element))
                        .collect(Collectors.toList());
                groupMember.setOtherRoles(otherRoles);
            } else {
                groupMember.setOtherRoles(allRoles);
            }
        });
    }

    public List<Member> getGroupsMembers(List<Group> groupDetails) {
        List<Member> members = new ArrayList<>();
        groupDetails.forEach(group -> {
            if (Objects.nonNull(group.getMembers())) {
                group.getMembers().forEach(groupMember -> {
                    updateGroupMembers(members, groupMember, group.getGroupId());
                });
            }
        });
        return members;
    }

    private void updateGroupMembers(List<Member> members, User groupMember, String groupName) {
        Optional<Member> addedMember = isMemberAdded(members, groupMember);
        if (addedMember.isPresent()) {
            Member matchedMember = addedMember.get();
            List<String> memberGroups = matchedMember.getGroups();
            if (Objects.isNull(memberGroups)) {
                memberGroups = new ArrayList<>();
            }
            memberGroups.add(groupName);
            matchedMember.setGroups(memberGroups);
        } else {
            List<String> groupsOfMember = new ArrayList<>();
            groupsOfMember.add(groupName);
            Member member = new Member(groupMember, groupName);
            members.add(member);
        }
    }

    private Optional<Member> isMemberAdded(List<Member> members, User groupMember) {
        return members.stream().filter(member -> member.getUser().getUserId().equals(groupMember.getUserId())).findFirst();
    }

    public List<MyTeamMember> getMyTeamMembersByRole(String role, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
        return myTeamMemberService.getMembersByRole(role, entityLinkSet);
    }

    public List<MyTeamMember> getMyTeamMembersByRole(String role, Long myTeamId) {
        return myTeamMemberService.getMembersByRole(role, myTeamId);
    }


    public MyTeamMember getMyTeamMemberByRole(String role, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
        List<MyTeamMember> myTeamMembers = myTeamMemberService.getMembersByRole(role, entityLinkSet);
        return (myTeamMembers.isEmpty()) ? null : myTeamMembers.get(0);
    }

    public List<MyTeamMember> getAllMembersOfMyTeam(Long linkedEntityId) {
        Long myTeamId = getMyTeamIdByLinkedEntity(linkedEntityId);
        return myTeamMemberService.getAllMembersOfMyTeam(myTeamId);
    }

    public MyTeamMember updateMyTeamMember(MyTeamMember myTeamMember) {
        MyTeamMember updatedMyTeamMember = (MyTeamMember) myTeamMemberService.update(myTeamMember);
        self.publishUpdatedMyTeamMember(updatedMyTeamMember);
        return updatedMyTeamMember;
    }

    public Slice<Id> getMyTeamMemberIds(String criteria, Pageable pageable) {
        return myTeamMemberService.filterIds(criteria, pageable);
    }

    @Override
    public AggregateInterface performCaseActionAndGetCaseStatusAggregate(Long aLong, String s, Class<AggregateInterface> aClass) {
        return null;
    }

    @Override
    public CaseStatus performCaseActionAndGetCaseStatus(Long aLong, String s) {
        return null;
    }

    @Override
    public BaseEntityInterface performCaseAction(Long aLong, String s) {
        return null;
    }

    public MyTeamMember prepareMyTeamMember(Roles role, User user) {
        MyTeamMember myTeamMember = new MyTeamMember();
        myTeamMember.setUser(user);
        List<String> roles = new ArrayList<>();
        roles.add(role.getRole());
        myTeamMember.setRoles(roles);
        return myTeamMember;
    }

    public MyTeamMember prepareMyTeamMember(String role, User user) {
        MyTeamMember myTeamMember = new MyTeamMember();
        myTeamMember.setUser(user);
        List<String> roles = new ArrayList<>();
        roles.add(role);
        myTeamMember.setRoles(roles);
        return myTeamMember;
    }

    public void addOrUpdateMemberWithSharedRole(MyTeamMember myTeamMember, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
        MyTeamMember existingMyTeamMember = myTeamMemberService.getMemberByUserId(myTeamMember.getUser().getUserId(), entityLinkSet);
        if (Objects.nonNull(existingMyTeamMember)) {
            List<String> roles = existingMyTeamMember.getRoles();
            roles.addAll(myTeamMember.getRoles());
            existingMyTeamMember.setRoles(roles.stream().distinct().collect(Collectors.toList()));
            myTeamMemberService.update(existingMyTeamMember);
            self.publishUpdatedMyTeamMember(existingMyTeamMember);
        } else {
            myTeamMemberService.addMember(myTeamMember, entityLinkSet);
        }
    }

    public void addRoleToMyTeamMember(MyTeamMember member, Roles role) {
        List<String> roles = member.getRoles();
        roles.add(role.getRole());
        member.setRoles(roles);
    }

    public List<Member> getUserRoleDetails(String[] userIds) {
        List<String> userIdsList = Arrays.asList(userIds);
        List<Member> memberList = new ArrayList<>();
        userIdsList.stream().forEach(userId -> {
            Member member = new Member();
            User user = gdsUserService.getUserByUserId(userId);
            member.setUser(user);
            PreferredRole preferredRoles = this.preferredRolesService.getPreferredRolesByUserId(userId);
            List preferredRolesOfUser = new ArrayList();
            if (Objects.nonNull(preferredRoles) && Objects.nonNull(preferredRoles.getPreferredRoles())) {
                preferredRolesOfUser = Arrays.asList(preferredRoles.getPreferredRoles());
                List<String> allRoles = getAllRoles();
                List<String> otherRoles = allRoles.stream()
                        .filter(element -> !Arrays.asList(preferredRoles.getPreferredRoles()).contains(element))
                        .collect(Collectors.toList());
                member.setOtherRoles(otherRoles);
            } else {
                member.setOtherRoles(getAllRoles());
            }
            member.setPreferredRoles(preferredRolesOfUser);
            memberList.add(member);
        });

        return memberList;
    }

    @Transactional
    public void deleteMyTeamMember(String parentType, String entityType, Long id) {
        MyTeamMember myTeamMember = self.getMyTeamMember(id);
        self.delete(myTeamMember, parentType, entityType);
    }

    public void delete(MyTeamMember myTeamMember, String parentType, String entityType) {
        /**
         * Used method name "delete" with argument as myTeam member entity, to handle publishing data when delete member
         * first argument has to be entityId or base entity instance
         */
        Class<? extends BaseEntityInterface> parentEntityClass = entityResolverDefaultInterface.getEntityClass(parentType, entityType);
        self.deleteMember(myTeamMember.getId(), parentEntityClass);
    }

    public void addUserToMyTeamByParentId(Roles role, User user, Long parentId) {
        Long myTeamId = getMyTeamIdByLinkedEntity(parentId);
        addUserToMyTeamByMyTeamId(role, user, myTeamId);
    }

    public Member addUserToMyTeamByMyTeamId(Roles role, User user, Long myTeamId) {
        MyTeamMember myTeamMember = new MyTeamMember();
        myTeamMember.setUser(user);
        List<String> roles = new ArrayList<>();
        roles.add(role.getRole());
        myTeamMember.setRoles(roles);

        Set<EntityLink<BaseEntityInterface>> entityLinkSet = new HashSet<>();
        entityLinkSet.add(new EntityLink(myTeamId, this.getEntityClass()));
        /**
         * Calling by this reference as some users are authorized to update parent entity but not myTeam
         * so bypassing myTeam case action check
         */
        return addOrUpdateMember(myTeamMember, entityLinkSet, role.getRole());
    }

    public AggregateInterface getAggregateByParent(long parentId, Class<AggregateInterface> aggregateInterfaceClass) {
        Long myTeamId = getMyTeamIdByLinkedEntity(parentId);
        return EntityServiceDefaultInterface.super.getAggregate(myTeamId, aggregateInterfaceClass);
    }

    public List<MyTeamMember> getMembersByRole(String role, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
        return this.myTeamMemberService.getMembersByRole(role, entityLinkSet);
    }

    public List<MyTeamMember> getMembersByRole(String role, Long myTeamId) {
        return this.myTeamMemberService.getMembersByRole(role, myTeamId);
    }

    public MyTeamMember mergeMyTeamMember(BaseEntityInterface newIns, BaseEntityInterface oldIns, List<String> oldInsChangedAttributeNames, List<String> newInsChangedAttributeNames) {
        return myTeamMemberService.merge(newIns, oldIns, oldInsChangedAttributeNames, newInsChangedAttributeNames);
    }

    // Bulk Updates
    public void bulkUpdateMyTeamMembers(MyTeamBulkUpdate myTeamBulkUpdate, String myTeamBulkOperation, Boolean isAllSelected) {
        if (isAllSelected) {
            if ((Objects.isNull(myTeamBulkUpdate.getCriteria()) || myTeamBulkUpdate.getCriteria().length() == 0) &&
                    (Objects.isNull(myTeamBulkUpdate.getViewCriteria()) || myTeamBulkUpdate.getViewCriteria().length() == 0))
                throw new InternalAssertionException("filter value cannot be null or empty");
            setLinkedEntityIdsByFilter(myTeamBulkUpdate);
        }
        if (myTeamBulkUpdate.getCaseObjectIds().isEmpty())
            return;
        setLinkedEntityNumbers(myTeamBulkUpdate);
        MyTeamBulkOperation operation = MyTeamBulkOperation.valueOf(myTeamBulkOperation.toUpperCase());
        switch (operation) {
            case BULK_ADD:
                if (getUnupdateableRoles().contains(myTeamBulkUpdate.getRole()))
                    throw new InternalAssertionException("Cannot add myteam member with the role " + myTeamBulkUpdate.getRole());
                self.handleMyTeamBulkAdd(myTeamBulkUpdate, getBackgroundJobTitle(myTeamBulkUpdate, "ADD"));
                return;
            case BULK_REMOVE:
                if (getUnupdateableRoles().contains(myTeamBulkUpdate.getRole()))
                    throw new InternalAssertionException("Cannot remove myteam member with the role " + myTeamBulkUpdate.getRole());
                self.handleMyTeamBulkRemove(myTeamBulkUpdate, getBackgroundJobTitle(myTeamBulkUpdate, "REMOVE"));
                return;
            case BULK_REPLACE:
                /*if (!getUnupdateableRoles().contains(myTeamBulkUpdate.getRole()))
                    throw new InternalAssertionException("Cannot replace myteam member with the role " + myTeamBulkUpdate.getRole());*/
                self.handleMyTeamBulkReplace(myTeamBulkUpdate, getBackgroundJobTitle(myTeamBulkUpdate, "REPLACE"));
                return;
            default:
                throw new InternalAssertionException("Invalid Myteam bulk operation");
        }

    }

    public String getCaseObjectType() {
        return "Object";
    }

    protected String getBackgroundJobTitle(MyTeamBulkUpdate myTeamBulkUpdate, String operation) {
        User userToAdd = myTeamBulkUpdate.getUserToAdd();
        User userToRemove = myTeamBulkUpdate.getUserToRemove();
        String title = "<STATUS> ";
        if (operation.equals("ADD")) {
            title += userToAdd.getFullName() + " (" + userToAdd.getAbbreviation() + ") in ";
        } else if (operation.equals("REMOVE")) {
            title += userToRemove.getFullName() + " (" + userToRemove.getAbbreviation() + ") from ";
        } else if (operation.equals("REPLACE")) {
            title += userToRemove.getFullName() + " (" + userToRemove.getAbbreviation() + ")" + " with " +userToAdd.getFullName() + " (" + userToAdd.getAbbreviation() + ") with ";
        }
        title += myTeamBulkUpdate.getCaseObjectIds().size() + " " + getCaseObjectType();
        title += myTeamBulkUpdate.getCaseObjectIds().size() > 1 ? "s" :"";
        return title;
    }

    protected void setLinkedEntityIdsByFilter(MyTeamBulkUpdate myTeamBulkUpdate) {
        myTeamBulkUpdate.setCaseObjectNumbers(new ArrayList<>());
    }

    protected void setLinkedEntityNumbers(MyTeamBulkUpdate myTeamBulkUpdate) {
        myTeamBulkUpdate.setCaseObjectNumbers(new ArrayList<>());
    }
    public List<String> getUnupdateableRoles() {
        return new ArrayList<>();
    }

    public String getRoleLabel(String role) {
        return role;
    }

    @Backgroundable(name = "MYTEAM-BULK-REPLACE", idGenerator = "#myTeamBulkUpdate.getGeneratedKey()", timeout = 5,
            parentIdExtractor = "#myTeamBulkUpdate.getRole()", parentName = "MYTEAM", title="#bulkReplaceTitle")
    @Transactional
    public void handleMyTeamBulkReplace(MyTeamBulkUpdate myTeamBulkUpdate, String bulkReplaceTitle) {
        try {
            handleBulkReplaceMyTeamMembers(myTeamBulkUpdate);
            if (!myTeamBulkUpdate.getCaseObjectIds().isEmpty()) {
                publishMyTeamBulkUpdate(myTeamBulkUpdate, "BULK_REPLACE", "SUCCESS");
            }
        } catch(Exception e) {
            log.info("Error while doing bulk replace");
        }
    }

    @Backgroundable(name = "MYTEAM-BULK-REMOVE", idGenerator = "#myTeamBulkUpdate.getGeneratedKey()", timeout = 5,
            parentIdExtractor = "#myTeamBulkUpdate.getRole()", parentName = "MYTEAM", title="#bulkRemoveTitle")
    @Transactional
    public void handleMyTeamBulkRemove(MyTeamBulkUpdate myTeamBulkUpdate, String bulkRemoveTitle) {
        try {
            handleBulkRemoveMyTeamMembers(myTeamBulkUpdate);
            publishMyTeamBulkUpdate(myTeamBulkUpdate, "BULK_REMOVE", "SUCCESS");
        } catch(Exception e) {
            log.info("Error while doing bulk remove");
        }
    }

    @Backgroundable(name = "MYTEAM-BULK-ADD", idGenerator = "#myTeamBulkUpdate.getGeneratedKey()", timeout = 5,
            parentIdExtractor = "#myTeamBulkUpdate.getRole()", parentName = "MYTEAM", title="#bulkAddTitle")
    @Transactional
    public void handleMyTeamBulkAdd(MyTeamBulkUpdate myTeamBulkUpdate, String bulkAddTitle) {
        try {
            handleBulkAddMyTeamMembers(myTeamBulkUpdate);
            publishMyTeamBulkUpdate(myTeamBulkUpdate, "BULK_ADD", "SUCCESS");
        } catch(Exception e) {
            log.info("Error while doing bulk add");
        }
    }

    public void addSharedRoleAndUpdateMyTeamMember(User user, String role, Long myTeamId) {
        MyTeamMember myTeamMember = new MyTeamMember();
        myTeamMember.setUser(user);
        List<String> roles = new ArrayList(Arrays.asList(role));
        myTeamMember.setRoles(roles);
        Set<EntityLink<BaseEntityInterface>> entityLinkSet = new HashSet<>();
        entityLinkSet.add(new EntityLink(myTeamId, this.getEntityClass()));
        addOrUpdateMemberWithSharedRoleWithoutPublish(myTeamMember, entityLinkSet);
    }

    public void addOrUpdateMemberWithSharedRoleWithoutPublish(MyTeamMember myTeamMember, Set<EntityLink<BaseEntityInterface>> entityLinkSet) {
        MyTeamMember existingMyTeamMember = myTeamMemberService.getMemberByUserId(myTeamMember.getUser().getUserId(), entityLinkSet);
        if (Objects.nonNull(existingMyTeamMember)) {
            List<String> roles = existingMyTeamMember.getRoles();
            roles.addAll(myTeamMember.getRoles());
            existingMyTeamMember.setRoles(roles.stream().distinct().collect(Collectors.toList()));
            myTeamMemberService.update(existingMyTeamMember);
        } else {
            myTeamMemberService.addMember(myTeamMember, entityLinkSet);
        }
    }

    public Boolean replaceMyTeamMembersWithRole(Long linkedEntityId, MyTeamBulkUpdate myTeamBulkUpdate) {
        Long myTeamId = getMyTeamIdByLinkedEntity(linkedEntityId);
        User userToAdd = myTeamBulkUpdate.getUserToAdd();
        User userToRemove = myTeamBulkUpdate.getUserToRemove();
        List<MyTeamMember> membersWithRole = getMyTeamMembersByRole(myTeamBulkUpdate.getRole(), myTeamId);
        if (membersWithRole.isEmpty() || !membersWithRole.get(0).getUser().equals(userToRemove)) {
            return false;
        }
        membersWithRole.forEach(myTeamMember -> {
            deleteRoleFromMyTeamMemberWithoutPublish(myTeamMember, myTeamBulkUpdate.getRole());
        });
        BaseEntityList<MyTeamMember> membersWithAddUserId = myTeamMemberService.filter("myteam.id:" + myTeamId + " and user.userId:" + userToAdd.getUserId(), PageRequest.of(0, 1));
        if (!membersWithAddUserId.getResults().isEmpty()) {
            MyTeamMember myTeamMemberToReplaceWith = membersWithAddUserId.getResults().get(0);
            List<String> roles = myTeamMemberToReplaceWith.getRoles();
            roles.add(myTeamBulkUpdate.getRole());
            myTeamMemberToReplaceWith.setRoles(roles.stream().distinct().collect(Collectors.toList()));
            myTeamMemberService.update(myTeamMemberToReplaceWith);
        } else {
            MyTeamMember myTeamMemberToReplaceWith = prepareMyTeamMember(myTeamBulkUpdate.getRole(), userToAdd);
            Set<EntityLink<BaseEntityInterface>> entityLinkSet = new HashSet<>();
            entityLinkSet.add(new EntityLink(myTeamId, this.getEntityClass()));
            myTeamMemberService.addMember(myTeamMemberToReplaceWith, entityLinkSet);
        }
        updateEntityWithNewMember(userToAdd, linkedEntityId, myTeamBulkUpdate.getRole());
        return true;
    }

    // this method will be overridden in consuming class if needed
    protected void updateEntityWithNewMember(User userToAdd, Long linkedEntityId, String role) {
        return;
    }


    public void deleteRoleFromMyTeamMembersWithoutPublish(User user, String role, Long myTeamId) {
        BaseEntityList<MyTeamMember> baseEntityList = myTeamMemberService.filter("myteam.id:" + myTeamId + " and user.userId:" + user.getUserId(), PageRequest.of(0, 1));
        baseEntityList.getResults().forEach(item -> {
            deleteRoleFromMyTeamMemberWithoutPublish(item, role);
        });
    }


    public void deleteRoleFromMyTeamMemberWithoutPublish(MyTeamMember myTeamMember, String role) {
        if (myTeamMember.getRoles().size() == 1 && myTeamMember.getRoles().contains(role)) {
            myTeamMemberService.delete(myTeamMember.getId());
        } else {
            List<String> roles = myTeamMember.getRoles();
            roles.remove(role);
            myTeamMember.setRoles(roles);
            myTeamMemberService.update(myTeamMember);
        }
    }

    public void handleBulkReplaceMyTeamMembers(MyTeamBulkUpdate myTeamBulkUpdate) {
        List<Long> linkedEntityIds = myTeamBulkUpdate.getCaseObjectIds();
        List<Long> validLinkedEntityIds = new ArrayList();
        linkedEntityIds.stream().forEach(linkedEntityId -> {
            Boolean isReplacePossible = replaceMyTeamMembersWithRole(linkedEntityId, myTeamBulkUpdate);
            if (isReplacePossible)
                validLinkedEntityIds.add(linkedEntityId);
        });
        myTeamBulkUpdate.setCaseObjectIds(validLinkedEntityIds);
    }

    public void handleBulkRemoveMyTeamMembers(MyTeamBulkUpdate myTeamBulkUpdate) {
        List<Long> linkedEntityIds = myTeamBulkUpdate.getCaseObjectIds();
        linkedEntityIds.stream().forEach(linkedEntityId -> {
            Long myTeamId = getMyTeamIdByLinkedEntity(linkedEntityId);
            User userToRemove = myTeamBulkUpdate.getUserToRemove();
            deleteRoleFromMyTeamMembersWithoutPublish(userToRemove, myTeamBulkUpdate.getRole(), myTeamId);
            updateEntityWithNewMember(null, linkedEntityId, myTeamBulkUpdate.getRole());
        });
    }

    public void handleBulkAddMyTeamMembers(MyTeamBulkUpdate myTeamBulkUpdate) {
        List<Long> linkedEntityIds = myTeamBulkUpdate.getCaseObjectIds();
        linkedEntityIds.stream().forEach(linkedEntityId -> {
            Long myTeamId = getMyTeamIdByLinkedEntity(linkedEntityId);
            User userToAdd = myTeamBulkUpdate.getUserToAdd();
            if (getNonSharedRoles().contains(myTeamBulkUpdate.getRole())) {
                List<MyTeamMember> myTeamMembers = getMyTeamMembersByRole(myTeamBulkUpdate.getRole(), myTeamId);
                if (myTeamMembers.isEmpty()) {
                    MyTeamMember myTeamMember = prepareMyTeamMember(myTeamBulkUpdate.getRole(), userToAdd);
                    Set<EntityLink<BaseEntityInterface>> entityLinkSet = new HashSet<>();
                    entityLinkSet.add(new EntityLink(myTeamId, this.getEntityClass()));
                    myTeamMemberService.addMember(myTeamMember, entityLinkSet);
                    updateEntityWithNewMember(userToAdd, linkedEntityId, myTeamBulkUpdate.getRole());
                }
            } else {
                addSharedRoleAndUpdateMyTeamMember(userToAdd, myTeamBulkUpdate.getRole(), myTeamId);
            }
        });
    }

    public List<String> getNonSharedRoles() {
        return new ArrayList<>();
    }

    public void publishMyTeamBulkUpdate(MyTeamBulkUpdate myTeamBulkUpdate, String eventType, String status) {
        if (Objects.isNull(topicToPublishBulkUpdates))
            return;
        myTeamBulkUpdate.setRole(getRoleLabel(myTeamBulkUpdate.getRole()));
        User actor = null;
        EventActorExtractorInterface eventActorExtractor = ApplicationContextHolder.getApplicationContext().getBean(EventActorExtractorInterface.class);
        if (Objects.nonNull(eventActorExtractor))
            actor = eventActorExtractor.getEventActor();
        Event event = new Event(eventType, status, getEntityClass().getName(),
                "com.example.mirai.libraries.myteam.model.dto.MyTeamBulkUpdate", actor, myTeamBulkUpdate, null,
                System.currentTimeMillis());
        createAndSendMessage(event);
    }

    public void createAndSendMessage(Event event) {
        String messageAsString = null;
        try {
            messageAsString = ObjectMapperUtil.getObjectMapper().writeValueAsString(event);
        } catch (JsonProcessingException jspe) {
            throw new RuntimeException("Unable to send message for myteam bulk update");
        }
        if (Objects.isNull(jmsTemplate))
            return;
        //inject jms template in consumer class, for this to work
        jmsTemplate.convertAndSend(topicToPublishBulkUpdates,
                messageAsString, new MessagePostProcessor() {
                    @Override
                    public javax.jms.Message
                    postProcessMessage(javax.jms.Message message) throws JMSException {
                        message.setStringProperty("type", event.getType());
                        message.setStringProperty("status", event.getStatus());
                        message.setStringProperty("entity", getEntityClass().getName());
                        message.setStringProperty("payload", event.getPayload());
                        message.setLongProperty("timestamp", System.currentTimeMillis());
                        message.setBooleanProperty("JMS_TIBCO_PRESERVE_UNDELIVERED", true);
                        log.info("My team bulk update message " + message);
                        return message;
                    }
                });
    }


    @EventListener(
            condition = "(#event.type == 'MYTEAM-BULK-REPLACE' || #event.type == 'MYTEAM-BULK-ADD' || #event.type == 'MYTEAM-BULK-REMOVE')"
    )
    public void handleBulkUpdateError(Event event) {
        publishMyTeamBulkUpdate((MyTeamBulkUpdate) event.getData(), event.getType(), "ERROR");

    }
}

