package com.example.mirai.projectname.notificationservice.engine.processor.releasepackage.role;


import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.notificationservice.engine.processor.releasepackage.ReleasePackageRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MyTeamMemberRole extends ReleasePackageRole {

    public MyTeamMemberRole(Event event, String role, User myTeamMember, String category, Long entityId, Long id) throws JsonProcessingException {
        super(event, role, category, entityId, id);
        this.recipient =  myTeamMember;
    }

   }

