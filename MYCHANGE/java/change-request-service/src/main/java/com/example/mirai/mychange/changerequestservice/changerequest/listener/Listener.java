package com.example.mirai.projectname.changerequestservice.changerequest.listener;

import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestSynchronizationService;
import com.example.mirai.projectname.libraries.bpm.BwEvent;
import com.example.mirai.projectname.libraries.model.ChangeObject;
import com.example.mirai.projectname.libraries.model.ReleasePackage;
import com.example.mirai.projectname.libraries.model.Scia;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;

@Component
@Slf4j
public class Listener {

    private ChangeRequestSynchronizationService changeRequestSynchronizationService;

    public Listener(ChangeRequestSynchronizationService changeRequestSynchronizationService) {
        this.changeRequestSynchronizationService = changeRequestSynchronizationService;
    }

    @JmsListener(destination = "com.example.mirai.projectname.changerequestservice.bpmevent.changenotice")
    public void processChangeNoticeUpdated(final Message message) throws JMSException {
        com.example.mirai.projectname.libraries.bpm.BpmEvent bpmEvent = convertMessageToBpmEvent(message);
        changeRequestSynchronizationService.updateChangeNoticeStatus(bpmEvent);
        message.acknowledge();
    }

    @JmsListener(destination = "com.example.mirai.projectname.changerequestservice.bwevent.changenotice")
    @SneakyThrows
    public void processChangeNoticeReconciliation(final Message message) {
        BwEvent bwEvent = convertMessageToBwEvent(message);
        changeRequestSynchronizationService.updateChangeNoticeStatus(bwEvent);
        message.acknowledge();
    }

    @JmsListener(destination = "com.example.mirai.projectname.changerequestservice.releasepackage")
    public void processReleasePackageUpdated(final Message message) throws JMSException {
        ReleasePackage releasePackage = new ReleasePackage(message.getBody(String.class), "release_package", "CHANGEREQUEST");
        changeRequestSynchronizationService.updateReleasePackageStatus(releasePackage);
        message.acknowledge();
    }

    @JmsListener(destination = "com.example.mirai.projectname.changerequestservice.scia")
    public void processSciaUpdated(final Message message) throws JMSException {
        Scia scia = new Scia(message.getBody(String.class), "scia", "CHANGEREQUEST");
        try {
            changeRequestSynchronizationService.updateSciaData(scia);
        } catch(Exception exception) {
            log.info("failed to sync Release Package", exception);
        }
        message.acknowledge();
    }

    @JmsListener(destination = "com.example.mirai.projectname.changerequestservice.bpmevent.action")
    public void processActionUpdated(final Message message) throws JMSException {
        com.example.mirai.projectname.libraries.bpm.BpmEvent bpmEvent = convertMessageToBpmEvent(message);
        changeRequestSynchronizationService.updateActionData(bpmEvent);
        message.acknowledge();
    }

    @JmsListener(destination = "com.example.mirai.projectname.changerequestservice.bwevent.action")
    @SneakyThrows
    public void processActionReconciliation(final Message message) {
        BwEvent bwEvent = convertMessageToBwEvent(message);
        changeRequestSynchronizationService.updateActionData(bwEvent);
        message.acknowledge();
    }

    @JmsListener(destination = "com.example.mirai.projectname.changerequestservice.bpmevent.agendaitem")
    public void processAgendaItemUpdated(final Message message) throws JMSException {
        com.example.mirai.projectname.libraries.bpm.BpmEvent bpmEvent = convertMessageToBpmEvent(message);
        changeRequestSynchronizationService.updateAgendaItemData(bpmEvent);
        message.acknowledge();
    }

    @JmsListener(destination = "com.example.mirai.projectname.changerequestservice.bwevent.agendaitem")
    @SneakyThrows
    public void processAgendaItemReconciliation(final Message message) {
        BwEvent bwEvent = convertMessageToBwEvent(message);
        changeRequestSynchronizationService.updateAgendaItemData(bwEvent);
        message.acknowledge();
    }

    @JmsListener(destination = "com.example.mirai.projectname.changerequestservice.changeobject",
            selector = "entity='com.example.mirai.projectname.impacteditemservice.changeobject.model.aggregate.ChangeObjectAggregate' and type='RECONCILIATION'")
    public void processImpactedItemReconciliation(final Message message) throws JMSException {
        ChangeObject changeObject = new ChangeObject(message.getBody(String.class));
        changeRequestSynchronizationService.updateMyTeamWithImpactedItemMyTeam(changeObject);
        message.acknowledge();
    }

    @JmsListener(destination = "com.example.mirai.projectname.changerequestservice.changeobject",
            selector = "entity='com.example.mirai.projectname.impacteditemservice.changeobject.model.aggregate.ChangeObjectAggregate' and type='CREATE_AGGREGATE'")
    public void processChangeObjectCreated(final Message message) throws JMSException {
        ChangeObject changeObject = new ChangeObject(message.getBody(String.class));
        changeRequestSynchronizationService.updateContextWithChangeObject(changeObject);
        message.acknowledge();
    }


    private com.example.mirai.projectname.libraries.bpm.BpmEvent convertMessageToBpmEvent(Message message) {
        try {
            String xmlString = message.getBody(String.class);
            return new com.example.mirai.projectname.libraries.bpm.BpmEvent(xmlString);
        } catch (JMSException exception) {
        	log.error("JMS exception occurred", exception);
        }
        return null;
    }

    private BwEvent convertMessageToBwEvent(Message message) {
        try {
            String xmlString = message.getBody(String.class);
            return new BwEvent(xmlString);
        } catch (JMSException exception) {
        	log.error("JMS exception occurred", exception);
        }
        return null;
    }
}
