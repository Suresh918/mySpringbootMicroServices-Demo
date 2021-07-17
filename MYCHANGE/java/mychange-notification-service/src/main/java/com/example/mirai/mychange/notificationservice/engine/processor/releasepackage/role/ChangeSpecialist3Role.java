package com.example.mirai.projectname.notificationservice.engine.processor.releasepackage.role;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.notificationservice.engine.processor.releasepackage.ReleasePackageRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChangeSpecialist3Role extends ReleasePackageRole {

    public ChangeSpecialist3Role(Event event, String role, User changeSpecialist3, String category, Long entityId, Long id) throws JsonProcessingException {
        super(event, role, category, entityId, id);
        this.recipient = changeSpecialist3;
    }

   }

