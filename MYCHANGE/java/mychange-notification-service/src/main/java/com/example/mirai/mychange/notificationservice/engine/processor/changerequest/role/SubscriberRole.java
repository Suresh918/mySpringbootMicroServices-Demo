package com.example.mirai.projectname.notificationservice.engine.processor.changerequest.role;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;

public class SubscriberRole extends ChangeRequestBaseRole {
    public SubscriberRole(Event event, String role, User user, String category, Long id, Long entityId) {
        super(event, role, category, entityId, id);
        this.recipient = user;
    }
}
