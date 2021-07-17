package com.example.mirai.projectname.notificationservice.engine.processor.releasepackage.extractor;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import com.example.mirai.libraries.notification.engine.processor.RoleExtractorInterface;
import com.example.mirai.libraries.notification.error.model.UnableToFetchExplicitSubscribers;
import com.example.mirai.libraries.notification.settings.model.EntitySubscription;
import com.example.mirai.libraries.notification.settings.service.EntitySubscriptionService;
import com.example.mirai.projectname.notificationservice.engine.processor.releasepackage.model.MyTeamJson;
import com.example.mirai.projectname.notificationservice.engine.processor.releasepackage.model.ReleasePackageJson;
import com.example.mirai.projectname.notificationservice.engine.processor.releasepackage.role.ChangeSpecialist2Role;
import com.example.mirai.projectname.notificationservice.engine.processor.releasepackage.role.MyTeamMemberRole;
import com.example.mirai.projectname.notificationservice.engine.processor.releasepackage.role.SubscriberRole;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.*;

public abstract class ReleasePackageRoleExtractor implements RoleExtractorInterface {
    String category;
    Long entityId;
    @Override
    public Boolean isStatusChangeEvent() {
        return true;
    }

    public Set<BaseRole> getProcessors(Event event) throws IOException {
        Set<BaseRole> baseRoles = new HashSet();
        Map data = (Map) event.getData();
        String myTeamDetails = getObjectMapper().writeValueAsString(data.get("my_team_details"));
        if(Objects.nonNull(myTeamDetails) && !myTeamDetails.equals("null")) {
            MyTeamJson myTeamJson = new MyTeamJson(myTeamDetails);
            String releasePackage = getObjectMapper().writeValueAsString(data.get("release_package"));
            ReleasePackageJson releasePackageJson = new ReleasePackageJson(releasePackage);
            this.entityId = Long.valueOf(releasePackageJson.getReleasePackageId());
            //return baseRoles;
            User changeSpecialist2 = myTeamJson.getChangeSpecialist2();
            List<String> recipientUserIds = new ArrayList<>();
            if (Objects.nonNull(changeSpecialist2) && (Objects.equals(this.category, "ReleasePackageReviewCreated") || Objects.equals(this.category, "ReleasePackageReviewValidationStarted"))) {
                recipientUserIds.add(changeSpecialist2.getUserId());
                baseRoles.add(new ChangeSpecialist2Role(event, "ChangeSpecialist2", changeSpecialist2, this.category, entityId, entityId));
            } else {
                List<User> myTeamMembers = myTeamJson.getReleasePackageMyTeamMembers();
                myTeamMembers.forEach(member -> {
                    //to avoid duplicate recipients
                    if (!recipientUserIds.contains(member.getUserId())) {
                        recipientUserIds.add(member.getUserId());
                        try {
                            if (member.equals(changeSpecialist2)) {
                                baseRoles.add(new ChangeSpecialist2Role(event, "ChangeSpecialist2", member, this.category, entityId, entityId));
                            }
                            baseRoles.add(new MyTeamMemberRole(event, "MyTeamMember", member, this.category, entityId, entityId));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
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
        }else{
            return baseRoles;
        }
    }

    public List<User> getSubscribers(Long entityId, final Event event) {
        List<User> subscribedUsers = new ArrayList<>();
        try {
            EntitySubscriptionService entitySubscriptionService = (EntitySubscriptionService) ApplicationContextHolder.getBean(EntitySubscriptionService.class);
            List<EntitySubscription> entitySubscriptions = entitySubscriptionService.getSubscriptionsToEvent(entityId, "RELEASEPACKAGE");
            entitySubscriptions.stream().forEach(subscription -> {
                User user = new User();
                user.setUserId(subscription.getUserId());
                user.setEmail(subscription.getEmail());
                subscribedUsers.add(user);
            });
        } catch(Exception exception) {
            throw new UnableToFetchExplicitSubscribers(exception, "Unable to get explicit subscribers", event);
        }
        return subscribedUsers;
    }
}
