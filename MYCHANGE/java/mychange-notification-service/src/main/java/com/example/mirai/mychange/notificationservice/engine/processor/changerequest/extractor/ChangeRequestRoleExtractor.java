package com.example.mirai.projectname.notificationservice.engine.processor.changerequest.extractor;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import com.example.mirai.libraries.notification.engine.processor.RoleExtractorInterface;
import com.example.mirai.libraries.notification.error.model.UnableToFetchExplicitSubscribers;
import com.example.mirai.libraries.notification.settings.model.EntitySubscription;
import com.example.mirai.libraries.notification.settings.service.EntitySubscriptionService;
import com.example.mirai.projectname.notificationservice.engine.processor.changerequest.ChangeRequestPropertyExtractorUtil;
import com.example.mirai.projectname.notificationservice.engine.processor.changerequest.role.MyTeamMemberRole;
import com.example.mirai.projectname.notificationservice.engine.processor.changerequest.role.SubscriberRole;

import java.io.IOException;
import java.util.*;

public abstract class ChangeRequestRoleExtractor implements RoleExtractorInterface {
    String category;
    Long entityId;
    @Override
    public Boolean isStatusChangeEvent() {
        return true;
    }

    public Set<BaseRole> getProcessors(Event event) throws IOException {
        Set<BaseRole> baseRoles = new HashSet();
        Map data = (Map) event.getData();
        this.entityId = Long.parseLong("" + ChangeRequestPropertyExtractorUtil.getChangeRequestIdFromChangeRequestAggregate(data));

        List myTeamMemberData = ChangeRequestPropertyExtractorUtil.getChangeRequestMyTeamMembersFromChangeRequestAggregate(data);
        List<User> myTeamMemberUsers = new ArrayList<>();
        myTeamMemberData.forEach(memberData -> {
            Map user = ChangeRequestPropertyExtractorUtil.getUserFromMember((Map) memberData);
            myTeamMemberUsers.add(getObjectMapper().convertValue(user, User.class));
        });
        List<String> recipientUserIds = new ArrayList<>();
        myTeamMemberUsers.forEach(member -> {
            //to avoid duplicate recipients
            if (!recipientUserIds.contains(member.getUserId())) {
                recipientUserIds.add(member.getUserId());
                baseRoles.add(new MyTeamMemberRole(event, "MyTeamMember", member, this.category, entityId, entityId));
            }
        });
        //add subscribers
        List<User> subscribers = getSubscribers(entityId, event);
        if (Objects.nonNull(subscribers)) {
            subscribers.forEach(subscriber -> {
                if (!recipientUserIds.contains(subscriber.getUserId())) {
                    recipientUserIds.add(subscriber.getUserId());
                    baseRoles.add(new SubscriberRole(event, "Subscriber", subscriber, this.category, entityId, entityId));
                }
            });
        }
        return baseRoles;
    }

    public List<User> getSubscribers(Long entityId, final Event event) {
        List<User> subscribedUsers = new ArrayList<>();
        try {
            EntitySubscriptionService entitySubscriptionService = (EntitySubscriptionService) ApplicationContextHolder.getBean(EntitySubscriptionService.class);
            List<EntitySubscription> entitySubscriptions = entitySubscriptionService.getSubscriptionsToEvent(entityId, "CHANGEREQUEST");
            entitySubscriptions.stream().forEach(subscription -> {
                User user = new User();
                user.setUserId(subscription.getUserId());
                user.setEmail(subscription.getEmail());
                subscribedUsers.add(user);
            });
        } catch(Exception exception) {
            //TODO segregate the exception between transport exception and other
            throw new UnableToFetchExplicitSubscribers(exception, "Unable to get explicit subscribers", event);
        }
        return subscribedUsers;
    }
}
