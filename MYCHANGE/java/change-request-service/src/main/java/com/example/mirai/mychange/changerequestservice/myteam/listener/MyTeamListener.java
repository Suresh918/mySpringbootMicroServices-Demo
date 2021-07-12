package com.example.mirai.projectname.changerequestservice.myteam.listener;

import com.example.mirai.projectname.changerequestservice.myteam.service.ChangeRequestMyTeamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyTeamListener {

    private final ChangeRequestMyTeamService changeRequestMyTeamService;
    private final ObjectMapper objectMapper;

    public MyTeamListener(ChangeRequestMyTeamService changeRequestMyTeamService, ObjectMapper objectMapper) {
        this.changeRequestMyTeamService = changeRequestMyTeamService;
        this.objectMapper = objectMapper;
    }

    /*@JmsListener(destination = "com.example.mirai.projectname.changerequestservice.usersettings.preferredroles")
    public void updateUserPreferredRoles(final Message message) throws JMSException {
        PreferredRolesMessage preferredRoles = new PreferredRolesMessage(message.getBody(String.class));
        changeRequestMyTeamService.updateMyTeamPreferredRoles(preferredRoles.getUserId(), preferredRoles.getRoles());
        message.acknowledge();
    }*/
}
