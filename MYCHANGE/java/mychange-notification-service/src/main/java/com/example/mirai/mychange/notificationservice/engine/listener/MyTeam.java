package com.example.mirai.projectname.notificationservice.engine.listener;

import com.example.mirai.libraries.notification.engine.listener.BaseListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;

@Component
@Slf4j
public class MyTeam extends BaseListener {
    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.myteam",
            selector = "entity='com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest' and type='ADD-MYTEAM-MEMBER'")
    public void processChangeRequestMyTeamMemberAdded(final Message message) throws JMSException {
        processMessage(message, "ChangeRequestMyTeamMemberAdded");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.myteam",
            selector = "entity='com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest' and type='DELETE-MYTEAM-MEMBER'")
    public void processChangeRequestMyTeamMemberRemoved(final Message message) throws JMSException {
        processMessage(message, "ChangeRequestMyTeamMemberRemoved");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.myteam",
            selector = "entity='com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage' and type='ADD-MYTEAM-MEMBER'")
    public void processReleasePackageMyTeamMemberAdded(final Message message) throws JMSException {
        processMessage(message, "ReleasePackageMyTeamMemberAdded");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.myteam",
            selector = "entity='com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage' and type='DELETE-MYTEAM-MEMBER'")
    public void processReleasePackageMyTeamMemberRemoved(final Message message) throws JMSException {
        processMessage(message, "ReleasePackageMyTeamMemberRemoved");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.myteam",
            selector = "entity='com.example.mirai.projectname.changerequestservice.myteam.model.ChangeRequestMyTeam' and type='BULK_ADD'")
    public void processChangeRequestMyTeamMembersBulkAddition(final Message message) throws JMSException {
        processMessage(message, "ChangeRequestMyTeamMemberBulkAdded");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.myteam",
            selector = "entity='com.example.mirai.projectname.changerequestservice.myteam.model.ChangeRequestMyTeam' and type='BULK_REMOVE'")
    public void processChangeRequestMyTeamMembersBulkRemoval(final Message message) throws JMSException {
        processMessage(message, "ChangeRequestMyTeamMemberBulkRemoved");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.myteam",
            selector = "entity='com.example.mirai.projectname.changerequestservice.myteam.model.ChangeRequestMyTeam' and type='BULK_REPLACE'")
    public void processChangeRequestMyTeamMembersBulkReplace(final Message message) throws JMSException {
        processMessage(message, "ChangeRequestMyTeamMemberBulkReplaced");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.myteam",
            selector = "entity='com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam' and type='BULK_ADD'")
    public void processReleasePackageMyTeamMembersBulkAddition(final Message message) throws JMSException {
        processMessage(message, "ReleasePackageMyTeamMemberBulkAdded");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.myteam",
            selector = "entity='com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam' and type='BULK_REMOVE'")
    public void processReleasePackageMyTeamMembersBulkRemoval(final Message message) throws JMSException {
        processMessage(message, "ReleasePackageMyTeamMemberBulkRemoved");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.myteam",
            selector = "entity='com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam' and type='BULK_REPLACE'")
    public void processReleasePackageMyTeamMembersBulkReplace(final Message message) throws JMSException {
        processMessage(message, "ReleasePackageMyTeamMemberBulkReplaced");
    }
}
