package com.example.mirai.projectname.notificationservice.engine.processor.changerequest.role;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;

public class MyTeamMemberRole extends ChangeRequestBaseRole {
    public MyTeamMemberRole(Event event, String role, User user, String category, Long id, Long entityId) {
        super(event, role, category, entityId, id);
        this.recipient = user;
    }
}
