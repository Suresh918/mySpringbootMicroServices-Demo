package com.example.mirai.projectname.notificationservice.engine.processor.changerequest.role;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;

public class ChangeSpecialist1Role extends ChangeRequestBaseRole {

    public ChangeSpecialist1Role(Event event, String role, User changeSpecialist1, String category, Long id, Long entityId) {
        super(event, role, category, entityId, id);
        this.recipient = changeSpecialist1;
    }
}
