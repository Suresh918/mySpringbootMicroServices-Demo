package com.example.mirai.projectname.notificationservice.engine.listener;

import com.example.mirai.libraries.notification.engine.listener.BaseListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;

@Component
@Slf4j
public class ChangeRequest extends BaseListener {

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.changerequest",
            selector = "entity='com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest' and type in ('SUBMIT','RESUBMIT')")
    public void processChangeRequestSubmitted(final Message message) throws JMSException {
        processMessage(message, "ChangeRequestSubmitted");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.changerequest",
            selector = "entity='com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest' and type in ('DEFINE_SOLUTION','REDEFINE_SOLUTION')")
    public void processChangeRequestSolutionDefined(final Message message) throws JMSException {
        processMessage(message, "ChangeRequestSolutionDefined");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.changerequest",
            selector = "entity='com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest' and type in ('ANALYZE_IMPACT','REANALYZE_IMPACT')")
    public void processChangeRequestImpactAnalyzed(final Message message) throws JMSException {
        processMessage(message, "ChangeRequestImpactAnalyzed");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.changerequest",
            selector = "entity='com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest' and type='APPROVE'")
    public void processChangeRequestApproved(final Message message) throws JMSException {
        processMessage(message, "ChangeRequestApproved");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.changerequest",
            selector = "entity='com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest' and type='REJECT'")
    public void processChangeRequestRejected(final Message message) throws JMSException {
        processMessage(message, "ChangeRequestRejected");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.changerequest",
            selector = "entity='com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest' and type='OBSOLETE'")
    public void processChangeRequestObsoleted(final Message message) throws JMSException {
        processMessage(message, "ChangeRequestObsoleted");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.changerequest",
            selector = "entity='com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest' and type='CLOSE'")
    public void processChangeRequestClosed(final Message message) throws JMSException {
        processMessage(message, "ChangeRequestClosed");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.changerequest",
            selector = "entity='com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest' and type='REDRAFT'")
    public void processChangeRequestRedrafted(final Message message) throws JMSException {
        processMessage(message, "ChangeRequestRedrafted");
    }
}
